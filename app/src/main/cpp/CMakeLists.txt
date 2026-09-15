# ==============================================================================
# POCKET ENGINE - CONFIGURACIÓN DE COMPILACIÓN NDK CMAKE (CMakeLists.txt)
# ==============================================================================
# Orquesta la compilación y enlace de los tres lenguajes nativos:
# 1. C++20 (Capa de arquitectura NDK y JNI)
# 2. Lua 5.4.9 Puro (Fuentes oficiales compiladas como biblioteca estática)
# 3. Rust (Núcleo pocketengine_core precompilado para cada ABI de 32/64 bits)
# ==============================================================================

cmake_minimum_required(VERSION 3.22.1)

project("pocketengine" C CXX)

# Habilitar estándar C++20 moderno
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Directorio de cabeceras de Lua 5.4 puro
set(LUA_DIR ${CMAKE_CURRENT_SOURCE_DIR}/lua)
include_directories(${LUA_DIR})
include_directories(${CMAKE_CURRENT_SOURCE_DIR})

# ------------------------------------------------------------------------------
# 1. Compilación de Lua 5.4.9 Oficial (Puro, ANSI C)
# ------------------------------------------------------------------------------
file(GLOB LUA_SOURCES "${LUA_DIR}/*.c")

# Excluir explícitamente los ejecutables independientes CLI si existieran
list(REMOVE_ITEM LUA_SOURCES "${LUA_DIR}/lua.c" "${LUA_DIR}/luac.c")

add_library(lua54 STATIC
    ${LUA_SOURCES}
)

# Definir flags para compatibilidad con Linux/POSIX en Android
target_compile_definitions(lua54 PRIVATE
    LUA_USE_POSIX
    LUA_USE_DLOPEN
)

# ------------------------------------------------------------------------------
# 2. Vinculación con el Núcleo en Rust (pocketengine_core)
# ------------------------------------------------------------------------------
add_library(pocketengine_core SHARED IMPORTED)
set_target_properties(pocketengine_core PROPERTIES
    IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libpocketengine_core.so"
)

# ------------------------------------------------------------------------------
# 3. Biblioteca Compartida Principal de Pocket Engine (libpocketengine.so)
# ------------------------------------------------------------------------------
add_library(pocketengine SHARED
    PocketEngineJni.cpp
    LuaVmManager.cpp
)

# Enlace de librerías del sistema Android, Lua 5.4 estático y Rust compartido
target_link_libraries(pocketengine PRIVATE
    lua54
    pocketengine_core
    log
    android
)
