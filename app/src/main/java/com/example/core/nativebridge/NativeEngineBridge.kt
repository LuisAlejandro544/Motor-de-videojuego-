package com.example.core.nativebridge

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * ==============================================================================
 * PUENTE NATIVO ENTRE KOTLIN, C++, RUST Y LUA 5.4 PURO (NativeEngineBridge)
 * ==============================================================================
 * Este objeto Singleton es el responsable de:
 * 1. Cargar las librerías dinámicas nativas en tiempo de ejecución:
 *    - `libpocketengine_core.so` (Rust)
 *    - `libpocketengine.so` (C++20 con Lua 5.4.9 embebido)
 * 2. Proporcionar llamadas JNI seguras sin bloquear el hilo principal de la UI.
 * 3. Notificar a la UI el estado de la arquitectura (32 bits o 64 bits), ticks
 *    de física y salida en tiempo real de los scripts de Lua.
 * ==============================================================================
 */
object NativeEngineBridge {
    private const val TAG = "NativeEngineBridge"

    private var isLoaded = false

    private val _engineInfo = MutableStateFlow(NativeEngineInfo())
    val engineInfo: StateFlow<NativeEngineInfo> = _engineInfo.asStateFlow()

    private val _consoleLogs = MutableStateFlow<List<String>>(emptyList())
    val consoleLogs: StateFlow<List<String>> = _consoleLogs.asStateFlow()

    init {
        loadNativeLibraries()
    }

    private fun loadNativeLibraries() {
        try {
            // 1. Cargar el núcleo de Rust primero para resolver símbolos
            System.loadLibrary("pocketengine_core")
            Log.i(TAG, "libpocketengine_core.so (Rust) cargado con éxito.")

            // 2. Cargar la librería C++ con Lua 5.4 y JNI
            System.loadLibrary("pocketengine")
            Log.i(TAG, "libpocketengine.so (C++20 & Lua 5.4) cargado con éxito.")

            isLoaded = true
            initSubsystems()
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Error cargando librerías nativas: ${e.message}", e)
            isLoaded = false
        }
    }

    private fun initSubsystems() {
        if (!isLoaded) return

        try {
            val success = nativeInitEngine()
            if (success) {
                val infoJson = nativeGetEngineInfo()
                parseEngineInfo(infoJson)
                appendLog("[Sistema] C++20, Rust Core y Lua 5.4.9 inicializados.")
            } else {
                appendLog("[Error] Fallo en la inicialización nativa.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción durante initSubsystems: ${e.message}", e)
            appendLog("[Excepción] ${e.message}")
        }
    }

    private fun parseEngineInfo(rawJson: String) {
        try {
            val json = JSONObject(rawJson)
            _engineInfo.value = NativeEngineInfo(
                architecture = json.optString("architecture", "Desconocida"),
                cppStandard = json.optString("cppStandard", "C++20"),
                rustCore = json.optString("rustCore", "Rust Core v0.1.0"),
                luaVersion = json.optString("luaVersion", "Lua 5.4.9"),
                androidNdk = json.optString("androidNdk", "NDK r26b"),
                bitWidth = json.optInt("bitWidth", 64),
                isInitialized = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando info del motor nativo: ${e.message}", e)
        }
    }

    /**
     * Ejecuta código de Lua 5.4 puro de manera asíncrona en el despachador de E/S.
     */
    suspend fun executeLua(code: String): LuaRunResult = withContext(Dispatchers.IO) {
        if (!isLoaded) {
            return@withContext LuaRunResult(
                success = false,
                output = "",
                error = "Librería nativa no cargada en el dispositivo",
                executionTimeMs = 0
            )
        }

        try {
            val rawResult = nativeExecuteLua(code)
            val json = JSONObject(rawResult)
            val success = json.optBoolean("success", false)
            val output = json.optString("output", "")
            val error = json.optString("error", "")
            val timeMs = json.optInt("executionTimeMs", 0)

            if (output.isNotEmpty()) {
                appendLog("[Lua 5.4 Output]: $output")
            }
            if (error.isNotEmpty()) {
                appendLog("[Lua 5.4 Error]: $error")
            }

            LuaRunResult(
                success = success,
                output = output,
                error = error,
                executionTimeMs = timeMs
            )
        } catch (e: Exception) {
            appendLog("[Excepción]: ${e.message}")
            LuaRunResult(
                success = false,
                output = "",
                error = e.message ?: "Error desconocido en ejecución de Lua",
                executionTimeMs = 0
            )
        }
    }

    /**
     * Avanza la simulación física en el núcleo de Rust.
     */
    suspend fun stepSimulation(deltaTime: Float = 0.0166f): Long = withContext(Dispatchers.Default) {
        if (!isLoaded) return@withContext 0L
        try {
            nativeStepSimulation(deltaTime)
        } catch (e: Exception) {
            Log.e(TAG, "Error en stepSimulation: ${e.message}")
            0L
        }
    }

    /**
     * Obtiene el reporte en vivo del motor de Rust.
     */
    fun getRustTelemetry(): String {
        if (!isLoaded) return "Rust Core: Inactivo (Librería no vinculada)"
        return try {
            nativeGetRustStats()
        } catch (e: Exception) {
            "Error consultando Rust: ${e.message}"
        }
    }

    fun clearLogs() {
        _consoleLogs.value = emptyList()
    }

    private fun appendLog(line: String) {
        val current = _consoleLogs.value.toMutableList()
        if (current.size > 200) {
            current.removeAt(0)
        }
        current.add(line)
        _consoleLogs.value = current
    }

    // =========================================================================
    // DECLARACIÓN DE MÉTODOS JNI NATIVOS
    // =========================================================================

    private external fun nativeInitEngine(): Boolean
    private external fun nativeGetEngineInfo(): String
    private external fun nativeExecuteLua(scriptCode: String): String
    private external fun nativeStepSimulation(deltaTime: Float): Long
    private external fun nativeGetRustStats(): String
}
