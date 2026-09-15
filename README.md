# Pocket Engine 🎮⚡

> **Motor de creación y ejecución de videojuegos 2D para dispositivos móviles (Android), diseñado para desarrolladores y creadores que construyen, programan y exportan sus juegos íntegramente desde su teléfono sin necesidad de un ordenador.**

Pocket Engine combina una interfaz táctil ergonómica y modular construida en **Jetpack Compose (Material Design 3)** con un núcleo nativo híbrido de alto rendimiento compuesto por **C++20**, **Rust determinista** y el lenguaje de scripting estándar de la industria **Lua 5.4.9 puro** (compilado directamente desde código fuente ANSI C oficial, sin wrappers).

---

## 🚀 Características Principales

- **Desarrollo Móvil Nativo**: Diseñado para programar desde la pantalla táctil de un smartphone. Sin atajos minimalistas: interfaces ricas en información, pantallas independientes especializadas y navegación fluida.
- **Gestor Profesional de Sprites y Recursos**: Clasificación de sprites por rol de motor (Fondos, Jugador / Player, Iconos, Ítems, Enemigos, Props, HUD y FX). Soporte para importar imágenes directamente desde el **Gestor de Archivos nativo de Android (SAF / Storage Access Framework)** o desde la **Galería de fotos** del teléfono.
- **Vista Previa Temprana a Pantalla Completa (Early Preview)**: El lienzo del juego toma todo el protagonismo visual en pantalla completa, permitiendo experimentar la jugabilidad sin interferencias. El gestor de actores fue desacoplado y reubicado cómodamente en un BottomSheet táctil.
- **Controles Táctiles Virtuales en Pantalla (Virtual Gamepad)**: D-Pad multidireccional con cálculo de inercia y botones de acción (A y B) con respuesta visual táctil para mover al Jugador (Player) y disparar en tiempo real sobre el lienzo.
- **Configurador de Fondos de Escena**: Personalización de fondos mediante gradientes temáticos (Espacio cósmico, Cielo diurno/nocturno, Cyberpunk retro), color sólido o texturas de sprites.
- **Motor de Scripting Lua 5.4 Oficial Puro**: Intérprete oficial de Lua 5.4.9 integrado en C++, con captura de errores en tiempo de ejecución (`lua_pcall`), redirección de consola en vivo y bindings directos a las físicas de Rust.
- **Física y Lógica Determinista en Rust (`pocketengine_core`)**: Simulación determinista sin recolector de basura (cero stuttering), almacenamiento seguro de actores y cálculo de transformaciones a 60/120 FPS.
- **Soporte Multi-Arquitectura Total (32 y 64 bits)**: Binarios compilados y probados para `arm64-v8a`, `armeabi-v7a`, `x86_64` y `x86`.
- **Persistencia Local Robusta con Room**: Base de datos SQLite local para proyectos, actores, capas de sprites y lógica visual, garantizando funcionamiento 100% offline.
- **Preparado para Distribución Independiente**: Pensado para exportación directa de APK para Uptodown, tiendas alternativas y distribución libre sin bloqueos de tiendas cerradas.

---

## 🛠️ Stack Tecnológico

| Capa | Tecnología | Propósito |
|---|---|---|
| **Interfaz de Usuario** | Kotlin + Jetpack Compose + Material Design 3 | UI modular: Hub de proyectos, Editor de escena, Workspace de bloques y Consola Nativa. |
| **Persistencia** | Room Database (SQLite) + KSP | Guardado relacional offline de proyectos, actores, bloques y scripts. |
| **Puente JNI y Rendimiento** | C++20 + Android NDK r26b + CMake 3.22 | Interfaz de bajo nivel entre la máquina virtual Android y las bibliotecas nativas. |
| **Intérprete de Scripting** | Lua 5.4.9 Oficial (ANSI C) | Motor de scripting puro embebido estáticamente, con enlace bidireccional a Rust y Kotlin. |
| **Simulación de Físicas** | Rust 1.80+ (`cdylib` / `cargo-ndk`) | Gestión de estado espacial de actores, bucle de simulación determinista y memoria segura. |

---

## 📂 Arquitectura de Directorios

```text
pocketengine/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── cpp/                     # Código nativo C++ y fuentes de Lua 5.4.9
│   │   │   │   ├── CMakeLists.txt       # Configuración de compilación NDK
│   │   │   │   ├── PocketEngineJni.cpp  # Implementación de endpoints JNI
│   │   │   │   ├── LuaVmManager.h/.cpp  # Gestor del runtime oficial Lua 5.4
│   │   │   │   └── lua/                 # Fuentes ANSI C de Lua 5.4.9 oficial
│   │   │   ├── rust/                    # Núcleo de físicas y estado en Rust
│   │   │   │   ├── Cargo.toml           # Configuración del paquete cdylib
│   │   │   │   └── src/lib.rs           # Simulación determinista y FFI C
│   │   │   ├── jniLibs/                 # Directorio de binarios nativos .so (excluidos de Git via .gitignore)
│   │   │   │   └── <abi>/.gitkeep       # Conserva estructura de carpetas (arm64-v8a, armeabi-v7a, x86, x86_64)
│   │   │   ├── java/com/example/
│   │   │   │   ├── data/                # Room DB, DAOs, Entidades de proyectos y actores
│   │   │   │   ├── nativebridge/        # Wrappers Kotlin del motor nativo (JNI)
│   │   │   │   └── ui/                  # Pantallas modulares de Compose y Navegación
│   │   │   └── AndroidManifest.xml
│   │   └── test/                        # Tests unitarios JVM (Robolectric)
│   └── build.gradle.kts                 # Configuración de CMake, NDK, Room y Compose
├── .github/
│   └── workflows/
│       └── build-debug-apk.yml          # GitHub Action para compilar APK Debug sin caché
├── scripts/
│   └── generate_debug_keystore.sh       # Generador desatendido de firma Debug desde 0
├── AGENTS.md                            # Guía persistente de reglas para asistentes IA
├── AI_CONTEXT.md                        # Contexto técnico de decisiones arquitectónicas
├── ROADMAP.md                           # Hoja de ruta de características futuras
├── STRUCTURE.md                         # Descripción exhaustiva de módulos y archivos
└── README.md                            # Documentación principal del proyecto
```

