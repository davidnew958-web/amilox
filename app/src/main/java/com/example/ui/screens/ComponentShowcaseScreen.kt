package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CompatibilityStatus
import com.example.ui.components.AppIconView
import com.example.ui.components.CompatibilityBadge
import com.example.ui.components.GradientButton
import com.example.ui.components.MulticlonxCard
import com.example.ui.components.SecondaryButton
import com.example.ui.components.UsageProgressBar
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.MulticlonxViewModel

@Composable
fun ComponentShowcaseScreen(
    viewModel: MulticlonxViewModel,
    modifier: Modifier = Modifier
) {
    var sampleToggle by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ElevatedSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = PrimaryText
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "UI Component Library",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Buttons Showcase
            ShowcaseSectionTitle("1. Action Buttons")
            GradientButton(
                text = "Primary Gradient Button",
                icon = Icons.Default.PlayArrow,
                onClick = { viewModel.showToast("Clicked Primary Button") }
            )
            Spacer(modifier = Modifier.height(10.dp))
            GradientButton(
                text = "Loading State Button",
                isLoading = true,
                onClick = {}
            )
            Spacer(modifier = Modifier.height(10.dp))
            GradientButton(
                text = "Disabled Button",
                isEnabled = false,
                onClick = {}
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = "Secondary Elevated Button",
                onClick = { viewModel.showToast("Clicked Secondary Button") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Badges and Chips
            ShowcaseSectionTitle("2. Status Badges & Compatibility")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CompatibilityBadge(status = CompatibilityStatus.COMPATIBLE)
                CompatibilityBadge(status = CompatibilityStatus.CHECKING)
                CompatibilityBadge(status = CompatibilityStatus.UNSUPPORTED)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Realistic Icons with Badges
            ShowcaseSectionTitle("3. Realistic Application Icons")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AppIconView(iconKey = "whatsapp", size = 52.dp, cloneBadgeNumber = 1)
                AppIconView(iconKey = "instagram", size = 52.dp, cloneBadgeNumber = 2)
                AppIconView(iconKey = "telegram", size = 52.dp, isPrivateSpace = true)
                AppIconView(iconKey = "discord", size = 52.dp, cloneBadgeNumber = 1)
                AppIconView(iconKey = "tiktok", size = 52.dp, cloneBadgeNumber = 2)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Progress and Metrics
            ShowcaseSectionTitle("4. Usage Progress Indicator")
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    UsageProgressBar(used = 2, total = 2)
                    Spacer(modifier = Modifier.height(14.dp))
                    UsageProgressBar(used = 4, total = 10)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. System States: Success, Warning, Error
            ShowcaseSectionTitle("5. System Feedback States")
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground,
                borderColor = SuccessGreen.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Clone created successfully.", color = PrimaryText, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground,
                borderColor = WarningAmber.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = WarningAmber)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Additional permission is required to continue.", color = PrimaryText, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground,
                borderColor = ErrorRed.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = ErrorRed)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("This app isn't supported on your device.", color = PrimaryText, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Interactive Toggles
            ShowcaseSectionTitle("6. Controls & Toggles")
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto-lock Sandbox on Exit", color = PrimaryText, fontSize = 14.sp)
                    Switch(
                        checked = sampleToggle,
                        onCheckedChange = { sampleToggle = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PrimaryText,
                            checkedTrackColor = PrimaryPurple,
                            uncheckedThumbColor = SecondaryText,
                            uncheckedTrackColor = ElevatedSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun ShowcaseSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = CyanAccent,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}
