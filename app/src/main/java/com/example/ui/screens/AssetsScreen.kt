package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockEngineData
import com.example.data.model.AssetResource
import com.example.data.model.SpriteCategory
import com.example.data.model.SpriteItem
import com.example.ui.components.ImportSpriteDialog
import com.example.ui.components.SpriteCardItem
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPrimary
import com.example.ui.theme.StudioSecondary
import com.example.ui.theme.StudioTertiary
import kotlinx.coroutines.launch

/**
 * Pantalla del Gestor de Sprites y Recursos del Motor Pocket Engine.
 * Permite categorizar y gestionar:
 * - Fondos (Backgrounds)
 * - Jugador (Player)
 * - Iconos (Icons)
 * - Ítems (Items)
 * - Enemigos, Props, HUD y Efectos FX
 * 
 * Además permite importar imágenes desde el Gestor de Archivos nativo de Android (SAF)
 * o desde la Galería de fotos del teléfono.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetsScreen(
    actorId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val actor = MockEngineData.sampleActors.find { it.id == actorId }
        ?: MockEngineData.sampleActors.first()

    var selectedTab by remember { mutableStateOf(0) } // 0: Gestor de Sprites, 1: Audio SFX
    val tabs = listOf("Gestor de Sprites", "Audio y SFX")

    var spritesList by remember { mutableStateOf<List<SpriteItem>>(MockEngineData.sampleSprites.toList()) }
    var selectedCategoryFilter by remember { mutableStateOf<SpriteCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isImportDialogOpen by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var audioAssetsList by remember {
        mutableStateOf(
            MockEngineData.sampleAssets.filter { it.name.endsWith(".wav") || it.name.endsWith(".mp3") }
        )
    }

    // Filtrado de sprites según categoría seleccionada y búsqueda
    val filteredSprites = remember(spritesList, selectedCategoryFilter, searchQuery) {
        spritesList.filter { sprite ->
            val matchesCategory = selectedCategoryFilter == null || sprite.category == selectedCategoryFilter
            val matchesSearch = searchQuery.isBlank() ||
                    sprite.name.contains(searchQuery, ignoreCase = true) ||
                    sprite.tags.any { it.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_assets")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar a la Escena")
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "Gestor de Sprites • ${actor.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "Fondos, Player, Iconos, Ítems y Texturas",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) {
                        isImportDialogOpen = true
                    } else {
                        val newAudio = AssetResource(
                            id = "sfx_${System.currentTimeMillis()}",
                            name = "sonido_efecto_${audioAssetsList.size + 1}.wav",
                            type = "Efecto Sonoro",
                            size = "44.1 kHz",
                            info = "Canal SFX"
                        )
                        audioAssetsList = audioAssetsList + newAudio
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Nuevo canal de audio añadido")
                        }
                    }
                },
                containerColor = StudioPrimary,
                contentColor = Color.Black,
                modifier = Modifier.testTag("fab_add_asset")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedTab == 0) "Importar Sprite" else "Nuevo Audio",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Pestañas principales
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = StudioPrimary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            if (selectedTab == 0) {
                // Barra de búsqueda rápida
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por nombre o etiqueta (ej. nave, fondo, moneda)...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("input_search_sprites")
                )

                // Barra horizontal de categorías (Fondo, Player, Iconos, Ítems, etc.)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text("Todos (${spritesList.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StudioPrimary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }

                    items(SpriteCategory.values()) { category ->
                        val count = spritesList.count { it.category == category }
                        FilterChip(
                            selected = selectedCategoryFilter == category,
                            onClick = { selectedCategoryFilter = if (selectedCategoryFilter == category) null else category },
                            label = { Text("${category.shortLabel} ($count)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StudioPrimary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                // Lista de Sprites clasificados
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                ) {
                    item {
                        Text(
                            text = "${filteredSprites.size} Sprites disponibles • Toca para asignar a ${actor.name}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(filteredSprites, key = { it.id }) { sprite ->
                        SpriteCardItem(
                            sprite = sprite,
                            isSelected = actor.spriteId == sprite.id,
                            onSelect = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Sprite seleccionado: ${sprite.name}")
                                }
                            },
                            onAssignToActor = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Asignado '${sprite.name}' al actor ${actor.name}")
                                }
                            },
                            onDelete = {
                                spritesList = spritesList.filter { it.id != sprite.id }
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Sprite eliminado")
                                }
                            }
                        )
                    }
                }
            } else {
                // Pestaña de Audio SFX
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        Text(
                            text = "Efectos sonoros y pistas vinculadas a la escena",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(audioAssetsList, key = { it.id }) { asset ->
                        AssetAudioCardItem(
                            asset = asset,
                            onDelete = {
                                audioAssetsList = audioAssetsList.filter { it.id != asset.id }
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de importación (Gestor de Archivos SAF o Galería)
    ImportSpriteDialog(
        isOpen = isImportDialogOpen,
        onDismiss = { isImportDialogOpen = false },
        onSpriteImported = { newSprite ->
            spritesList = spritesList + newSprite
            MockEngineData.sampleSprites.add(newSprite)
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Sprite '${newSprite.name}' añadido a la categoría ${newSprite.category.shortLabel}")
            }
        }
    )
}

@Composable
fun AssetAudioCardItem(
    asset: AssetResource,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("asset_item_${asset.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(StudioTertiary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Audiotrack,
                    contentDescription = null,
                    tint = StudioTertiary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${asset.type} • ${asset.size}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { /* Preescucha rápida */ }) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Reproducir sonido",
                    tint = StudioTertiary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar recurso",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
