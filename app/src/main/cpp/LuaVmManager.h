/**
 * ==============================================================================
 * POCKET ENGINE - GESTOR DE LA MÁQUINA VIRTUAL LUA 5.4 (LuaVmManager.h)
 * ==============================================================================
 * Este archivo define el gestor del runtime de Lua puro (sin wrappers externos)
 * basado en la versión oficial más reciente de Lua (5.4.9).
 *
 * Ofrece:
 * 1. Inicialización aislada y segura del estado `lua_State`.
 * 2. Registro de funciones de la API del juego (Motor, Actores, Físicas en Rust).
 * 3. Captura del buffer de salida de `print(...)` para mostrar en la consola móvil.
 * 4. Ejecución protegida (`lua_pcall`) para capturar errores de sintaxis y runtime.
 * ==============================================================================
 */

#ifndef POCKET_ENGINE_LUA_VM_MANAGER_H
#define POCKET_ENGINE_LUA_VM_MANAGER_H

#include <string>
#include <vector>
#include <mutex>

// Inclusión directa de las cabeceras C de Lua 5.4 puro
extern "C" {
#include "lua/lua.h"
#include "lua/lauxlib.h"
#include "lua/lualib.h"
}

struct LuaExecutionResult {
    bool success;
    std::string output;
    std::string error;
    int executionTimeMs;
};

class LuaVmManager {
public:
    static LuaVmManager& getInstance();

    // Inicializa la máquina virtual y registra las APIs del motor
    bool init();

    // Ejecuta un script en Lua puro 5.4 de forma protegida
    LuaExecutionResult executeScript(const std::string& code);

    // Devuelve la versión de Lua compilada
    std::string getLuaVersionString() const;

    // Buffer de salida acumulado
    void appendOutput(const std::string& text);
    std::string getAndClearOutput();

    // Cierra el estado
    void shutdown();

private:
    LuaVmManager();
    ~LuaVmManager();

    // Deshabilita copia y asignación (Singleton)
    LuaVmManager(const LuaVmManager&) = delete;
    LuaVmManager& operator=(const LuaVmManager&) = delete;

    void registerEngineBindings();

    lua_State* L = nullptr;
    std::mutex vmMutex;
    std::string outputBuffer;
};

#endif // POCKET_ENGINE_LUA_VM_MANAGER_H
