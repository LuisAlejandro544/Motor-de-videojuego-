package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VirtualControlsConfig
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPlayGreen
import com.example.ui.theme.StudioPrimary
import com.example.ui.theme.StudioSecondary
import com.example.ui.theme.StudioTertiary
import kotlinx.coroutines.delay

/**
 * Gamepad táctil virtual optimizado para pantallas móviles Android.
 * Contiene:
 * 1. D-Pad Direccional (Arriba, Abajo, Izquierda, Derecha) con soporte de toque continuo.
 * 2. Botones de Acción (Botón A: Disparo/Acción, Botón B: Turbo/Salto).
 * 
 * Permite controlar directamente en tiempo real al Player en la vista previa del juego.
 */
@Composable
fun VirtualGamepad(
    config: VirtualControlsConfig,
    onDirectionHold: (dx: Int, dy: Int) -> Unit,
    onActionA: () -> Unit,
    onActionB: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!config.isEnabled) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // D-Pad Direccional (Lado izquierdo)
        if (config.showDpad) {
            VirtualDpad(
                opacity = config.opacity,
                onDirectionHold = onDirectionHold
            )
        } else {
            Spacer(modifier = Modifier.size(140.dp))
        }

        // Botones de Acción (Lado derecho)
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            if (config.showActionB) {
                VirtualActionButton(
                    label = config.labelB,
                    subLabel = "B",
                    color = StudioTertiary,
                    icon = Icons.Default.RocketLaunch,
                    sizeDp = 64,
                    opacity = config.opacity,
                    onClick = onActionB,
                    testTag = "btn_action_b"
                )
            }

            if (config.showActionA) {
                VirtualActionButton(
                    label = config.labelA,
                    subLabel = "A",
                    color = StudioPlayGreen,
                    icon = Icons.Default.PlayArrow,
                    sizeDp = 72,
                    opacity = config.opacity,
                    onClick = onActionA,
                    testTag = "btn_action_a"
                )
            }
        }
    }
}

/**
 * Cruceta Direccional Virtual (D-Pad).
 * Permite enviar movimiento continuo mientras el usuario mantenga presionado el botón táctil.
 */
@Composable
fun VirtualDpad(
    opacity: Float,
    onDirectionHold: (dx: Int, dy: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(Color(0xFF090D14).copy(alpha = opacity))
            .border(1.5.dp, StudioBorder.copy(alpha = opacity), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Centro estético del D-Pad
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF1B2332).copy(alpha = opacity))
                .border(1.dp, Color(0xFF2E384D).copy(alpha = opacity), CircleShape)
        )

        // Botón ARRIBA
        DpadButton(
            icon = Icons.Default.KeyboardArrowUp,
            dx = 0,
            dy = 1,
            onDirectionHold = onDirectionHold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 6.dp)
                .testTag("dpad_up")
        )

        // Botón ABAJO
        DpadButton(
            icon = Icons.Default.KeyboardArrowDown,
            dx = 0,
            dy = -1,
            onDirectionHold = onDirectionHold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-6).dp)
                .testTag("dpad_down")
        )

        // Botón IZQUIERDA
        DpadButton(
            icon = Icons.Default.KeyboardArrowLeft,
            dx = -1,
            dy = 0,
            onDirectionHold = onDirectionHold,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 6.dp)
                .testTag("dpad_left")
        )

        // Botón DERECHA
        DpadButton(
            icon = Icons.Default.KeyboardArrowRight,
            dx = 1,
            dy = 0,
            onDirectionHold = onDirectionHold,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-6).dp)
                .testTag("dpad_right")
        )
    }
}

/**
 * Botón individual del D-Pad con detección de pulsación continua mediante corrutinas.
 */
@Composable
private fun DpadButton(
    icon: ImageVector,
    dx: Int,
    dy: Int,
    onDirectionHold: (dx: Int, dy: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Bucle continuo mientras el usuario sostiene el dedo sobre la dirección
    LaunchedEffect(isPressed) {
        if (isPressed) {
            while (true) {
                onDirectionHold(dx, dy)
                delay(35) // Despacho regular a ~30Hz
            }
        }
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isPressed) StudioPrimary.copy(alpha = 0.55f)
                else Color(0xFF161E2E)
            )
            .border(
                1.dp,
                if (isPressed) StudioPrimary else Color(0xFF2A364F),
                RoundedCornerShape(10.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onDirectionHold(dx, dy) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isPressed) Color.White else Color(0xFF8B949E),
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Botón de Acción Táctil (A o B) con halo y feedback visual.
 */
@Composable
fun VirtualActionButton(
    label: String,
    subLabel: String,
    color: Color,
    icon: ImageVector,
    sizeDp: Int,
    opacity: Float,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(sizeDp.dp)
                .scale(if (isPressed) 0.92f else 1.0f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = if (isPressed) 0.85f else 0.5f * opacity),
                            Color(0xFF0D1117).copy(alpha = 0.95f * opacity)
                        )
                    )
                )
                .border(
                    width = if (isPressed) 2.5.dp else 1.5.dp,
                    color = if (isPressed) color else color.copy(alpha = 0.6f * opacity),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = subLabel,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = if (isPressed) Color.White else color
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}
