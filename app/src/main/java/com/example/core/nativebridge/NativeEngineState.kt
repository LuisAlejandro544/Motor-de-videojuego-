package com.example.core.nativebridge

/**
 * Modelos de datos para el estado y telemetría de los tres motores nativos:
 * 1. C++20 (Render y NDK)
 * 2. Rust (Simulación física determinista y memoria segura)
 * 3. Lua 5.4.9 (Intérprete de scripts de alto nivel)
 */
data class NativeEngineInfo(
    val architecture: String = "Detectando...",
    val cppStandard: String = "C++20",
    val rustCore: String = "Rust Core v0.1.0",
    val luaVersion: String = "Lua 5.4.9 (Puro)",
    val androidNdk: String = "NDK r26b",
    val bitWidth: Int = 64,
    val isInitialized: Boolean = false
)

/**
 * Resultado de una ejecución de script en la máquina virtual de Lua 5.4
 */
data class LuaRunResult(
    val success: Boolean,
    val output: String,
    val error: String,
    val executionTimeMs: Int
)

/**
 * Métrica individual de rendimiento o estado para el dashboard
 */
data class EngineMetric(
    val label: String,
    val value: String,
    val detail: String
)
