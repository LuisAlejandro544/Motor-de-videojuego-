package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.nativebridge.NativeEngineBridge
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioPlayGreen
import com.example.ui.theme.StudioPrimary
import com.example.ui.theme.StudioSecondary
import com.example.ui.theme.StudioTertiary
import kotlinx.coroutines.launch

/**
 * ==============================================================================
 * PANTALLA DE CONSOLA Y TELEMETRÍA DEL MOTOR NATIVO (EngineConsoleScreen)
 * ==============================================================================
 * Esta pantalla expone al desarrollador móvil:
 * 1. Inspección de Arquitectura NDK (Soporte 32 bits y 64 bits garantizado).
 * 2. Estado de los tres subsistemas: C++20, Rust Core y Lua 5.4.9 Puro.
 * 3. Consola interactiva para ejecutar scripts en Lua 5.4 en tiempo real.
 * 4. Monitor de ticks de física calculados por el motor de Rust.
 * ==============================================================================
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EngineConsoleScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val engineInfo by NativeEngineBridge.engineInfo.collectAsState()
    val consoleLogs by NativeEngineBridge.consoleLogs.collectAsState()

    var luaCodeInput by remember {
        mutableStateOf(
            """-- Script de prueba en Lua 5.4.9 puro
print("=== INICIANDO MOTOR NATIVO ===")
print("Lua Version: " .. _VERSION)

-- 1. Disparar un paso de simulación en Rust
local ticks = engine.step(0.016)
print("Ticks en Rust Core: " .. tostring(ticks))
print(engine.rust_stats())

-- 2. Crear un actor en Rust y desplazarlo
actor.spawn("heroe_nave", 120.0, 300.0)
actor.move("heroe_nave", 15.5, -4.2)
local x, y = actor.get_coords("heroe_nave")
print(string.format("Coordenadas del Actor: X=%.2f, Y=%.2f", x, y))
print("¡Ejecutado con éxito en C++, Rust y Lua!")"""
        )
    }

    var isExecuting by remember { mutableStateOf(false) }
    var rustLiveStats by remember { mutableStateOf(NativeEngineBridge.getRustTelemetry()) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_from_console")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "Consola de Motores Nativos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "C++20 • Rust Core • Lua 5.4.9 (Puro)",
                            fontSize = 11.sp,
                            color = StudioPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { NativeEngineBridge.clearLogs() }) {
                        Icon(Icons.Default.CleaningServices, contentDescription = "Limpiar consola")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(StudioDarkBg),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TARJETA 1: Telemetría de Arquitectura (32 / 64 bits, NDK, Rust, Lua)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_engine_telemetry"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StudioPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Memory,
                                        contentDescription = null,
                                        tint = StudioPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Arquitectura del Motor",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Binarios NDK 32 y 64 bits",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(StudioPlayGreen.copy(alpha = 0.2f))
                                    .border(1.dp, StudioPlayGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (engineInfo.isInitialized) "ACTIVO (${engineInfo.bitWidth} BITS)" else "CARGANDO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StudioPlayGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Grid de 3 pilares
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // C++ Pill
                            SubsystemBadge(
                                title = "C++ NDK",
                                subtitle = engineInfo.cppStandard,
                                detail = "ABI: ${engineInfo.architecture}",
                                tint = StudioSecondary,
                                modifier = Modifier.weight(1f)
                            )

                            // Rust Pill
                            SubsystemBadge(
                                title = "Rust Core",
                                subtitle = "Simulación",
                                detail = "Memoria Segura",
                                tint = Color(0xFFFF8C00),
                                modifier = Modifier.weight(1f)
                            )

                            // Lua Pill
                            SubsystemBadge(
                                title = "Lua Puro",
                                subtitle = "v5.4.9",
                                detail = "Sin Wrappers",
                                tint = StudioPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // TARJETA 2: Control de Físicas y Ticks en Rust
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = Color(0xFFFF8C00),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Simulación Determinista (Rust)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        NativeEngineBridge.stepSimulation(0.0166f)
                                        rustLiveStats = NativeEngineBridge.getRustTelemetry()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF8C00).copy(alpha = 0.2f),
                                    contentColor = Color(0xFFFF8C00)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Avanzar Tick", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = rustLiveStats,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // TARJETA 3: Editor de Scripts Lua 5.4 Puro
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Code,
                                    contentDescription = null,
                                    tint = StudioPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Editor Interactivo Lua 5.4.9",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            // Botón de Ejecutar en Lua
                            Button(
                                onClick = {
                                    isExecuting = true
                                    coroutineScope.launch {
                                        NativeEngineBridge.executeLua(luaCodeInput)
                                        rustLiveStats = NativeEngineBridge.getRustTelemetry()
                                        isExecuting = false
                                    }
                                },
                                enabled = !isExecuting,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StudioPlayGreen,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("btn_run_lua_script")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isExecuting) "Ejecutando..." else "Ejecutar",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Snippets rápidos
                        Text(text = "Plantillas rápidas:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SnippetButton("Hola Mundo") {
                                luaCodeInput = "print('Hola desde Lua 5.4.9 puro!')\nprint('Matemáticas: 2^10 = ' .. (2^10))"
                            }
                            SnippetButton("Simulación Rust") {
                                luaCodeInput = "local ticks = engine.step(0.016)\nprint('Rust Ticks: ' .. ticks)\nprint(engine.rust_stats())"
                            }
                            SnippetButton("Mover Actor") {
                                luaCodeInput = "actor.spawn('enemigo', 50, 80)\nactor.move('enemigo', 20, 10)\nlocal x, y = actor.get_coords('enemigo')\nprint('Enemigo en: ' .. x .. ', ' .. y)"
                            }
                            SnippetButton("GC Generacional") {
                                luaCodeInput = "-- Probar el GC generacional introducido en Lua 5.4\ncollectgarbage('generational')\nprint('Modo GC: Generacional activado')\nprint('Memoria usada: ' .. collectgarbage('count') .. ' KB')"
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = luaCodeInput,
                            onValueChange = { luaCodeInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .testTag("input_lua_code"),
                            shape = RoundedCornerShape(10.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = StudioPrimary,
                                unfocusedBorderColor = StudioBorder,
                                focusedContainerColor = Color(0xFF0D1117),
                                unfocusedContainerColor = Color(0xFF0D1117)
                            )
                        )
                    }
                }
            }

            // TARJETA 4: Terminal y Registro de Salida en Tiempo Real
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Terminal,
                                    contentDescription = null,
                                    tint = StudioTertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Terminal de Salida (Logs)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "${consoleLogs.size} líneas",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D1117))
                                .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            if (consoleLogs.isEmpty()) {
                                Text(
                                    text = "No hay registros aún. Presiona 'Ejecutar' para ver la salida de Lua 5.4.",
                                    color = Color.Gray,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(consoleLogs) { logLine ->
                                        val isError = logLine.contains("[Error]") || logLine.contains("[Excepción]") || logLine.contains("[Lua 5.4 Error]")
                                        Text(
                                            text = logLine,
                                            color = if (isError) Color(0xFFFF6B6B) else Color(0xFF58A6FF),
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun SubsystemBadge(
    title: String,
    subtitle: String,
    detail: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = tint.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, tint.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = tint)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = detail, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SnippetButton(
    title: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = title, fontSize = 11.sp)
    }
}
