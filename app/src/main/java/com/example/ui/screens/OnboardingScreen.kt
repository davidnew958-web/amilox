package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppIconView
import com.example.ui.components.GradientButton
import com.example.ui.components.MulticlonxLogo
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SuccessGreen

@Composable
fun OnboardingScreen(
    currentPage: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onGetStarted: () -> Unit,
    onAlreadyHaveAccount: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Skip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MulticlonxLogo(size = 32.dp, showWordmark = true, wordmarkSize = 16.sp)

                if (currentPage < 2) {
                    TextButton(onClick = onSkip) {
                        Text(
                            text = "Skip",
                            color = SecondaryText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.15f))

            // Animated Screen Illustration & Texts
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboarding_content",
                modifier = Modifier.weight(1f)
            ) { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (page) {
                        0 -> OnboardingIllustrationPage1()
                        1 -> OnboardingIllustrationPage2()
                        else -> OnboardingIllustrationPage3()
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = when (page) {
                            0 -> "Two Accounts, One Device"
                            1 -> "Your Apps, Your Privacy"
                            else -> "Make Room for More"
                        },
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = when (page) {
                            0 -> "Manage multiple accounts for your favorite supported apps seamlessly without constant re-logging."
                            1 -> "Protect access to your private space with a PIN or supported biometric authentication."
                            else -> "Organize your cloned apps in one convenient, secure place with dedicated isolated storage."
                        },
                        fontSize = 15.sp,
                        color = SecondaryText,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Pagination Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 28.dp)
            ) {
                for (i in 0..2) {
                    val isActive = i == currentPage
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (isActive) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isActive) CyanAccent else BorderColor)
                    )
                }
            }

            // Actions
            if (currentPage < 2) {
                GradientButton(
                    text = "Next",
                    onClick = onNext,
                    testTag = "onboarding_next_button"
                )
            } else {
                GradientButton(
                    text = "Get Started",
                    onClick = onGetStarted,
                    testTag = "onboarding_get_started_button"
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = onAlreadyHaveAccount,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "I Already Have an Account",
                        color = CyanAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OnboardingIllustrationPage1() {
    // Floating duplicate social app icons inside glowing rounded squares
    Box(
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glowing background orb
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(DeepIndigo.copy(alpha = 0.6f), Color.Transparent)
                    )
                )
        )

        // WhatsApp duplicate cards
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(20.dp))
                .background(CardBackground)
                .border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            AppIconView(iconKey = "whatsapp", size = 52.dp, cloneBadgeNumber = 1)
        }

        Box(
            modifier = Modifier
                .size(88.dp)
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(22.dp))
                .background(CardBackground)
                .border(1.5.dp, PrimaryPurple.copy(alpha = 0.7f), RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            AppIconView(iconKey = "whatsapp", size = 56.dp, cloneBadgeNumber = 2)
        }

        // Instagram and Telegram duplicates
        Box(
            modifier = Modifier
                .size(82.dp)
                .align(Alignment.BottomStart)
                .clip(RoundedCornerShape(20.dp))
                .background(CardBackground)
                .border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            AppIconView(iconKey = "instagram", size = 52.dp, cloneBadgeNumber = 1)
        }

        Box(
            modifier = Modifier
                .size(86.dp)
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(22.dp))
                .background(CardBackground)
                .border(1.5.dp, CyanAccent.copy(alpha = 0.7f), RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            AppIconView(iconKey = "telegram", size = 54.dp, cloneBadgeNumber = 2)
        }
    }
}

@Composable
private fun OnboardingIllustrationPage2() {
    // Glowing shield with a lock and geometric particles
    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(PrimaryPurple.copy(alpha = 0.45f), CyanAccent.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )

        // Shield outer container
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.linearGradient(listOf(DeepIndigo, Color(0xFF141930)))
                )
                .border(2.dp, CyanAccent.copy(alpha = 0.8f), RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(72.dp)
            )
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = PrimaryText,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
private fun OnboardingIllustrationPage3() {
    // Modern phone dashboard displaying multiple app icons
    Box(
        modifier = Modifier
            .size(width = 220.dp, height = 240.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(SecondaryBackground)
            .border(1.5.dp, PrimaryPurple.copy(alpha = 0.6f), RoundedCornerShape(28.dp))
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Mini header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 60.dp, height = 8.dp)
                        .clip(CircleShape)
                        .background(BorderColor)
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen)
                )
            }

            // Mini App Grid inside mockup
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AppIconView(iconKey = "whatsapp", size = 42.dp, cloneBadgeNumber = 1)
                AppIconView(iconKey = "instagram", size = 42.dp, cloneBadgeNumber = 1)
                AppIconView(iconKey = "facebook", size = 42.dp, cloneBadgeNumber = 2)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AppIconView(iconKey = "telegram", size = 42.dp, cloneBadgeNumber = 1)
                AppIconView(iconKey = "discord", size = 42.dp, cloneBadgeNumber = 1)
                AppIconView(iconKey = "tiktok", size = 42.dp, cloneBadgeNumber = 2)
            }

            // Bottom docking row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ElevatedSurface)
                    .border(1.dp, BorderColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SANDBOX SPACE ACTIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
