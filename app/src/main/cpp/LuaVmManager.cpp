/**
 * ==============================================================================
 * POCKET ENGINE - IMPLEMENTACIÓN DEL RUNTIME LUA 5.4 PURO (LuaVmManager.cpp)
 * ==============================================================================
 * Implementa la integración directa con la C-API de Lua 5.4.9 y el puente a Rust.
 *
 * Sin wrappers intermediarios: control total sobre la pila (Lua stack),
 * recolección de basura, captura de logs de depuración y enlaces nativos.
 * ==============================================================================
 */

#include "LuaVmManager.h"
#include "RustBridge.h"
#include <android/log.h>
#include <chrono>

#define LOG_TAG "PocketEngineNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Redirección personalizada de la función 'print' de Lua hacia la consola móvil
static int lua_custom_print(lua_State* L) {
    int n = lua_gettop(L);
    std::string line;
    for (int i = 1; i <= n; i++) {
        size_t len = 0;
        const char* s = luaL_tolstring(L, i, &len);
        if (s) {
            if (i > 1) line += "\t";
            line.append(s, len);
        }
        lua_pop(L, 1);
    }
    line += "\n";
    LuaVmManager::getInstance().appendOutput(line);
    LOGI("[Lua 5.4 Out]: %s", line.c_str());
    return 0;
}

// Vinculación: engine.step(dt) -> Invoca la simulación determinista en Rust
static int lua_engine_step(lua_State* L) {
    lua_Number dt = luaL_optnumber(L, 1, 0.016666667);
    uint64_t ticks = pocket_rust_step_simulation(static_cast<float>(dt));
    lua_pushinteger(L, static_cast<lua_Integer>(ticks));
    return 1;
}

// Vinculación: engine.rust_stats() -> Consulta métricas de memoria de Rust
static int lua_engine_rust_stats(lua_State* L) {
    const char* stats = pocket_rust_get_stats();
    if (stats) {
        lua_pushstring(L, stats);
        pocket_rust_free_string(const_cast<char*>(stats));
    } else {
        lua_pushstring(L, "Rust Engine: Inactivo o sin datos");
    }
    return 1;
}

// Vinculación: actor.spawn(id, x, y) -> Crea entidad en Rust
static int lua_actor_spawn(lua_State* L) {
    const char* id = luaL_checkstring(L, 1);
    lua_Number x = luaL_optnumber(L, 2, 0.0);
    lua_Number y = luaL_optnumber(L, 3, 0.0);
    int res = pocket_rust_spawn_actor(id, static_cast<float>(x), static_cast<float>(y));
    lua_pushboolean(L, res == 1);
    return 1;
}

// Vinculación: actor.move(id, dx, dy) -> Mueve entidad en Rust
static int lua_actor_move(lua_State* L) {
    const char* id = luaL_checkstring(L, 1);
    lua_Number dx = luaL_checknumber(L, 2);
    lua_Number dy = luaL_checknumber(L, 3);
    int res = pocket_rust_move_actor(id, static_cast<float>(dx), static_cast<float>(dy));
    lua_pushboolean(L, res == 1);
    return 1;
}

// Vinculación: actor.get_coords(id) -> Retorna x, y desde Rust
static int lua_actor_get_coords(lua_State* L) {
    const char* id = luaL_checkstring(L, 1);
    float x = 0.0f;
    float y = 0.0f;
    if (pocket_rust_get_actor_coords(id, &x, &y) == 1) {
        lua_pushnumber(L, static_cast<lua_Number>(x));
        lua_pushnumber(L, static_cast<lua_Number>(y));
        return 2;
    }
    return 0;
}

LuaVmManager::LuaVmManager() = default;

LuaVmManager::~LuaVmManager() {
    shutdown();
}

LuaVmManager& LuaVmManager::getInstance() {
    static LuaVmManager instance;
    return instance;
}

bool LuaVmManager::init() {
    std::lock_guard<std::mutex> lock(vmMutex);
    if (L != nullptr) {
        lua_close(L);
        L = nullptr;
    }

    L = luaL_newstate();
    if (!L) {
        LOGE("No se pudo crear lua_State (memoria insuficiente)");
        return false;
    }

    // Cargar librerías estándar oficiales de Lua 5.4
    luaL_openlibs(L);

    // Sobrescribir print global con nuestro interceptor
    lua_register(L, "print", lua_custom_print);

    // Registrar módulos nativos del motor
    registerEngineBindings();

    LOGI("Lua 5.4.9 VM inicializada correctamente con librerías estándar.");
    return true;
}

void LuaVmManager::registerEngineBindings() {
    // 1. Tabla global 'engine'
    lua_newtable(L);

    lua_pushcfunction(L, lua_engine_step);
    lua_setfield(L, -2, "step");

    lua_pushcfunction(L, lua_engine_rust_stats);
    lua_setfield(L, -2, "rust_stats");

    lua_setglobal(L, "engine");

    // 2. Tabla global 'actor'
    lua_newtable(L);

    lua_pushcfunction(L, lua_actor_spawn);
    lua_setfield(L, -2, "spawn");

    lua_pushcfunction(L, lua_actor_move);
    lua_setfield(L, -2, "move");

    lua_pushcfunction(L, lua_actor_get_coords);
    lua_setfield(L, -2, "get_coords");

    lua_setglobal(L, "actor");
}

LuaExecutionResult LuaVmManager::executeScript(const std::string& code) {
    std::lock_guard<std::mutex> lock(vmMutex);
    LuaExecutionResult result;
    result.success = false;
    result.executionTimeMs = 0;

    if (!L) {
        result.error = "La Máquina Virtual de Lua 5.4 no está inicializada";
        return result;
    }

    outputBuffer.clear();
    auto start = std::chrono::high_resolution_clock::now();

    // 1. Compilar el código Lua en un chunk protegido
    int loadStatus = luaL_loadstring(L, code.c_str());
    if (loadStatus != LUA_OK) {
        const char* err = lua_tostring(L, -1);
        result.error = (err != nullptr) ? err : "Error de sintaxis desconocido en Lua 5.4";
        lua_pop(L, 1); // Remover mensaje de error de la pila
        result.output = outputBuffer;
        return result;
    }

    // 2. Ejecutar de forma protegida con lua_pcall
    int pcallStatus = lua_pcall(L, 0, LUA_MULTRET, 0);
    auto end = std::chrono::high_resolution_clock::now();
    result.executionTimeMs = static_cast<int>(
        std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count()
    );

    if (pcallStatus != LUA_OK) {
        const char* err = lua_tostring(L, -1);
        result.error = (err != nullptr) ? err : "Error en tiempo de ejecución de Lua";
        lua_pop(L, 1);
        result.output = outputBuffer;
        return result;
    }

    result.success = true;
    result.output = outputBuffer;
    return result;
}

std::string LuaVmManager::getLuaVersionString() const {
    return LUA_RELEASE; // e.g. "Lua 5.4.9"
}

void LuaVmManager::appendOutput(const std::string& text) {
    outputBuffer += text;
}

std::string LuaVmManager::getAndClearOutput() {
    std::string temp = outputBuffer;
    outputBuffer.clear();
    return temp;
}

void LuaVmManager::shutdown() {
    std::lock_guard<std::mutex> lock(vmMutex);
    if (L) {
        lua_close(L);
        L = nullptr;
    }
}
