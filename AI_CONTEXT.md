# Contexto del Proyecto para Inteligencia Artificial (AI_CONTEXT.md) 🧠

Este documento almacena el contexto crítico, las restricciones del usuario y las directrices arquitectónicas que cualquier modelo o agente de IA debe conocer antes de proponer cambios o escribir código en **Pocket Engine**.

---

## 👤 Perfil del Usuario y Filosofía del Proyecto

1. **Desarrollo Exclusivamente Móvil (Sin PC)**:
   - El usuario programa, diseña y prueba sus juegos directamente desde su teléfono Android.
   - Toda herramienta, pantalla y flujo de trabajo debe estar optimizado para pantallas táctiles verticales y horizontales, con áreas interactivas accesibles para los dedos (mínimo 48dp).

2. **Distribución Independiente (Uptodown y Tiendas de Terceros)**:
   - El APK final se distribuirá en **Uptodown** o canales de terceros, **NO** exclusivamente en Google Play.
   - No asumir servicios o APIs restrictivas que dependan de los Servicios de Google Play cerrados o dependencias bloqueantes de cuentas propietarias.
   - Las políticas de exportación deben priorizar la generación limpia de archivos `.apk` autónomos.

3. **Rechazo al Minimalismo Extremo**:
   - Al usuario **NO** le agrada el minimalismo vacío o estéril.
   - Prefiere interfaces completas, con densidad informativa visual, barras de herramientas especializadas, indicadores de estado, datos de telemetría y paletas ricas en color y contraste.

4. **Diseño Multi-Pantalla Modular (Nunca Pantalla Única Saturada)**:
   - Está terminantemente prohibido amontonar todas las herramientas en una sola pantalla colapsada.
   - Cada área de trabajo tiene su propia pantalla dedicada accesible mediante botones y navegación clara:
     - Pantalla de Gestión de Proyectos (`ProjectsScreen`).
     - Pantalla de Edición de Escena y Early Preview a Pantalla Completa (`ProjectEditorScreen`).
     - Pantalla de Gestor de Sprites y Recursos (`AssetsScreen`).
     - Pantalla de Workspace de Bloques (`BlocksScreen`).
     - Pantalla de Consola y Diagnóstico Nativo (`EngineConsoleScreen`).

5. **Gestor de Sprites y Almacenamiento Nativo (SAF + Galería)**:
   - Los sprites deben estar clasificados por roles de motor de videojuegos: Fondo (`Background`), Jugador (`Player`), Iconos (`Icons`), Ítems coleccionables (`Items`), Enemigos (`Enemy`), Props/Escenario (`Prop`), HUD/UI (`UI_HUD`) y Efectos (`FX`).
   - Soporte para importar imágenes desde el **Gestor de Archivos nativo de Android** (`ActivityResultContracts.GetContent`) y la **Galería de fotos** (`ActivityResultContracts.PickVisualMedia`).
   - El actor designado como `Player` debe responder directamente a los controles táctiles en pantalla (`VirtualGamepad`) con D-Pad y botones A/B.

6. **Prioridad Absoluta a la Funcionalidad Real Sobre el Peso del APK**:
   - Al usuario no le preocupa el peso final del APK siempre y cuando las dependencias sean 100% funcionales, reales y estables.
   - **Siempre usar dependencias reales** y comprobadas; evitar inventar soluciones "sin dependencias" que aumenten la fragilidad del código.

7. **Restricción de Licencias**:
   - Nunca agregar bibliotecas con licencias restrictivas (como GPL viral o licencias que obliguen a abrir el código comercial o requieran atribuciones intrusivas forzadas en la interfaz del usuario).

8. **Soberanía y Seguridad**:
   - **NUNCA** usar propiedades de sistema protegidas o riesgosas como `persist.sys.*`.
   - **Evitar nombrar marcas comerciales protegidas** por derechos de autor en paquetes, recursos o identificadores que puedan poner en riesgo al usuario.

9. **Exclusión de Binarios .so en Repositorio Público**:
   - Para mantener el repositorio público ligero, transparente y seguro, **los archivos `.so` quedan excluidos de Git** a través de `.gitignore` (`*.so`, `**/*.so`, `app/src/main/jniLibs/**/*.so`).
   - El código fuente en Rust (`app/src/main/rust`) y C++/Lua (`app/src/main/cpp`) se compila para las 4 arquitecturas Android (`arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`) dinámicamente tanto en GitHub Actions como en compilaciones locales.


---

## ⚙️ Arquitectura Multi-Lenguaje Obligatoria

Pocket Engine utiliza una arquitectura en tres niveles nativos que **NUNCA** debe reemplazarse por soluciones simuladas en Kotlin puro:

1. **Lua 5.4 Oficial Puro (ANSI C)**:
   - Versión 5.4.9 compilada desde las fuentes oficiales del proyecto Lua.
   - **Cero wrappers externos**: La gestión se realiza mediante llamadas directas a la API ANSI C (`lua_pcall`, `luaL_newstate`, `luaL_openlibs`, etc.) dentro de `LuaVmManager.cpp`.
   - Se deben capturar errores de sintaxis y runtime para mostrarlos en la consola visual sin abortar la aplicación móvil.

2. **Núcleo de Físicas y Estado en Rust (`pocketengine_core`)**:
   - Escrito en Rust determinista y exportado como `cdylib`.
   - Se compila e incluye para las cuatro arquitecturas Android: **64 bits** (`arm64-v8a`, `x86_64`) y **32 bits** (`armeabi-v7a`, `x86`).
   - Mantiene la simulación espacial de actores libre de pausas por recolección de basura.

3. **Capa C++20 y CMake**:
   - El archivo `CMakeLists.txt` en `app/src/main/cpp` compila las fuentes de Lua y el enlace JNI con el NDK r26b.
   - Si se requiere añadir lógica nativa, debe incorporarse en C++20 o Rust y enlazarse en Gradle/CMake sin saltearse pasos de compilación.

---

## 🛡️ Estándar de Código y Modularidad

- **Comentarios y Documentación en Código**: Cada archivo nuevo de Kotlin, C++ o Rust debe incluir una cabecera explicativa que detalle la responsabilidad del componente y la lógica principal contenida.
- **Versión Mínima de Android**: Mantener `minSdk = 24` para que funcione en una base masiva de dispositivos móviles sin requerir actualizaciones forzadas de hardware.
- **Manejo de Errores Defensivo**: En entornos móviles, los recursos de memoria y CPU son compartidos. Emplear manejo de excepciones estructurado (`try/catch` en Kotlin, `Result` en Rust, comprobación de punteros y códigos de estado en C++ y Lua).

---

## 🚀 Integración Continua (CI/CD) y Generación de APK en la Nube

- **Autonomía Móvil Total**: Dado que el usuario no tiene PC, las compilaciones de APK se realizan en la nube mediante **GitHub Actions** (`.github/workflows/build-debug-apk.yml`), permitiendo descargar el APK directamente al móvil.
- **Compilación Limpia Sin Caché**: El workflow compila con `--no-build-cache --no-daemon` y sin cachés de Gradle/Cargo para garantizar reproducibilidad exacta y cero desincronización de binarios nativos (C++, Rust, Lua).
- **Generación Forzada de Firma Debug**: Se utiliza el script `scripts/generate_debug_keystore.sh` para crear una firma `debug.keystore` válida de forma 100% desatendida y desde cero en el entorno de build, eliminando la necesidad de configurar secrets o certificados manuales.

