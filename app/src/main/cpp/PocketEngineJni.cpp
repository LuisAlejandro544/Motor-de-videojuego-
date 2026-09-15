/**
 * ==============================================================================
 * POCKET ENGINE - CAPA JNI NATIVA C++ / RUST / LUA (PocketEngineJni.cpp)
 * ==============================================================================
 * Este archivo implementa los enlaces JNI (Java Native Interface) entre la capa
 * de interfaz en Kotlin (Jetpack Compose) y los tres pilares nativos del motor:
 * 1. C++20 (Renderizado y arquitectura NDK)
 * 2. Rust (Simulación física determinista y memoria segura)
 * 3. Lua 5.4.9 puro (Motor de scripts e interpretación de bloques)
 *
 * Detecta dinámicamente arquitecturas de 32 bits y 64 bits para garantizar
 * estabilidad en cualquier dispositivo móvil Android.
 * ==============================================================================
 */

#include <jni.h>
#include <string>
#include <sstream>
#include <android/log.h>
#include "RustBridge.h"
#include "LuaVmManager.h"

#define LOG_TAG "PocketEngineJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Determinar en tiempo de compilación la arquitectura del binario Android
static const char* getTargetArchitecture() {
#if defined(__aarch64__)
    return "arm64-v8a (64-bit ARM)";
#elif defined(__arm__)
    return "armeabi-v7a (32-bit ARM)";
#elif defined(__x86_64__)
    return "x86_64 (64-bit x86)";
#elif defined(__i386__)
    return "x86 (32-bit x86)";
#else
    return "Unknown Architecture";
#endif
}

// Escapar caracteres para JSON seguro
static std::string escapeJsonString(const std::string& input) {
    std::ostringstream ss;
    for (char c : input) {
        switch (c) {
            case '"': ss << "\\\""; break;
            case '\\': ss << "\\\\"; break;
            case '\b': ss << "\\b"; break;
            case '\f': ss << "\\f"; break;
            case '\n': ss << "\\n"; break;
            case '\r': ss << "\\r"; break;
            case '\t': ss << "\\t"; break;
            default:
                if ('\x00' <= c && c <= '\x1f') {
                    // Caracter de control
                    ss << "\\u00" << std::hex << (int)c;
                } else {
                    ss << c;
                }
        }
    }
    return ss.str();
}

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_example_core_nativebridge_NativeEngineBridge_nativeInitEngine(
        JNIEnv* env,
        jobject /* this */
) {
    LOGI("Inicializando subsistemas nativos de PocketEngine...");

    // 1. Inicializar Rust Core
    int32_t rustOk = pocket_rust_init();
    if (!rustOk) {
        LOGE("Fallo al inicializar el núcleo Rust");
        return JNI_FALSE;
    }

    // 2. Inicializar Lua 5.4.9 Runtime puro
    bool luaOk = LuaVmManager::getInstance().init();
    if (!luaOk) {
        LOGE("Fallo al inicializar la máquina virtual de Lua 5.4");
        return JNI_FALSE;
    }

    LOGI("Subsistemas Rust, C++ y Lua 5.4 inicializados con éxito.");
    return JNI_TRUE;
}

JNIEXPORT jstring JNICALL
Java_com_example_core_nativebridge_NativeEngineBridge_nativeGetEngineInfo(
        JNIEnv* env,
        jobject /* this */
) {
    const char* arch = getTargetArchitecture();
    const char* rustVer = pocket_rust_version();
    std::string luaVer = LuaVmManager::getInstance().getLuaVersionString();

    std::ostringstream json;
    json << "{"
         << "\"architecture\": \"" << arch << "\","
         << "\"cppStandard\": \"C++20\","
         << "\"rustCore\": \"" << (rustVer ? rustVer : "Desconocido") << "\","
         << "\"luaVersion\": \"" << luaVer << " (Puro, sin wrappers)\","
         << "\"androidNdk\": \"NDK r26b\","
         << "\"bitWidth\": " << (sizeof(void*) * 8)
         << "}";

    return env->NewStringUTF(json.str().c_str());
}

JNIEXPORT jstring JNICALL
Java_com_example_core_nativebridge_NativeEngineBridge_nativeExecuteLua(
        JNIEnv* env,
        jobject /* this */,
        jstring scriptCode
) {
    if (scriptCode == nullptr) {
        return env->NewStringUTF("{\"success\":false,\"error\":\"Script nulo\",\"output\":\"\"}");
    }

    const char* codeChars = env->GetStringUTFChars(scriptCode, nullptr);
    std::string code(codeChars);
    env->ReleaseStringUTFChars(scriptCode, codeChars);

    LuaExecutionResult result = LuaVmManager::getInstance().executeScript(code);

    std::ostringstream json;
    json << "{"
         << "\"success\": " << (result.success ? "true" : "false") << ","
         << "\"output\": \"" << escapeJsonString(result.output) << "\","
         << "\"error\": \"" << escapeJsonString(result.error) << "\","
         << "\"executionTimeMs\": " << result.executionTimeMs
         << "}";

    return env->NewStringUTF(json.str().c_str());
}

JNIEXPORT jlong JNICALL
Java_com_example_core_nativebridge_NativeEngineBridge_nativeStepSimulation(
        JNIEnv* env,
        jobject /* this */,
        jfloat deltaTime
) {
    uint64_t ticks = pocket_rust_step_simulation(deltaTime);
    return static_cast<jlong>(ticks);
}

JNIEXPORT jstring JNICALL
Java_com_example_core_nativebridge_NativeEngineBridge_nativeGetRustStats(
        JNIEnv* env,
        jobject /* this */
) {
    const char* stats = pocket_rust_get_stats();
    if (stats) {
        jstring result = env->NewStringUTF(stats);
        pocket_rust_free_string(const_cast<char*>(stats));
        return result;
    }
    return env->NewStringUTF("Rust Core: No disponible");
}

} // extern "C"
