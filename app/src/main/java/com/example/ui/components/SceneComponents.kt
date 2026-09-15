package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActorModel
import com.example.data.model.ActorType
import com.example.data.model.BackgroundMode
import com.example.data.model.SceneBackgroundConfig
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioGridLine
import com.example.ui.theme.StudioPlayGreen
import com.example.ui.theme.StudioPrimary
import com.example.ui.theme.StudioSecondary
import com.example.ui.theme.StudioTertiary

/**
 * Componentes de Escena y Actores del motor de juego Pocket Engine.
 * Proporciona el canvas de coordenadas 2D para visualización del juego,
 * renderizado dinámico de fondos (Espacio, Cielo, Neón, Sólido o Sprite)
 * y las tarjetas de gestión de actores y definición de Player.
 */

@Composable
fun SceneCanvasView(
    actors: List<ActorModel>,
    selectedActorId: String?,
    modifier: Modifier = Modifier,
    backgroundConfig: SceneBackgroundConfig = SceneBackgroundConfig(),
    showGrid: Boolean = true,
    zoom: Float = 1.0f,
    panOffsetX: Float = 0f,
    panOffsetY: Float = 0f,
    onSelectActor: (String) -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(StudioDarkBg)
            .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
            .testTag("scene_canvas_view")
    ) {
        // Lienzo de dibujo con cuadrícula métrica, fondo configurable y actores
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = (size.width / 2) + panOffsetX
            val centerY = (size.height / 2) + panOffsetY

            // 1. Dibujar el fondo configurable de la escena
            when (backgroundConfig.mode) {
                BackgroundMode.GRADIENT_SPACE -> {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF141E33), Color(0xFF070A10)),
                            center = Offset(centerX, centerY),
                            radius = size.maxDimension * 0.75f
                        )
                    )
                    // Puntos de estrellas cósmicas
                    val starPositions = listOf(
                        Offset(size.width * 0.15f, size.height * 0.2f),
                        Offset(size.width * 0.85f, size.height * 0.25f),
                        Offset(size.width * 0.3f, size.height * 0.7f),
                        Offset(size.width * 0.7f, size.height * 0.8f),
                        Offset(size.width * 0.5f, size.height * 0.12f),
                        Offset(size.width * 0.9f, size.height * 0.65f),
                        Offset(size.width * 0.1f, size.height * 0.85f)
                    )
                    starPositions.forEach { pos ->
                        drawCircle(color = Color.White.copy(alpha = 0.5f), radius = 1.5f, center = pos)
                    }
                }
                BackgroundMode.GRADIENT_SKY -> {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1B62B2), Color(0xFF5AB1F5), Color(0xFFE9B774))
                        )
                    )
                }
                BackgroundMode.GRADIENT_CYBER -> {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1F0D3D), Color(0xFF0D1D4A), Color(0xFF33083B))
                        )
                    )
                }
                BackgroundMode.SOLID_DARK -> {
                    drawRect(color = Color(backgroundConfig.solidColorHex))
                }
                BackgroundMode.SOLID_LIGHT -> {
                    drawRect(color = Color(0xFFE6EDF3))
                }
                BackgroundMode.SPRITE_IMAGE -> {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF0F1A2E), Color(0xFF1C2740))
                        )
                    )
                }
            }

            // 2. Cuadrícula métrica si está habilitada
            if (showGrid) {
                val gridStep = 40f * zoom
                val gridColor = Color.White.copy(alpha = 0.08f)
                val axisColor = Color(0xFF58A6FF).copy(alpha = 0.4f)

                // Líneas horizontales
                var y = centerY % gridStep
                while (y < size.height) {
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += gridStep
                }

                // Líneas verticales
                var x = centerX % gridStep
                while (x < size.width) {
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += gridStep
                }

                // Ejes centrales principales (X = 0, Y = 0)
                val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                drawLine(
                    color = axisColor,
                    start = Offset(0f, centerY),
                    end = Offset(size.width, centerY),
                    strokeWidth = 1.5f,
                    pathEffect = dashPathEffect
                )
                drawLine(
                    color = axisColor,
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, size.height),
                    strokeWidth = 1.5f,
                    pathEffect = dashPathEffect
                )

                // Origen central
                drawCircle(
                    color = Color(0xFF58A6FF),
                    radius = 4f * zoom,
                    center = Offset(centerX, centerY)
                )
            }

            // 3. Dibujar actores en el canvas
            actors.filter { it.isVisible }.forEach { actor ->
                val actorDrawX = centerX + (actor.posX * 0.4f * zoom)
                val actorDrawY = centerY - (actor.posY * 0.4f * zoom) // Invertido para coords cartesianas
                val isSelected = actor.id == selectedActorId

                val actorColor = when {
                    actor.isPlayer -> Color(0xFF58A6FF)
                    actor.type == ActorType.ENEMY -> Color(0xFFFF5252)
                    actor.type == ActorType.PROP -> Color(0xFFFFD166)
                    actor.type == ActorType.UI -> Color(0xFF06D6A0)
                    else -> Color(0xFF8338EC)
                }

                // Aura especial de Player si el actor está designado como Jugador Principal
                if (actor.isPlayer) {
                    drawCircle(
                        color = Color(0xFF58A6FF).copy(alpha = 0.25f),
                        radius = (34f * actor.scale * zoom),
                        center = Offset(actorDrawX, actorDrawY)
                    )
                    drawCircle(
                        color = Color(0xFF58A6FF).copy(alpha = 0.6f),
                        radius = (28f * actor.scale * zoom),
                        center = Offset(actorDrawX, actorDrawY),
                        style = Stroke(width = 1.5f)
                    )
                }

                // Círculo o forma del actor
                drawCircle(
                    color = actorColor.copy(alpha = 0.35f),
                    radius = 24f * actor.scale * zoom,
                    center = Offset(actorDrawX, actorDrawY)
                )
                drawCircle(
                    color = if (isSelected) Color.White else actorColor,
                    radius = 16f * actor.scale * zoom,
                    center = Offset(actorDrawX, actorDrawY)
                )

                // Si está seleccionado, mostrar recuadro de selección
                if (isSelected) {
                    drawRect(
                        color = Color(0xFF58A6FF),
                        topLeft = Offset(actorDrawX - (30f * zoom), actorDrawY - (30f * zoom)),
                        size = androidx.compose.ui.geometry.Size(60f * zoom, 60f * zoom),
                        style = Stroke(width = 2f)
                    )
                }
            }
        }

        // Overlay con información de coordenadas y resolución de pantalla
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(StudioPlayGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Escena 2D • Zoom ${String.format("%.1f", zoom)}x • 60 FPS",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Tarjeta de cada Actor u Objeto de la lista de actores en la escena.
 */
@Composable
fun ActorListItemCard(
    actor: ActorModel,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit,
    onOpenScripts: () -> Unit,
    onOpenAssets: () -> Unit,
    onTogglePlayer: () -> Unit = {},
    onToggleVisibility: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("actor_card_${actor.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else StudioBorder
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícono representativo del actor
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                actor.isPlayer -> StudioPrimary.copy(alpha = 0.25f)
                                actor.type == ActorType.ENEMY -> Color(0xFFFF5252).copy(alpha = 0.2f)
                                actor.type == ActorType.PROP -> Color(0xFFFFD166).copy(alpha = 0.2f)
                                actor.type == ActorType.UI -> Color(0xFF06D6A0).copy(alpha = 0.2f)
                                else -> StudioSecondary.copy(alpha = 0.2f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (actor.isPlayer) Icons.Default.SportsEsports else Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = if (actor.isPlayer) StudioPrimary else when (actor.type) {
                            ActorType.ENEMY -> Color(0xFFFF5252)
                            ActorType.PROP -> Color(0xFFFFD166)
                            ActorType.UI -> Color(0xFF06D6A0)
                            else -> StudioSecondary
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = actor.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (actor.isPlayer) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = StudioPrimary.copy(alpha = 0.25f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StudioPrimary.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "PLAYER 🎮",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StudioPrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${actor.type.label} • X:${actor.posX} Y:${actor.posY} • Escala: ${actor.scale}x",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Botón para definir si es el Player
                IconButton(
                    onClick = onTogglePlayer,
                    modifier = Modifier.testTag("btn_toggle_player_${actor.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Definir Player",
                        tint = if (actor.isPlayer) StudioPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }

                // Indicador de visibilidad
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (actor.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Visibilidad",
                        tint = if (actor.isVisible) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botones de acceso directo a los Bloques (Scripts) y Recursos de este Actor
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón principal: Editar Bloques
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenScripts() }
                        .testTag("btn_scripts_${actor.id}"),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Bloques (${actor.scriptsCount})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Botón secundario: Ver Disfraces y Recursos
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenAssets() }
                        .testTag("btn_assets_${actor.id}"),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Disfraces / Sprite",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

