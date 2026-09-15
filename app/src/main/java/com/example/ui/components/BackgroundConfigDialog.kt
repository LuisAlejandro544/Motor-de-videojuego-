package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BackgroundMode
import com.example.data.model.SceneBackgroundConfig
import com.example.data.model.SpriteCategory
import com.example.data.model.SpriteItem
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPlayGreen
import com.example.ui.theme.StudioPrimary

/**
 * Diálogo de configuración del Fondo del Escenario y Opciones de Cámara.
 * Permite cambiar el fondo del juego (Espacio cósmico, Cielo, Cyberpunk, Colores sólidos,
 * o un Sprite importado de fondo), activar la rejilla métrica y restablecer la vista.
 */
@Composable
fun BackgroundConfigDialog(
    isOpen: Boolean,
    currentConfig: SceneBackgroundConfig,
    sprites: List<SpriteItem>,
    showGrid: Boolean,
    onDismiss: () -> Unit,
    onSaveConfig: (SceneBackgroundConfig, Boolean) -> Unit
) {
    if (!isOpen) return

    var selectedMode by remember { mutableStateOf(currentConfig.mode) }
    var selectedSpriteId by remember { mutableStateOf(currentConfig.spriteId) }
    var isGridEnabled by remember { mutableStateOf(showGrid) }

    val backgroundSprites = remember(sprites) {
        sprites.filter { it.category == SpriteCategory.BACKGROUND }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Wallpaper,
                    contentDescription = null,
                    tint = StudioPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Fondo del Escenario",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Selecciona el Estilo de Fondo:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(BackgroundMode.values()) { mode ->
                    val isSelected = selectedMode == mode
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMode = mode }
                            .testTag("bg_mode_${mode.name}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) StudioPrimary else StudioBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Muestra visual previa del modo
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (mode) {
                                            BackgroundMode.GRADIENT_SPACE -> Brush.radialGradient(listOf(Color(0xFF141E33), Color(0xFF070A10)))
                                            BackgroundMode.GRADIENT_SKY -> Brush.verticalGradient(listOf(Color(0xFF1B62B2), Color(0xFF5AB1F5)))
                                            BackgroundMode.GRADIENT_CYBER -> Brush.verticalGradient(listOf(Color(0xFF1F0D3D), Color(0xFF33083B)))
                                            BackgroundMode.SOLID_DARK -> Brush.linearGradient(listOf(Color(0xFF0D1117), Color(0xFF0D1117)))
                                            BackgroundMode.SOLID_LIGHT -> Brush.linearGradient(listOf(Color(0xFFE6EDF3), Color(0xFFE6EDF3)))
                                            BackgroundMode.SPRITE_IMAGE -> Brush.linearGradient(listOf(Color(0xFF0F1A2E), Color(0xFF1C2740)))
                                        }
                                    )
                                    .border(1.dp, StudioBorder, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = mode.label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Si seleccionó modo SPRITE_IMAGE, mostrar los sprites de categoría BACKGROUND disponibles
                if (selectedMode == BackgroundMode.SPRITE_IMAGE) {
                    item {
                        Text(
                            text = "Selecciona la Textura de Fondo:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudioPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(backgroundSprites) { bgSprite ->
                                val isSpriteSelected = selectedSpriteId == bgSprite.id
                                Surface(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .clickable { selectedSpriteId = bgSprite.id },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSpriteSelected) StudioPrimary.copy(alpha = 0.2f) else Color(0xFF161E2E),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSpriteSelected) 2.dp else 1.dp,
                                        color = if (isSpriteSelected) StudioPrimary else StudioBorder
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        SpriteThumbnail(sprite = bgSprite, size = 48.dp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = bgSprite.name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Opción para activar / desactivar la cuadrícula
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GridOn, contentDescription = null, tint = StudioPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Cuadrícula Métrica", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("Guías y ejes cartesianos (X:0, Y:0)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = isGridEnabled,
                            onCheckedChange = { isGridEnabled = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = currentConfig.copy(
                        mode = selectedMode,
                        spriteId = selectedSpriteId
                    )
                    onSaveConfig(updated, isGridEnabled)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = StudioPrimary,
                    contentColor = Color.Black
                ),
                modifier = Modifier.testTag("btn_save_background_config")
            ) {
                Text("Aplicar Fondo", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Cerrar")
            }
        }
    )
}
