# Estructura del Proyecto (Architecture & Structure) 🏗️

Este documento describe la organización modular de **Pocket Engine**, detallando la responsabilidad de cada paquete, módulo y archivo en el sistema para garantizar un desarrollo escalable y robusto.

---

## 📐 Diagrama de Comunicación Entre Capas

```text
┌─────────────────────────────────────────────────────────────────────────┐
│                       CAPA DE PRESENTACIÓN (UI)                         │
│   Jetpack Compose (Material 3) • Pantallas Independientes Modulares    │
│   [ProjectsScreen]  [ProjectEditorScreen]  [BlocksScreen]  [EngineConsole]│
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ Llamadas de usuario / Eventos UI
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      CAPA DE ENLACE KOTLIN (BRIDGE)                     │
│               NativeEngineBridge.kt  (System.loadLibrary)                │
│    Carga de: libpocketengine_core.so (Rust) y libpocketengine.so (C++)  │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ JNI (Java Native Interface)
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         CAPA C++20 & LUA 5.4 NATIVO                     │
│          PocketEngineJni.cpp • LuaVmManager.cpp • Fuentes ANSI C        │
│    - Intérprete oficial Lua 5.4.9 (C puro, ejecución protegida lua_pcall) │
│    - Registro de funciones globales: engine.* y actor.*                 │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ FFI C bidireccional (extern "C")
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      CAPA DE FÍSICA Y ESTADO EN RUST                    │
│            pocketengine_core (lib.rs) • Memoria Segura Sin GC           │
│    - Deterministic physics loop (ticks fijos dt)                        │
│    - Almacenamiento espacial seguro de actores (HashMap con Mutex)       │
│    - Compilado para 64-bit (arm64, x86_64) y 32-bit (armv7, x86)        │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 🗂️ Árbol Detallado de Directorios

### 1. Núcleo C++ y Lua (`app/src/main/cpp/`)
Contiene la implementación en C++20 y los fuentes en C de Lua 5.4.9 que se compilan con el Android NDK vía CMake:
- **`CMakeLists.txt`**: Define el proyecto nativo, flags de compilación C++20 (`-O3`, `-fexceptions`), inclusión estática de los archivos fuente de Lua y enlace dinámico con la librería de Rust (`pocketengine_core`) y librerías del sistema Android (`libandroid.so`, `liblog.so`).
- **`PocketEngineJni.cpp`**: Endpoints JNI que exponen las funciones nativas a Kotlin: inicialización del motor, ejecución de scripts en Lua, avance de ticks físicos, spawn de actores y lectura de telemetría de memoria.
- **`LuaVmManager.h` / `LuaVmManager.cpp`**: Controlador del estado de Lua (`lua_State`). Implementa apertura segura de librerías estándar de Lua (`base`, `string`, `table`, `math`, etc.), registro de llamadas nativas a Rust, captura de errores de sintaxis y runtime sin crashear el proceso Android, y captura de la salida de `print()` a través de un buffer dinámico.
- **`lua/`**: Fuentes oficiales y puros en ANSI C de **Lua 5.4.9** (`lua.h`, `lauxlib.h`, `lualib.h`, `lapi.c`, `lvm.c`, `ldo.c`, etc.), compilados directamente sin intermediarios.

---

### 2. Núcleo en Rust (`app/src/main/rust/`)
Biblioteca de físicas y gestión de actores sin recolector de basura:
- **`Cargo.toml`**: Configuración del paquete Rust `pocketengine_core` como tipo `cdylib`, con optimizaciones de tamaño y velocidad para móviles (`opt-level = 3`, `lto = true`).
- **`src/lib.rs`**: Contiene la lógica del motor determinista:
  - Estructuras `ActorState` y `EnginePhysicsWorld` protegidas por `Mutex`.
  - Bucle de física con paso de tiempo configurable (`step(dt)`).
  - API externa C (`extern "C"`) para interoperar limpiamente con C++ y Lua.

---

### 3. Binarios NDK Compilados (`app/src/main/jniLibs/`)
Estructura de directorios organizada por ABI para máxima portabilidad en teléfonos antiguos y modernos. **Nota de Repositorio Público**: Los archivos binarios compilados `.so` están excluidos del control de versiones mediante `.gitignore` para mantener el repositorio público ligero y transparente; se generan automáticamente bajo demanda en CI/CD o compilación local desde el código fuente Rust (`app/src/main/rust`) mediante `cargo ndk` y CMake:
- **`arm64-v8a/`**: Para procesadores móviles modernos de 64 bits (la mayoría de teléfonos Android actuales).
- **`armeabi-v7a/`**: Para teléfonos móviles de 32 bits, garantizando compatibilidad con dispositivos de entrada y gama baja.
- **`x86_64/`**: Para emuladores y dispositivos Android de 64 bits con arquitectura Intel/AMD.
- **`x86/`**: Para emuladores y dispositivos Android de 32 bits basados en Intel.


---

### 4. Capa de Aplicación Kotlin (`app/src/main/java/com/example/`)
Organizada siguiendo una arquitectura modular y desacoplada para evitar saturar la memoria del teléfono:
- **`nativebridge/NativeEngineBridge.kt`**: Objeto singleton que realiza la carga segura de las librerías nativas (`libpocketengine_core.so` y `libpocketengine.so`), exponiendo métodos Kotlin tipados y seguros ante excepciones.
- **`data/`**:
  - `PocketEngineDatabase.kt`: Instancia Room Database que almacena datos de forma local y offline.
  - `dao/`: Objetos de acceso a datos para operaciones CRUD de proyectos y actores.
  - `model/`: Entidades de datos (`ProjectEntity`, `ActorEntity`, `BlockScriptEntity`).
- **`ui/`**:
  - `navigation/AppNavigation.kt`: Enrutador tipo-seguro que gestiona el historial y la transición entre pantallas independientes.
  - `screens/ProjectsScreen.kt`: Pantalla del catálogo y gestión de proyectos con estadísticas, banners y accesos directos.
  - `screens/ProjectEditorScreen.kt`: Pantalla del motor de juego y Early Preview a Pantalla Completa, con controles virtuales integrados, gestión de fondo y gestor de actores desacoplado en BottomSheet.
  - `screens/AssetsScreen.kt`: Pantalla especializada de Gestor de Sprites y Recursos con clasificación por categorías (Fondos, Player, Iconos, Ítems, etc.) e importación desde Storage Access Framework (SAF) y Galería.
  - `screens/BlocksScreen.kt`: Espacio de programación visual basado en bloques con paleta de categorías y área de arrastre.
  - `screens/EngineConsoleScreen.kt`: Consola dedicada que expone diagnósticos de 32/64 bits, simulación de ticks en Rust y editor de scripts Lua 5.4.9 con consola interactiva en tiempo real.
  - `components/VirtualGamepad.kt`: Gamepad virtual táctil con D-Pad continuo de 4 direcciones y botones de acción (A y B) con inercia para mover al Player.
  - `components/SpriteComponents.kt`: Componentes reutilizables de visualización y filtrado de sprites, badges por rol y diálogo dual de importación (Gestor nativo SAF + Galería).
  - `components/BackgroundConfigDialog.kt`: Selector y configurador visual de fondos de escena (gradientes, sólidos o texturas).
  - `components/SceneComponents.kt`: Renderizador 2D `SceneCanvasView` con zoom, cuadrícula, renderizado de fondos dinámicos y halo visual del Player.
  - `theme/`: Definición centralizada de colores, tipografía y formas Material Design 3.

---

### 5. Suite de Pruebas Unitarias (`app/src/test/java/com/example/`)
- **`SpriteManagerTest.kt`**: Pruebas unitarias de roles de motor de sprites, límites de movimiento del Player con Gamepad, compatibilidad de URIs SAF y persistencia de configuración de fondo.
- **`ExampleUnitTest.kt`**: Comprobaciones base de ejecución JVM.

---

### 6. Configuración de Build (`app/build.gradle.kts` y `settings.gradle.kts`)
- Integra CMake con el Android NDK.
- Configura empaquetado de librerías `jniLibs`.
- Declara dependencias oficiales (Room, Compose M3, Corrutinas, Navigation).
- Configura `minSdk = 24`, `targetSdk = 35`, habilitando compatibilidad amplia sin sacrificar las APIs más modernas.

---

### 7. Automatización CI/CD y Generación de Firmas (`.github/` y `scripts/`)
- **`.github/workflows/build-debug-apk.yml`**: Flujo de GitHub Actions que clona el proyecto, instala las toolchains nativas de C++, Android NDK, CMake, Rust y Lua 5.4, ejecuta la generación forzada de firma con el script `.sh`, y compila un APK Debug completamente limpio sin caché (`--no-build-cache --no-daemon`) para descarga directa en teléfonos móviles y Uptodown.
- **`scripts/generate_debug_keystore.sh`**: Script bash ejecutable que genera un par de claves RSA 2048-bit `debug.keystore` de forma desatendida y limpia desde cero, sin requerir contraseñas interactivas ni secrets externos preexistentes.

