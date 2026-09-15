package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ControlCamera
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.BlockControlColor
import com.example.ui.theme.BlockDataColor
import com.example.ui.theme.BlockEventColor
import com.example.ui.theme.BlockLooksColor
import com.example.ui.theme.BlockMotionColor
import com.example.ui.theme.BlockSensingColor
import com.example.ui.theme.BlockSoundColor

/**
 * Modelos de datos visuales para las interfaces del motor de videojuegos (Pocket Engine).
 * Contienen las definiciones de Proyectos, Actores/Objetos de escena, Categorías
 * y Plantillas visuales para el área de bloques de programación.
 */

// Orientación de pantalla para el proyecto
enum class ProjectOrientation(val title: String) {
    PORTRAIT("Vertical (9:16)"),
    LANDSCAPE("Horizontal (16:9)")
}

// Representa un proyecto o videojuego creado en el motor
data class ProjectModel(
    val id: String,
    val title: String,
    val description: String,
    val orientation: ProjectOrientation,
    val actorsCount: Int,
    val scenesCount: Int,
    val lastModified: String,
    val accentHex: Long = 0xFF58A6FF
)

// Tipos de actores o elementos en el juego
enum class ActorType(val label: String) {
    PLAYER("Jugador Principal"),
    ENEMY("Enemigo / Obstáculo"),
    PROP("Objeto / Plataforma"),
    UI("Elemento de Interfaz"),
    BACKGROUND("Fondo de Escenario")
}

/**
 * Categorías de Sprites de un motor de juegos profesional.
 * Permite clasificar texturas e imágenes según su función en el juego.
 */
enum class SpriteCategory(
    val title: String,
    val subtitle: String,
    val shortLabel: String
) {
    BACKGROUND("Fondo (Background)", "Capas de fondo, texturas y paisajes", "Fondo"),
    PLAYER("Jugador (Player)", "Sprites del personaje principal controlable", "Player"),
    ICONS("Iconos (Icons)", "Símbolos, botones de interfaz y cursores", "Iconos"),
    ITEMS("Ítems (Items)", "Monedas, gemas, llaves y coleccionables", "Ítems"),
    ENEMY("Enemigo (Enemy)", "Enemigos, monstruos y peligros móviles", "Enemigo"),
    PROP("Props / Escenario", "Plataformas, bloques y decoraciones", "Props"),
    UI_HUD("Interfaz (UI / HUD)", "Barras de vida, marcos y diálogos", "HUD"),
    FX("Efectos (FX)", "Explosiones, chispas y partículas", "FX")
}

/**
 * Representa un Sprite registrado en el Gestor de Sprites del proyecto.
 * Soporta imágenes locales del dispositivo (Galería o Gestor de Archivos SAF) o gráficos vectoriales.
 */
data class SpriteItem(
    val id: String,
    val name: String,
    val category: SpriteCategory,
    val imageUri: String? = null,
    val width: Int = 64,
    val height: Int = 64,
    val tintColorHex: Long = 0xFF58A6FF,
    val tags: List<String> = emptyList(),
    val isDefault: Boolean = false,
    val shapeType: String = "circle" // "ship", "circle", "square", "star", "gem", "heart"
)

// Modos de fondo para la escena del juego
enum class BackgroundMode(val label: String) {
    GRADIENT_SPACE("Espacio Cósmico (Estrellas)"),
    GRADIENT_SKY("Cielo Azul Diurno"),
    GRADIENT_CYBER("Neón Cyberpunk"),
    SOLID_DARK("Color Sólido Oscuro"),
    SOLID_LIGHT("Color Sólido Claro"),
    SPRITE_IMAGE("Sprite / Textura de Fondo")
}

