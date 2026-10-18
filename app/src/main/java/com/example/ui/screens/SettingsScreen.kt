package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenDestination
import com.example.ui.components.MulticlonxCard
import com.example.ui.components.MulticlonxLogo
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.viewmodel.MulticlonxUiState
import com.example.viewmodel.MulticlonxViewModel

@Composable
fun SettingsScreen(
    uiState: MulticlonxUiState,
    viewModel: MulticlonxViewModel,
    modifier: Modifier = Modifier
) {
    var infoDialogTitle by remember { mutableStateOf<String?>(null) }
    var infoDialogBody by remember { mutableStateOf<String?>(null) }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Settings",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            }

            // Group: Account
            SettingsSectionHeader(title = "Account")
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.Person,
                        title = if (uiState.userAccount.isSignedIn) uiState.userAccount.fullName else "Profile",
                        subtitle = if (uiState.userAccount.isSignedIn) uiState.userAccount.email else "alex.vance@multiclonx.net",
                        onClick = {
                            if (uiState.userAccount.isSignedIn) {
                                infoDialogTitle = "User Profile"
                                infoDialogBody = "Logged in as ${uiState.userAccount.email} (${uiState.currentPlan.title})"
                            } else {
                                viewModel.navigateTo(ScreenDestination.SignIn)
                            }
                        }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Default.Email,
                        title = if (uiState.userAccount.isSignedIn) "Sign Out" else "Sign In / Sign Up",
                        subtitle = if (uiState.userAccount.isSignedIn) "Disconnect from Multiclonx cloud profile" else "Sync your account configuration",
                        onClick = {
                            if (uiState.userAccount.isSignedIn) {
                                viewModel.signOut()
                            } else {
                                viewModel.navigateTo(ScreenDestination.SignIn)
                            }
                        }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Default.WorkspacePremium,
                        title = "Subscription Management",
                        subtitle = "Current: ${uiState.currentPlan.title}",
                        onClick = { viewModel.navigateTo(ScreenDestination.Premium) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Group: Security
            SettingsSectionHeader(title = "Security")
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.Lock,
                        title = "App Lock",
                        subtitle = if (uiState.securityState.isAppLockEnabled) "Enabled" else "Disabled",
                        onClick = { viewModel.navigateTo(ScreenDestination.SecuritySettingsScreen) }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Default.Fingerprint,
                        title = "PIN & Biometrics",
                        subtitle = "Configure 6-digit passcode and fingerprint",
                        onClick = { viewModel.navigateTo(ScreenDestination.SecuritySettingsScreen) }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Default.Shield,
                        title = "Private Space",
                        subtitle = "Protected partition for isolated clones",
                        onClick = { viewModel.navigateTo(ScreenDestination.PrivateSpace) }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Default.Security,
                        title = "Privacy Settings",
                        subtitle = "Zero-telemetry local sandbox isolation",
                        onClick = {
                            infoDialogTitle = "Privacy Settings"
                            infoDialogBody = "Multiclonx maintains zero external telemetry. All cloned application instances run strictly locally on this device with sandboxed storage namespaces."
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Group: Preferences
            SettingsSectionHeader(title = "Preferences")
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.DarkMode,
                        title = "Theme",
                        subtitle = "Dark Mode Only (Permanent)",
                        badgeText = "Always On",
                        onClick = {
                            viewModel.showToast("Multiclonx is exclusively crafted for Dark Mode.")
                        }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = "English (US)",
                        onClick = {
                            infoDialogTitle = "Language"
                            infoDialogBody = "Multiclonx currently defaults to English (US). Additional localization packs are coming in future releases."
                        }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Clone container alerts and status",
                        onClick = {
                            viewModel.showToast("App notifications are enabled.")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Group: Support & Showcase
            SettingsSectionHeader(title = "Support & UI Showcase")
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.ViewQuilt,
                        title = "UI Component Showcase",
                        subtitle = "Inspect all buttons, chips, badges, and states",
                        badgeText = "Showcase",
                        onClick = { viewModel.navigateTo(ScreenDestination.ComponentShowcase) }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Default.Help,
                        title = "Help Center",
                        subtitle = "Frequently asked questions and guides",
                        onClick = {
                            infoDialogTitle = "Help Center"
                            infoDialogBody = "How does cloning work?\nMulticlonx generates isolated virtual sandbox spaces. Each cloned app has its own separate database, preferences, and cache directories so you can log into multiple accounts at once."
                        }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Default.SupportAgent,
                        title = "Contact Support",
                        subtitle = "support@multiclonx.net",
                        onClick = {
                            viewModel.showToast("Support email copied: support@multiclonx.net")
                        }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Default.ReportProblem,
                        title = "Report a Problem",
                        subtitle = "Submit compatibility feedback",
                        onClick = {
                            viewModel.showToast("Feedback form submitted.")
                        }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        icon = Icons.Default.Info,
                        title = "About Multiclonx",
                        subtitle = "Version 2.4.0 (Build 412 Pro)",
                        onClick = {
                            infoDialogTitle = "About Multiclonx"
                            infoDialogBody = "Multiclonx v2.4.0 Pro\nTagline: One phone. Multiple accounts.\nEngineered for modern Android devices with strict dark theme and localized sandbox storage."
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Legal & Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MulticlonxLogo(size = 28.dp, showWordmark = true, wordmarkSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Multiclonx v2.4.0 • Built for Android",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
        }

        // Generic Info Dialog
        if (infoDialogTitle != null) {
            AlertDialog(
                onDismissRequest = {
                    infoDialogTitle = null
                    infoDialogBody = null
                },
                containerColor = SecondaryBackground,
                title = { Text(infoDialogTitle!!, color = PrimaryText) },
                text = { Text(infoDialogBody!!, color = SecondaryText, fontSize = 14.sp) },
                confirmButton = {
                    TextButton(onClick = {
                        infoDialogTitle = null
                        infoDialogBody = null
                    }) {
                        Text("OK", color = CyanAccent)
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = CyanAccent,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BorderColor.copy(alpha = 0.5f))
    )
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ElevatedSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badgeText != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimaryPurple.copy(alpha = 0.3f))
                        .border(1.dp, PrimaryPurple, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
