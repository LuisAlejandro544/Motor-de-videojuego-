package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockEngineData
import com.example.data.model.BlockCategory
import com.example.data.model.BlockTemplate
import com.example.data.model.PlacedBlock
import com.example.ui.components.BlockPaletteItem
import com.example.ui.components.PlacedBlockCard
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioPrimary
import com.example.ui.theme.StudioTertiary

/**
 * Pantalla principal del Workspace de Bloques de Programación.
 * Esta pantalla cumple con el requerimiento del usuario:
 * "solamente empezaremos por una interfaz principal, que hay tenga dónde estarán los próximos bloques.
 * No hagas funciones, solo trabaja en las interfazes".
 *
 * Ofrece:
 * 1. Lienzo de ensamblado con los bloques actuales y hendiduras magnéticas.
 * 2. Visualización del espacio reservado para los próximos bloques.
 * 3. Selector de categorías estilo Pocket Code (Eventos, Movimiento, Control, Apariencia, Sonido, Sensores, Datos).
 * 4. Paleta / Catálogo visual inferior de bloques disponibles para las próximas fases.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocksScreen(
    actorId: String,
    onBack: () -> Unit,
    onOpenEngineConsole: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val actor = MockEngineData.sampleActors.find { it.id == actorId }
        ?: MockEngineData.sampleActors.first()

    var placedBlocks by remember { mutableStateOf(MockEngineData.samplePlacedBlocks) }
    var selectedCategory by remember { mutableStateOf<BlockCategory?>(null) }
    var showPaletteSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Filtrar catálogo de bloques si hay una categoría seleccionada
    val displayedTemplates = remember(selectedCategory) {
        if (selectedCategory == null) {
            MockEngineData.availableBlockTemplates
        } else {
            MockEngineData.availableBlockTemplates.filter { it.category == selectedCategory }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_from_blocks")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar al Editor de Escena")
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(StudioPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = StudioPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Scripts: ${actor.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                            Text(
                                text = "${placedBlocks.size} bloques en el lienzo",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenEngineConsole,
                        modifier = Modifier.testTag("btn_blocks_open_console")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "Abrir Consola Lua 5.4 Nativa",
                            tint = StudioPrimary
                        )
                    }
                    IconButton(onClick = { /* Zoom in */ }) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In")
                    }
                    IconButton(onClick = { /* Zoom out */ }) {
                        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPaletteSheet = true },
                containerColor = StudioPrimary,
                contentColor = Color.Black,
                modifier = Modifier.testTag("fab_add_block")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Añadir Bloque", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(StudioDarkBg)
        ) {
            // Barra de Categorías de Bloques (Filtro rápido / Paleta de color)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Todas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StudioPrimary,
                            selectedLabelColor = Color.Black
                        )
                    )
                }

                items(BlockCategory.values()) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = if (selectedCategory == category) null else category
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(category.color)
                            )
                        },
                        label = { Text(category.title) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = category.color.copy(alpha = 0.25f),
                            selectedLabelColor = category.color
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selectedCategory == category) category.color else StudioBorder,
                            enabled = true,
                            selected = selectedCategory == category
                        )
                    )
                }
            }

            // Lienzo Principal con Bloques Ensamblados y Área de los Próximos Bloques
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Banner informativo del estado del editor
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(StudioTertiary)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Lienzo de Programación Visual • Los bloques se ejecutan de arriba hacia abajo",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Renderizado de bloques ensamblados actualmente
                items(placedBlocks, key = { it.id }) { block ->
                    PlacedBlockCard(
                        block = block,
                        onBlockClick = { /* Interacción táctil con el bloque */ }
                    )
                }

                // ZONA DONDE ESTARÁN LOS PRÓXIMOS BLOQUES (Requerimiento explícito del usuario)
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showPaletteSheet = true }
                            .testTag("dropzone_next_blocks"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF161B22).copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.5.dp,
                            color = StudioPrimary.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Ranura superior tipo conector de puzzle
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                                    .background(StudioPrimary.copy(alpha = 0.6f))
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(StudioPrimary.copy(alpha = 0.15f))
                                    .border(1.dp, StudioPrimary.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = StudioPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "ÁREA PARA PRÓXIMOS BLOQUES",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = StudioPrimary,
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Aquí encajarán los nuevos bloques de movimiento, física, condiciones y eventos en la siguiente etapa.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { showPaletteSheet = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StudioPrimary.copy(alpha = 0.2f),
                                    contentColor = StudioPrimary
                                )
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Explorar Paleta de Bloques", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Espacio inferior para scroll holgado
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Modal Bottom Sheet: Catálogo / Paleta de Bloques Disponibles
    if (showPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaletteSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Paleta de Bloques Disponibles",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Selecciona un bloque para colocarlo en el lienzo",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Contador de plantillas
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${displayedTemplates.size} disponibles",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de bloques disponibles listos para ser colocados
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedTemplates, key = { it.id }) { template ->
                        BlockPaletteItem(
                            template = template,
                            onSelect = {
                                // Colocar visualmente el bloque en el lienzo
                                val newPlaced = PlacedBlock(
                                    id = "pl_${System.currentTimeMillis()}",
                                    templateId = template.id,
                                    category = template.category,
                                    text = template.label,
                                    paramValue = template.defaultParam,
                                    shape = template.shape,
                                    indentLevel = 0
                                )
                                placedBlocks = placedBlocks + newPlaced
                                showPaletteSheet = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
