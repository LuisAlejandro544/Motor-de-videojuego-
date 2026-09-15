package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.ActorModel
import com.example.data.model.ActorType
import com.example.data.model.SceneBackgroundConfig
import com.example.data.model.VirtualControlsConfig
import com.example.ui.components.ActorListItemCard
import com.example.ui.components.BackgroundConfigDialog
import com.example.ui.components.SceneCanvasView
import com.example.ui.components.VirtualGamepad
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPlayGreen
import com.example.ui.theme.StudioPrimary
import com.example.ui.theme.StudioSecondary
import com.example.ui.theme.StudioTertiary
import kotlinx.coroutines.launch

/**
 * Pantalla principal del Editor de Escena y Vista Previa Temprana de Pocket Engine.
 * 
 * Mejoras arquitectónicas aplicadas:
 * 1. Vista Previa Temprana a Pantalla Completa: el canvas del juego toma todo el protagonismo visual
 *    permitiendo previsualizar el juego como un motor real en teléfonos móviles.
 * 2. Desacoplamiento de Actores: los actores ya no estorban el área de dibujo; se gestionan
 *    desde un BottomSheet ergonómico accesible en cualquier momento.
 * 3. Configuración de Fondo de Escenario (Espacio cósmico, Cielo, Cyberpunk, Sólido o Textura).
 * 4. Definición dinámica del Jugador Principal (Player) con aura visual distintiva.
 * 5. Gamepad táctil virtual en pantalla (D-Pad + Botones A y B) con movimiento en vivo del Player.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectEditorScreen(
    projectId: String,
    onBack: () -> Unit,
    onOpenBlocks: (actorId: String) -> Unit,
    onOpenAssets: (actorId: String) -> Unit,
    onOpenEngineConsole: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val project = MockEngineData.sampleProjects.find { it.id == projectId }
        ?: MockEngineData.sampleProjects.first()

    // Estado de actores en escena
    var actorsList by remember { mutableStateOf(MockEngineData.sampleActors) }
    var selectedActorId by remember {
        mutableStateOf(actorsList.firstOrNull { it.isPlayer }?.id ?: actorsList.firstOrNull()?.id ?: "")
    }

    // Configuración de fondo de escenario
    var backgroundConfig by remember { mutableStateOf(SceneBackgroundConfig()) }
    var isBackgroundDialogOpen by remember { mutableStateOf(false) }

    // Configuración de controles táctiles en pantalla
    var virtualControlsConfig by remember { mutableStateOf(VirtualControlsConfig()) }

    // Estados de navegación e interfaz
    var isActorsSheetOpen by remember { mutableStateOf(false) }
    var isFullScreenPreview by remember { mutableStateOf(false) }
    var showGrid by remember { mutableStateOf(true) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }

    val actorsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Actor designado como Jugador Principal (Player)
    val playerActor = actorsList.find { it.isPlayer } ?: actorsList.find { it.type == ActorType.PLAYER }

    // Función para mover al Player en tiempo real con el D-Pad virtual
    val handlePlayerMove = { dx: Int, dy: Int ->
        playerActor?.let { currentHero ->
            val step = currentHero.speed.toInt()
            val newX = (currentHero.posX + (dx * step)).coerceIn(-480, 480)
            val newY = (currentHero.posY + (dy * step)).coerceIn(-720, 720)

            actorsList = actorsList.map { actor ->
                if (actor.id == currentHero.id) {
                    actor.copy(posX = newX, posY = newY)
                } else {
                    actor
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (!isFullScreenPreview) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_projects")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar al Hub")
                        }
                    },
                    title = {
                        Column {
                            Text(
                                text = project.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (playerActor != null) "Player: ${playerActor.name}" else "Sin Player",
                                    fontSize = 11.sp,
                                    color = StudioPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${project.orientation.title}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        // Abrir Gestor de Sprites global
                        IconButton(
                            onClick = { onOpenAssets(selectedActorId) },
                            modifier = Modifier.testTag("btn_editor_sprites")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Gestor de Sprites",
                                tint = StudioSecondary
                            )
                        }

                        // Configuración del Fondo de Escenario
                        IconButton(
                            onClick = { isBackgroundDialogOpen = true },
                            modifier = Modifier.testTag("btn_editor_background")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Wallpaper,
                                contentDescription = "Configurar Fondo",
                                tint = StudioPrimary
                            )
                        }

                        // Consola Nativa C++ / Rust / Lua
                        IconButton(
                            onClick = onOpenEngineConsole,
                            modifier = Modifier.testTag("btn_editor_open_console")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = "Consola C++ / Rust / Lua",
                                tint = Color(0xFFE6EDF3)
                            )
                        }

                        // Modo Pantalla Completa de Vista Previa
                        IconButton(
                            onClick = { isFullScreenPreview = true },
                            modifier = Modifier.testTag("btn_fullscreen_preview")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Pantalla Completa",
                                tint = StudioPlayGreen
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFullScreenPreview) PaddingValues(0.dp) else innerPadding)
        ) {
            // 1. LIENZO DE LA ESCENA / EARLY PREVIEW DEL JUEGO A PANTALLA COMPLETA
            SceneCanvasView(
                actors = actorsList,
                selectedActorId = selectedActorId,
                backgroundConfig = backgroundConfig,
                showGrid = showGrid,
                zoom = zoomLevel,
                onSelectActor = { actorId ->
                    selectedActorId = actorId
                    actorsList = actorsList.map { it.copy() }
                },
                modifier = Modifier.fillMaxSize()
            )

            // 2. CONTROLES TÁCTILES VIRTUALES (GAMEPAD) EN PANTALLA
            VirtualGamepad(
                config = virtualControlsConfig,
                onDirectionHold = { dx, dy ->
                    handlePlayerMove(dx, dy)
                },
                onActionA = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("💥 Botón A: ¡Disparo láser del Player ejecutado!")
                    }
                },
                onActionB = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("⚡ Botón B: ¡Turbo propulsor activado!")
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (isFullScreenPreview) 16.dp else 70.dp)
            )

            // 3. BARRA DE HERRAMIENTAS FLOTANTE SUPERIOR (En modo Pantalla Completa)
            if (isFullScreenPreview) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 28.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón para salir de pantalla completa
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                        modifier = Modifier.clickable { isFullScreenPreview = false }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FullscreenExit,
                                contentDescription = "Salir de Pantalla Completa",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Salir Preview", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Botones rápidos de control (Fondo, Actores, Grid)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.75f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { isBackgroundDialogOpen = true }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Wallpaper, contentDescription = "Fondo", tint = StudioPrimary, modifier = Modifier.size(18.dp))
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.75f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { isActorsSheetOpen = true }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Layers, contentDescription = "Actores", tint = StudioSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // 4. BARRA DE HERRAMIENTAS INFERIOR ESTILO DOCK (Cuando no está en fullscreen)
            if (!isFullScreenPreview) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0D1117).copy(alpha = 0.92f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Botón para desplegar el Gestor de Actores (Ubicado cómodamente)
                        Button(
                            onClick = { isActorsSheetOpen = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudioSecondary.copy(alpha = 0.25f),
                                contentColor = StudioSecondary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("btn_open_actors_sheet")
                        ) {
                            Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Actores (${actorsList.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // Botón para definir/cambiar el Player rápidamente
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (playerActor != null) StudioPrimary.copy(alpha = 0.2f) else Color(0xFF1F2937),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (playerActor != null) StudioPrimary.copy(alpha = 0.5f) else StudioBorder
                            ),
                            modifier = Modifier
                                .clickable { isActorsSheetOpen = true }
                                .testTag("badge_quick_player")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = null,
                                    tint = StudioPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = playerActor?.name ?: "Definir Player",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Controles de zoom y cuadrícula
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { showGrid = !showGrid },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GridOn,
                                    contentDescription = "Cuadrícula",
                                    tint = if (showGrid) StudioPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    zoomLevel = if (zoomLevel >= 1.5f) 0.8f else zoomLevel + 0.2f
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ZoomIn,
                                    contentDescription = "Zoom",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 5. MODAL BOTTOM SHEET: GESTOR DE ACTORES REUBICADO CÓMODAMENTE
    if (isActorsSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isActorsSheetOpen = false },
            sheetState = actorsSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.testTag("actors_bottom_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Encabezado del Gestor de Actores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Gestor de Actores del Juego",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Define al Player, edita bloques y disfraces",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Botón para añadir nuevo actor
                    Button(
                        onClick = {
                            val newActor = ActorModel(
                                id = "act_${System.currentTimeMillis()}",
                                name = "Objeto ${actorsList.size + 1}",
                                type = ActorType.PROP,
                                posX = 0,
                                posY = 0,
                                rotation = 0f,
                                scale = 1.0f,
                                costumeName = "objeto_nuevo.png",
                                scriptsCount = 0,
                                soundsCount = 0,
                                isPlayer = false
                            )
                            actorsList = actorsList + newActor
                            selectedActorId = newActor.id
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Nuevo actor '${newActor.name}' creado")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_sheet_add_actor")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Añadir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista scrolleable de actores con botón para definir Player
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(actorsList, key = { it.id }) { actor ->
                        ActorListItemCard(
                            actor = actor,
                            isSelected = actor.id == selectedActorId,
                            onSelect = {
                                selectedActorId = actor.id
                            },
                            onOpenScripts = {
                                isActorsSheetOpen = false
                                onOpenBlocks(actor.id)
                            },
                            onOpenAssets = {
                                isActorsSheetOpen = false
                                onOpenAssets(actor.id)
                            },
                            onTogglePlayer = {
                                // Designa a este actor como el Player y desmarca al anterior
                                actorsList = actorsList.map { item ->
                                    if (item.id == actor.id) {
                                        val makePlayer = !item.isPlayer
                                        item.copy(isPlayer = makePlayer, type = if (makePlayer) ActorType.PLAYER else ActorType.PROP)
                                    } else {
                                        item.copy(isPlayer = false)
                                    }
                                }
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("🎮 Player definido como: ${actor.name}")
                                }
                            },
                            onToggleVisibility = {
                                actorsList = actorsList.map { item ->
                                    if (item.id == actor.id) item.copy(isVisible = !item.isVisible)
                                    else item
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // 6. DIÁLOGO DE CONFIGURACIÓN DEL FONDO DEL JUEGO
    BackgroundConfigDialog(
        isOpen = isBackgroundDialogOpen,
        currentConfig = backgroundConfig,
        sprites = MockEngineData.sampleSprites,
        showGrid = showGrid,
        onDismiss = { isBackgroundDialogOpen = false },
        onSaveConfig = { newConfig, newGridState ->
            backgroundConfig = newConfig
            showGrid = newGridState
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Fondo de escenario actualizado: ${newConfig.mode.label}")
            }
        }
    )
}
