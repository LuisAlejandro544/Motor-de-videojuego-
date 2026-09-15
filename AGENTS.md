# Reglas e Instrucciones Persistentes para Agentes IA (AGENTS.md) 🤖

Este archivo define las reglas de comportamiento, razonamiento y desarrollo que **todos los agentes de Inteligencia Artificial** deben cumplir al trabajar en el código base de **Pocket Engine**.

---

## 🧭 Flujo de Razonamiento Obligatorio (Pensar Antes de Actuar)

1. **Razonar Siempre Antes de Modificar**:
   - Antes de ejecutar cualquier herramienta de edición o comando, realiza un análisis interno de las implicaciones del cambio.
   - Identifica con precisión qué archivos se verán afectados, qué arquitecturas nativas (32 y 64 bits) intervienen y qué dependencias deben respetarse.
   - Nunca emitas respuestas directas apresuradas sin haber razonado la arquitectura subyacente.

2. **Economía de Contexto y Consulta**:
   - **No revises archivos que no sean estrictamente necesarios** para la tarea actual. Mantén la interacción ágil y enfocada únicamente en el alcance solicitado.

3. **Ciclo de Desarrollo por Roles**:
   - **El Arquitecto**: Diseña la estructura, el modelo de datos y las justificaciones técnicas antes de cambios estructurales mayores.
   - **El Constructor**: Escribe código robusto, completo, tipado y listo para producción, nunca fragmentos incompletos con "TODO" o "añade tu lógica aquí".
   - **El Detective**: Diagnostica fallos mediante análisis metódico de causa raíz (Chain of Thought), no aplicando parches a ciegas.
   - **El Crítico**: Valida seguridad, eficiencia y legibilidad en cada componente modificado.
   - **El Optimizador**: Refactoriza preservando intacto el comportamiento externo.
   - **El Escudo**: Asegura la estabilidad mediante pruebas unitarias verificadas con `gradle :app:testDebugUnitTest`.
   - **El Narrador**: Mantiene la documentación técnica al día con explicaciones claras en español.

---

## 🚨 Reglas Críticas del Proyecto (Mandatorias)

### 1. El Usuario Trabaja Exclusivamente en Teléfono Móvil
- El usuario **no tiene un ordenador personal (PC)**.
- Cualquier solución, instrucción o herramienta debe ser utilizable y operable directamente desde su teléfono Android.
- No proporciones comandos que requieran ejecutar software de escritorio externo.

### 2. Distribución en Uptodown y Tiendas de Terceros
- La aplicación se distribuirá en **Uptodown** o como APK independiente.
- No diseñes arquitecturas que dependan de forma obligatoria de Google Play Services o flujos propietarios que restrinjan la portabilidad del APK.

### 3. Cero Propiedades `persist.sys.*`
- Queda **estrictamente prohibido** utilizar o invocar propiedades del sistema como `persist.sys.*` en cualquier módulo, optimizador o lógica de rendimiento.

### 4. Gestión del archivo `commit_message.txt`
- Si existe un archivo `commit_message.txt`, asegúrate de que toda su información esté redactada en **español**.
- **No actualices ni modifiques** dicho archivo a menos que el usuario lo solicite expresamente.

### 5. Uso de Dependencias Reales y Funcionales
- Al usuario no le preocupa el peso final del archivo APK.
- **Evita implementar soluciones caseras o "sin dependencias"** si existe una librería oficial, estable y mantenida que resuelva la necesidad de forma segura.
- Usa siempre dependencias 100% funcionales.

### 6. Protección de Propiedad Intelectual y Nombres
- **Nunca nombres archivos, variables o identificadores** utilizando marcas comerciales protegidas por derechos de autor que puedan comprometer legalmente al usuario o su proyecto.

### 7. Integridad Absoluta de C++, Rust y Lua
- Cuando el proyecto involucre C++, Rust o Lua, estos **deben estar plenamente integrados en el proceso de compilación de Gradle y CMake**.
- **Queda prohibido saltarse la compilación nativa o colocar funciones simuladas de fallback en Kotlin puro** cuando se haya solicitado o diseñado una implementación nativa.
- Asegurar que los binarios existan para las 4 arquitecturas Android: `arm64-v8a`, `armeabi-v7a` (32 bits), `x86_64` y `x86`.

### 8. Documentación Explicativa en Código
- Todos los archivos de código fuente deben incluir una cabecera o comentarios estructurados que expliquen la lógica del archivo, sus entradas, salidas y la función de cada bloque relevante.

### 9. Rechazo al Minimalismo Extremo
- No apliques estilos visuales vacíos o minimalistas simplistas.
- Diseña interfaces con riqueza de información, métricas visuales, paletas contrastadas y herramientas accesibles.

### 10. Arquitectura Multi-Pantalla
- **Nunca diseñes una interfaz con una sola pantalla que concentre todo el contenido mezclado.**
- Emplea pantallas independientes y modulares conectadas por botones de navegación claros y botones de regreso (`onBack`).

### 11. Licencias de Dependencias
- No recomiendes ni agregues dependencias con licencias que obliguen al proyecto a liberar su código fuente (copyleft viral como GPL/AGPL) o que exijan menciones y créditos obligatorios dentro de la interfaz gráfica del usuario.
