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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlockCategory
import com.example.data.model.BlockShape
import com.example.data.model.BlockTemplate
import com.example.data.model.PlacedBlock

/**
 * Componentes visuales para representar bloques de programación estilo Pocket Code.
 * Incluyen conectores tipo rompecabezas, códigos de color por categoría,
 * campos de parámetros interactivos y hendiduras visuales.
 */

@Composable
fun PlacedBlockCard(
    block: PlacedBlock,
    modifier: Modifier = Modifier,
    onBlockClick: () -> Unit = {}
) {
    val shape = when (block.shape) {
        BlockShape.CAP_HEADER -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
        BlockShape.WRAP_LOOP -> RoundedCornerShape(8.dp)
        BlockShape.BOOLEAN_VALUE -> RoundedCornerShape(50.dp)
        BlockShape.STACK_COMMAND -> RoundedCornerShape(8.dp)
    }

    val indentPadding = (block.indentLevel * 24).dp

    Column(
        modifier = modifier
            .padding(start = indentPadding)
            .fillMaxWidth()
            .testTag("placed_block_${block.id}")
            .clickable { onBlockClick() }
    ) {
        // Pestaña superior del bloque (conector macho/hembra)
        if (block.shape == BlockShape.STACK_COMMAND || block.shape == BlockShape.WRAP_LOOP) {
            Box(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .width(32.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                    .background(block.category.color.copy(alpha = 0.85f))
            )
        }

        // Cuerpo principal del bloque
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, shape = shape),
            shape = shape,
            color = block.category.color,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    // Ícono de arrastre / conector
                    Icon(
                        imageVector = block.category.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 4.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = block.text,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Valor de parámetro si existe
                    if (block.paramValue.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.28f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = block.paramValue,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Handle táctil para reordenar
                Icon(
                    imageVector = Icons.Default.DragIndicator,
                    contentDescription = "Reordenar bloque",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Muesca inferior para conectar el siguiente bloque
        if (block.shape == BlockShape.STACK_COMMAND || block.shape == BlockShape.CAP_HEADER) {
            Box(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .width(32.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                    .background(Color.Black.copy(alpha = 0.25f))
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

/**
 * Plantilla de bloque mostrada en el catálogo / paleta de bloques disponibles.
 * Muestra visualmente qué bloques estarán disponibles para la siguiente etapa.
 */
@Composable
fun BlockPaletteItem(
    template: BlockTemplate,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("palette_item_${template.id}"),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, template.category.color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pill con el color de la categoría
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(template.category.color.copy(alpha = 0.2f))
                    .border(1.5.dp, template.category.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = template.category.icon,
                    contentDescription = null,
                    tint = template.category.color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.label,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (template.description.isNotEmpty()) {
                    Text(
                        text = template.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Etiqueta indicadora
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(template.category.color.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = template.category.title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = template.category.color
                )
            }
        }
    }
}