// Configuración del fondo del escenario
data class SceneBackgroundConfig(
    val mode: BackgroundMode = BackgroundMode.GRADIENT_SPACE,
    val solidColorHex: Long = 0xFF0D1117,
    val primaryColorHex: Long = 0xFF080C14,
    val secondaryColorHex: Long = 0xFF141E33,
    val spriteId: String? = null,
    val spriteUri: String? = null,
    val isTileRepeated: Boolean = false
)

// Configuración de controles táctiles en pantalla (Gamepad Virtual)
data class VirtualControlsConfig(
    val isEnabled: Boolean = true,
    val showDpad: Boolean = true,
    val showActionA: Boolean = true,
    val labelA: String = "Disparo (A)",
    val showActionB: Boolean = true,
    val labelB: String = "Turbo (B)",
    val opacity: Float = 0.85f
)

// Representa un actor u objeto dentro de la escena activa
data class ActorModel(
    val id: String,
    val name: String,
    val type: ActorType,
    val posX: Int,
    val posY: Int,
    val rotation: Float,
    val scale: Float,
    val costumeName: String,
    val scriptsCount: Int,
    val soundsCount: Int,
    val isVisible: Boolean = true,
    val isPlayer: Boolean = (type == ActorType.PLAYER),
    val spriteId: String? = null,
    val imageUri: String? = null,
    val speed: Float = 6f
)

// Categorías de bloques visuales al estilo Pocket Code / Scratch
enum class BlockCategory(
    val title: String,
    val subtitle: String,
    val color: Color,
    val icon: ImageVector
) {
    EVENTOS(
        title = "Eventos",
        subtitle = "Inicio de escena, toques y mensajes",
        color = BlockEventColor,
        icon = Icons.Default.FlashOn
    ),
    MOVIMIENTO(
        title = "Movimiento",
        subtitle = "Posición, velocidad, ángulos y física",
        color = BlockMotionColor,
        icon = Icons.Default.Navigation
    ),
    CONTROL(
        title = "Control",
        subtitle = "Bucles, esperas y condiciones",
        color = BlockControlColor,
        icon = Icons.Default.ControlCamera
    ),
    APARIENCIA(
        title = "Apariencia",
        subtitle = "Disfraces, tamaños, efectos visuales",
        color = BlockLooksColor,
        icon = Icons.Default.Animation
    ),
    SONIDO(
        title = "Sonido",
        subtitle = "Efectos sonoros, notas y música",
        color = BlockSoundColor,
        icon = Icons.Default.GraphicEq
    ),
    SENSORES(
        title = "Sensores",
        subtitle = "Pantalla táctil, colisiones e inclinación",
        color = BlockSensingColor,
        icon = Icons.Default.Sensors
    ),
    DATOS(
        title = "Datos / Vars",
        subtitle = "Variables globales, locales y listas",
        color = BlockDataColor,
        icon = Icons.Default.DataObject
    )
}

// Forma del bloque de código visual
enum class BlockShape {
    CAP_HEADER,    // Bloque inicio redondeado arriba (ej. "Al iniciar")
    STACK_COMMAND, // Bloque estándar con conector superior e inferior
    WRAP_LOOP,     // Bloque contenedor en 'C' (ej. "Por siempre", "Repetir")
    BOOLEAN_VALUE  // Bloque de condición
}

// Plantilla de bloque disponible para arrastrar o ensamblar en la interfaz
data class BlockTemplate(
    val id: String,
    val category: BlockCategory,
    val label: String,
    val shape: BlockShape,
    val defaultParam: String = "",
    val description: String = ""
)

// Bloque ya colocado en el área de trabajo de scripts
data class PlacedBlock(
    val id: String,
    val templateId: String,
    val category: BlockCategory,
    val text: String,
    val paramValue: String = "",
    val shape: BlockShape = BlockShape.STACK_COMMAND,
    val indentLevel: Int = 0
)

// Recurso gráfico o auditivo de la biblioteca del proyecto
data class AssetResource(
    val id: String,
    val name: String,
    val type: String, // "Sprite", "Fondo", "Sonido"
    val size: String,
    val info: String
)
