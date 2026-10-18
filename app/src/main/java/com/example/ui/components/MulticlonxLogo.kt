package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText

@Composable
fun MulticlonxLogo(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    showWordmark: Boolean = false,
    wordmarkSize: TextUnit = 20.sp,
    showTagline: Boolean = false,
    isAnimated: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_anim")
    val glowAlpha by if (isAnimated) {
        infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glow_alpha"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.7f) }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val width = this.size.width
            val height = this.size.height

            // Dimensions for the 2 overlapping squares
            val squareSide = width * 0.62f
            val cornerRadius = CornerRadius(squareSide * 0.28f, squareSide * 0.28f)

            // Back square (Deep Indigo / Purple gradient)
            val backOffset = Offset(width * 0.38f, height * 0.04f)
            val backBrush = Brush.linearGradient(
                colors = listOf(
                    PrimaryPurple.copy(alpha = glowAlpha),
                    DeepIndigo
                ),
                start = backOffset,
                end = Offset(backOffset.x + squareSide, backOffset.y + squareSide)
            )

            drawRoundRect(
                brush = backBrush,
                topLeft = backOffset,
                size = Size(squareSide, squareSide),
                cornerRadius = cornerRadius
            )

            // Back square subtle cyan rim
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(CyanAccent.copy(alpha = 0.6f * glowAlpha), Color.Transparent)
                ),
                topLeft = backOffset,
                size = Size(squareSide, squareSide),
                cornerRadius = cornerRadius,
                style = Stroke(width = width * 0.035f)
            )

            // Front square (Cyan to Purple gradient - representing the cloned layer)
            val frontOffset = Offset(width * 0.02f, height * 0.34f)
            val frontBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF38D9F5),
                    Color(0xFF7047FF)
                ),
                start = frontOffset,
                end = Offset(frontOffset.x + squareSide, frontOffset.y + squareSide)
            )

            drawRoundRect(
                brush = frontBrush,
                topLeft = frontOffset,
                size = Size(squareSide, squareSide),
                cornerRadius = cornerRadius
            )

            // Front square inner highlight stroke
            drawRoundRect(
                color = Color.White.copy(alpha = 0.45f),
                topLeft = frontOffset,
                size = Size(squareSide, squareSide),
                cornerRadius = cornerRadius,
                style = Stroke(width = width * 0.03f)
            )

            // Tiny duplication motif in center of front square (two mini layered bars)
            val motifCenter = Offset(frontOffset.x + squareSide / 2, frontOffset.y + squareSide / 2)
            val motifSize = squareSide * 0.36f
            val miniRadius = CornerRadius(motifSize * 0.22f, motifSize * 0.22f)

            drawRoundRect(
                color = Color.White.copy(alpha = 0.85f),
                topLeft = Offset(motifCenter.x - motifSize * 0.4f, motifCenter.y - motifSize * 0.4f),
                size = Size(motifSize * 0.8f, motifSize * 0.8f),
                cornerRadius = miniRadius,
                style = Stroke(width = width * 0.035f)
            )
        }

        if (showWordmark) {
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MULTI",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        fontSize = wordmarkSize,
                        letterSpacing = 0.5.sp,
                        color = PrimaryText
                    )
                    Text(
                        text = "CLONX",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        fontSize = wordmarkSize,
                        letterSpacing = 0.5.sp,
                        color = CyanAccent
                    )
                }
                if (showTagline) {
                    Text(
                        text = "One phone. Multiple accounts.",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = SecondaryText
                    )
                }
            }
        }
    }
}
