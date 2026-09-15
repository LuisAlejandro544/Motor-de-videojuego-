# Hoja de Ruta de Desarrollo (Roadmap) 🗺️

Este documento describe el plan estratégico de evolución de **Pocket Engine**, enfocado en proporcionar una experiencia de desarrollo de videojuegos 2D completa y autosuficiente desde un teléfono móvil Android.

---

## 📍 Fase 1: Arquitectura Base y Núcleo Multi-Lenguaje (Completada ✅)

- [x] **Arquitectura Multi-Lenguaje Integrada**:
  - Enlace JNI robusto en C++20 con soporte para CMake 3.22 y NDK r26b.
  - Compilación e integración estática de **Lua 5.4.9 oficial en ANSI C puro**, sin wrappers de terceros, con captura segura de errores mediante `lua_pcall`.
  - Módulo nativo en **Rust (`pocketengine_core`)** para simulación de físicas deterministas y gestión de memoria segura a cero costo.
- [x] **Soporte Multi-Arquitectura Total**:
  - Binarios nativos precompilados y compatibles con `arm64-v8a`, `armeabi-v7a` (32 bits), `x86_64` y `x86`.
- [x] **Capa de Persistencia Local**:
  - Base de datos Room SQLite para gestión 100% offline de proyectos, actores, scripts y variables.
- [x] **Interfaz Modular en Jetpack Compose**:
  - Pantallas separadas e interconectadas: Hub de Proyectos, Editor de Escena, Workspace de Bloques y Consola Nativa del Motor.
  - Diseño Material Design 3 no-minimalista con alta densidad visual y controles ergonómicos para dedos móviles.

---

## 📍 Fase 2: Editor de Escenas Táctil, Canvas de Renderizado 2D y Gestor de Sprites (Fase Activa 🔄)

- [x] **Gestor Profesional de Sprites y Recursos**:
  - Clasificación por categorías de motor de juegos: Fondo (`Background`), Jugador (`Player`), Iconos (`Icons`), Ítems coleccionables (`Items`), Enemigos (`Enemy`), Escenarios/Props (`Prop`), Interfaz (`UI/HUD`) y Efectos (`FX`).
  - Importación dual de imágenes: Integración con el **Gestor de Archivos nativo de Android** vía Storage Access Framework (`ActivityResultContracts.GetContent`) y la **Galería de fotos** (`PickVisualMedia`).
- [x] **Early Preview a Pantalla Completa**:
  - Modo inmersivo de pantalla completa para probar la escena y los actores sin elementos visuales que estorben.
  - Reubicación ergonómica del gestor de actores en un `ModalBottomSheet` táctil accesible con un toque.
- [x] **Controles Virtuales en Pantalla (Virtual Gamepad)**:
  - D-Pad táctil continuo (Arriba, Abajo, Izquierda, Derecha) con inercia configurable.
  - Botones de acción táctiles (A: Disparo/Acción, B: Turbo/Salto) con feedback visual y háptico.
  - Control en vivo del actor designado como **Player** en el lienzo 2D.
- [x] **Configurador de Fondo de Escenario**:
  - Modos de fondo dinámicos: Gradiente espacial cósmico, cielo atmosférico, cyberpunk neón, colores sólidos y texturas de sprites.
  - Alternador de cuadrícula (*Grid*) y niveles de zoom (0.8x a 1.5x).
- [ ] **Viewport 2D con Gestos Táctiles Avanzados**:
  - Transformaciones fluidas de pan y pinch-to-zoom multi-touch en el canvas.
- [ ] **Editor de Sprites Avanzado y Tilemaps**:
  - Importador de hojas de sprites (*sprite sheets*) con corte automático por cuadrícula.
  - Paleta de baldosas (*tilesets*) para dibujar terrenos arrastrando el dedo.

---

## 📍 Fase 3: Audio de Baja Latencia y Sistema de Partículas (Próximamente ⏳)

- [ ] **Motor de Audio Nativo en C++ (Oboe)**:
  - Integración de audio de ultra-baja latencia para dispositivos móviles mediante OpenSL ES / AAudio (Oboe).
  - Reproducción simultánea de múltiples canales de efectos de sonido (SFX) y música en bucle sin pausas.
- [ ] **Simulación de Partículas en Rust**:
  - Emisores de partículas 2D (humo, fuego, chispas, explosiones) simuladas en Rust con cálculo vectorizado.
  - Transferencia de matrices de partículas hacia el buffer de dibujado sin pasar por el recolector de basura de Java/Kotlin.
- [ ] **Depuración Visual de Colisiones**:
  - Modo *wireframe* para visualizar cajas de colisión (*AABB* y círculos delimitadores) calculados por Rust.

---

## 📍 Fase 4: Exportador Autónomo de APK para Dispositivos Móviles (Hito Clave 🚀)

- [x] **Compilación de APK Debug en la nube sin PC (GitHub Actions CI/CD)**:
  - Workflow automatizado `.github/workflows/build-debug-apk.yml` que descarga toolchains nativas completas (C++, NDK, CMake, Rust y Lua 5.4).
  - Compilación limpia de APK Debug sin caché (`--no-build-cache --no-daemon`) para evitar artefactos residuales.
  - Generación desatendida forzada de firma `debug.keystore` desde cero mediante script `scripts/generate_debug_keystore.sh`.
  - Publicación y entrega de artefactos `.apk` descargables directamente en el teléfono del desarrollador para Uptodown o sideloading.
- [ ] **Generación de APK en el propio Teléfono (On-Device)**:
  - Empaquetador móvil que une el runtime nativo precompilado de Pocket Engine con los assets y scripts del proyecto del usuario.
  - Generación directa del archivo `.apk` instalable en la memoria del teléfono sin necesidad de conectarse a una PC ni a servidores en la nube.
- [ ] **Compatibilidad con Tiendas de Terceros**:
  - Salida limpia y optimizada lista para subir a **Uptodown**, repositorios independientes o distribuir vía APK directo.
  - Cumplimiento estricto con el principio de soberanía del usuario: cero dependencias propietarias o telemetría forzada.


---

## 📍 Fase 5: Ecosistema y Plantillas Avanzadas (Futuro 🔮)

- [ ] **Paquetes de Plantillas de Géneros**:
  - *Platformer 2D* (físicas de salto, plataformas móviles y enemigos patrulla).
  - *Top-Down Action / RPG* (movimiento en 8 direcciones, inventario básico y diálogos).
  - *Infinite Runner* (generación procedural de obstáculos en Lua).
- [ ] **Exportador/Importador de Proyectos Comprimidos (`.pocketgame`)**:
  - Facilidad para compartir proyectos entre creadores móviles mediante Bluetooth, mensajería o almacenamiento local.
