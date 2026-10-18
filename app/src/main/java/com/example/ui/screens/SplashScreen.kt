package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenDestination
import com.example.ui.components.MulticlonxLogo
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText

@Composable
fun SplashScreen(
    onContinue: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .clickable { onContinue() },
        contentAlignment = Alignment.Center
    ) {
        // Dramatic background radial lighting
        Box(
            modifier = Modifier
                .size(320.dp * glowScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            DeepIndigo.copy(alpha = 0.55f),
                            PrimaryPurple.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Centered Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            MulticlonxLogo(
                size = 92.dp,
                showWordmark = false,
                isAnimated = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.foundation.layout.Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MULTI",
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp,
                        letterSpacing = 1.sp,
                        color = PrimaryText
                    )
                    Text(
                        text = "CLONX",
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp,
                        letterSpacing = 1.sp,
                        color = CyanAccent
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "One phone. Multiple accounts.",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = SecondaryText,
                    letterSpacing = 0.2.sp
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = CyanAccent,
                strokeWidth = 2.5.dp
            )
        }

        // Tap hint at bottom
        Text(
            text = "Tap to enter",
            fontSize = 12.sp,
            color = SecondaryText.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        )
    }
}