---

## 🤖 Integración Continua (CI/CD) con GitHub Actions

Para que el usuario pueda compilar y obtener el instalador APK directamente desde su teléfono sin depender de un ordenador propio, el repositorio incluye un flujo de trabajo automatizado en **GitHub Actions** (`.github/workflows/build-debug-apk.yml`):

### Características del Workflow de CI/CD:
1. **Descarga Completa del Código**: Clona el repositorio con todas sus dependencias y submódulos.
2. **Descarga de Toolchains Nativas**:
   - **C++ y Android NDK**: Configura NDK `26.1.10909125`, CMake `3.22.1`, Ninja y herramientas de compilación.
   - **Rust Multi-Arquitectura**: Instala toolchain estable de Rust y añade los 4 targets Android (`aarch64-linux-android`, `armv7-linux-androideabi`, `x86_64-linux-android`, `i686-linux-android`) más `cargo-ndk`.
   - **Lua 5.4 Oficial Puro**: Compilado nativamente por CMake desde las fuentes ANSI C oficiales (`app/src/main/cpp/lua/`), con utilidades `lua5.4` y `liblua5.4-dev` en el runner.
3. **Generación de Firma Debug Forzada Desde 0**:
   - Se ejecuta el script `./scripts/generate_debug_keystore.sh` de forma desatendida.
   - Elimina cualquier keystore anterior y genera una firma RSA 2048-bit válida desde cero sin requerir contraseñas interactivas ni secrets externos.
4. **Compilación Limpia Sin Caché (No Cache)**:
   - Se compila con `gradle assembleDebug --no-build-cache --no-daemon`, evitando conflictos por artefactos desfasados.
5. **Artefacto Listo para Descarga Móvil**:
   - Publica el archivo `PocketEngine-Debug.apk` como artefacto descargable en GitHub, listo para instalar en Android o distribuir en Uptodown.
   - Puede activarse con cada `push`/`pull_request` a `main` o ejecutarse manualmente con un toque desde el móvil mediante `workflow_dispatch`.

### Generación Manual de Firma con Script Shell (`scripts/generate_debug_keystore.sh`)
El script genera de manera autónoma el archivo `debug.keystore`:
```bash
chmod +x ./scripts/generate_debug_keystore.sh
./scripts/generate_debug_keystore.sh
```

---

## 💻 Requisitos Previos y Compilación Local

### Requisitos del Sistema
- **Android SDK**: `minSdk = 24` (Android 7.0 Nougat+), `targetSdk = 35`, `compileSdk = 35`
- **Android NDK**: Versión `26.1.10909125` (r26b)
- **CMake**: `3.22.1`
- **Rust Toolchain**: `rustc 1.80+` con targets `aarch64-linux-android`, `armv7-linux-androideabi`, `x86_64-linux-android`, `i686-linux-android`
- **Java / JDK**: OpenJDK 17 o superior

### Compilar el APK desde terminal
```bash
# Compilar la aplicación completa en modo depuración
gradle :app:assembleDebug

# Ejecutar el conjunto de pruebas unitarias
gradle :app:testDebugUnitTest
```

---

## 🕹️ Ejemplo de Scripting en Lua 5.4 Nativo

En el editor de scripts o en la **Consola Nativa** de Pocket Engine puedes ejecutar directamente código Lua 5.4:

```lua
-- Script del actor: 'act_hero_ship'
print("Iniciando nave principal en Pocket Engine...")

-- Crear y posicionar el actor en el motor de físicas de Rust
local actor_id = "hero_ship"
actor.spawn(actor_id, 100.0, 250.0)

-- Aplicar un desplazamiento y avanzar la simulación física
actor.move(actor_id, 15.5, -5.0)
engine.step(0.016) -- 1 tick a 60 FPS

-- Consultar coordenadas sincronizadas desde Rust
local x, y = actor.get_coords(actor_id)
print(string.format("Nave posicionada en: X=%.2f, Y=%.2f", x, y))
print("Telemetría Rust: " .. engine.rust_stats())
```

---

## 📱 Flujo de Usuario en Teléfono Móvil

1. **Hub de Proyectos**: Explora proyectos existentes con previsualizaciones gráficas, crea nuevos juegos eligiendo plantillas y orientación (horizontal/vertical).
2. **Editor de Escena**: Diseña el nivel, posiciona actores en pantalla táctil con zoom y rejilla, y gestiona capas de sprites.
3. **Workspace de Bloques y Scripts**: Programa comportamientos visuales por bloques o escribe lógica avanzada en Lua 5.4.
4. **Consola y Monitor Nativo**: Inspecciona la arquitectura activa del procesador (32 vs 64 bits), ejecuta diagnósticos de ticks de Rust y prueba scripts interactivos de Lua en tiempo real.
