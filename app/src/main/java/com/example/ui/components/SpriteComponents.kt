package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Rocket
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.SpriteCategory
import com.example.data.model.SpriteItem
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioPlayGreen
import com.example.ui.theme.StudioPrimary
import com.example.ui.theme.StudioSecondary
import com.example.ui.theme.StudioTertiary

/**
 * Componentes especializados para el Gestor de Sprites de Pocket Engine.
 * Brinda renderizado de miniaturas (Coil AsyncImage para archivos importados de galería o
 * gestor de archivos nativo SAF, o representaciones vectoriales según su categoría).
 */

@Composable
fun SpriteThumbnail(
    sprite: SpriteItem,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    val accentColor = Color(sprite.tintColorHex)

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F1522))
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (!sprite.imageUri.isNullOrEmpty()) {
            // Renderizado real con Coil desde el URI local del teléfono (Galería o File Manager)
            AsyncImage(
                model = sprite.imageUri,
                contentDescription = sprite.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Renderizado vectorial expresivo según forma y categoría
            when (sprite.shapeType) {
                "ship" -> {
                    Icon(
                        imageVector = Icons.Default.Rocket,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(size * 0.6f)
                    )
                }
                "star" -> {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(size * 0.6f)
                    )
                }
                "gem" -> {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(size * 0.6f)
                    )
                }
                "heart" -> {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(size * 0.6f)
                    )
                }
                "square" -> {
                    Box(
                        modifier = Modifier
                            .size(size * 0.55f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(accentColor, accentColor.copy(alpha = 0.5f))
                                )
                            )
                    )
                }
                else -> {
                    // Círculo por defecto o categoría
                    Box(
                        modifier = Modifier
                            .size(size * 0.55f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(accentColor, accentColor.copy(alpha = 0.5f))
                                )
                            )
                    )
                }
            }
        }
    }
}

/**
 * Insignia de categoría del sprite (Fondo, Player, Icono, Ítem, etc.)
 */
@Composable
fun SpriteCategoryBadge(
    category: SpriteCategory,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (category) {
        SpriteCategory.BACKGROUND -> Triple(StudioSecondary.copy(alpha = 0.2f), StudioSecondary, Icons.Default.Wallpaper)
        SpriteCategory.PLAYER -> Triple(StudioPrimary.copy(alpha = 0.2f), StudioPrimary, Icons.Default.SportsEsports)
        SpriteCategory.ICONS -> Triple(Color(0xFFF0883E).copy(alpha = 0.2f), Color(0xFFF0883E), Icons.Default.Extension)
        SpriteCategory.ITEMS -> Triple(Color(0xFFFFD166).copy(alpha = 0.2f), Color(0xFFFFD166), Icons.Default.MilitaryTech)
        SpriteCategory.ENEMY -> Triple(Color(0xFFFF5252).copy(alpha = 0.2f), Color(0xFFFF5252), Icons.Default.Close)
        SpriteCategory.PROP -> Triple(Color(0xFF8B949E).copy(alpha = 0.2f), Color(0xFF8B949E), Icons.Default.Layers)
        SpriteCategory.UI_HUD -> Triple(Color(0xFF06D6A0).copy(alpha = 0.2f), Color(0xFF06D6A0), Icons.Default.Extension)
        SpriteCategory.FX -> Triple(StudioTertiary.copy(alpha = 0.2f), StudioTertiary, Icons.Default.Star)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = category.shortLabel,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

/**
 * Tarjeta individual de Sprite dentro del Gestor de Sprites.
 */
@Composable
fun SpriteCardItem(
    sprite: SpriteItem,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onAssignToActor: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("sprite_card_${sprite.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else StudioBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpriteThumbnail(
                sprite = sprite,
                size = 52.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sprite.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    SpriteCategoryBadge(category = sprite.category)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${sprite.width}x${sprite.height} px • ${if (sprite.imageUri != null) "Imagen Local" else "Vectorial"}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Botón de asignación rápida
            IconButton(
                onClick = onAssignToActor,
                modifier = Modifier.testTag("btn_assign_sprite_${sprite.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Asignar",
                    tint = StudioPrimary
                )
            }

            if (!sprite.isDefault) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Diálogo interactivo para importar sprites desde el Gestor de Archivos nativo de Android (SAF),
 * la Galería de fotos o crear un sprite vectorial rápido.
 */
@Composable
fun ImportSpriteDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSpriteImported: (SpriteItem) -> Unit
) {
    if (!isOpen) return

    var spriteName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(SpriteCategory.PLAYER) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var shapeType by remember { mutableStateOf("ship") }

    // Launcher 1: Gestor de Archivos Nativo del Sistema Android (SAF)
    val fileManagerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            if (spriteName.isBlank()) {
                val pathSegment = uri.lastPathSegment ?: "sprite_archivo"
                spriteName = pathSegment.substringAfterLast("/").substringBeforeLast(".")
            }
        }
    }

    // Launcher 2: Galería de Fotos de Android (Photo Picker)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            if (spriteName.isBlank()) {
                spriteName = "sprite_galeria_${System.currentTimeMillis() % 1000}"
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Importar Nuevo Sprite",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Campo de nombre
                OutlinedTextField(
                    value = spriteName,
                    onValueChange = { spriteName = it },
                    label = { Text("Nombre del Sprite") },
                    placeholder = { Text("ej. nave_combate, fondo_planeta") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_sprite_name")
                )

                // Botones de selección de origen (Gestor de Archivos Nativo vs Galería)
                Text(
                    text = "Origen de la Imagen:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón 1: Gestor de Archivos nativo de Android (SAF)
                    Button(
                        onClick = { fileManagerLauncher.launch("image/*") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_import_from_files"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedUri != null) StudioPrimary else Color(0xFF1F2937),
                            contentColor = if (selectedUri != null) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Archivos", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Botón 2: Galería de fotos
                    Button(
                        onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_import_from_gallery"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1F2937),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Galería", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (selectedUri != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StudioPlayGreen.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioPlayGreen.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = StudioPlayGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Imagen seleccionada correctamente",
                                fontSize = 11.sp,
                                color = StudioPlayGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Selector de categoría del sprite
                Text(
                    text = "Rol / Categoría en el Motor:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SpriteCategory.values()) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.shortLabel, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StudioPrimary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (spriteName.isNotBlank()) spriteName else "sprite_${System.currentTimeMillis() % 1000}"
                    val newSprite = SpriteItem(
                        id = "spr_${System.currentTimeMillis()}",
                        name = finalName,
                        category = selectedCategory,
                        imageUri = selectedUri?.toString(),
                        width = if (selectedCategory == SpriteCategory.BACKGROUND) 1080 else 128,
                        height = if (selectedCategory == SpriteCategory.BACKGROUND) 1920 else 128,
                        tintColorHex = when (selectedCategory) {
                            SpriteCategory.PLAYER -> 0xFF58A6FF
                            SpriteCategory.BACKGROUND -> 0xFF0B132B
                            SpriteCategory.ENEMY -> 0xFFFF5252
                            SpriteCategory.ITEMS -> 0xFFFFD166
                            SpriteCategory.ICONS -> 0xFFF0883E
                            else -> 0xFF06D6A0
                        },
                        shapeType = shapeType,
                        isDefault = false
                    )
                    onSpriteImported(newSprite)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = StudioPrimary,
                    contentColor = Color.Black
                ),
                modifier = Modifier.testTag("btn_confirm_import_sprite")
            ) {
                Text("Guardar Sprite", fontWeight = FontWeight.Bold)
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
                Text("Cancelar")
            }
        }
    )
}
