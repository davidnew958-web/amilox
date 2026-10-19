package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCatalogItem
import com.example.model.CompatibilityStatus
import com.example.ui.components.AppIconView
import com.example.ui.components.CompatibilityBadge
import com.example.ui.components.GradientButton
import com.example.ui.components.MulticlonxCard
import com.example.ui.components.SecondaryButton
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.viewmodel.MulticlonxUiState
import com.example.viewmodel.MulticlonxViewModel

@Composable
fun CreateCloneScreen(
    appItem: AppCatalogItem,
    uiState: MulticlonxUiState,
    viewModel: MulticlonxViewModel,
    modifier: Modifier = Modifier
) {
    val existingClones = remember(uiState.clonedApps, appItem.packageName) {
        uiState.clonedApps.filter { it.packageName == appItem.packageName }
    }
    val existingCount = existingClones.size
    val nextCloneNumber = existingCount + 1

    var accountLabel by remember { mutableStateOf("") }
    var isPrivateSpace by remember { mutableStateOf(false) }
    var numberOfCopies by remember { androidx.compose.runtime.mutableIntStateOf(1) }

    val presetLabels = listOf(
        "Account #$nextCloneNumber",
        "Work Account",
        "Personal Account",
        "Second Account",
        "Gaming"
    )

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
            // Top Bar
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
                        .testTag("back_button_create_clone")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = PrimaryText,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (existingCount > 0) "Add Another Copy (${existingCount} Active)" else "Create Clone",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Identity Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppIconView(
                    iconKey = appItem.iconKey,
                    packageName = appItem.packageName,
                    size = 80.dp,
                    cloneBadgeNumber = nextCloneNumber
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = appItem.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = appItem.packageName,
                    fontSize = 12.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(8.dp))

                CompatibilityBadge(status = appItem.compatibility)

                if (existingCount > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElevatedSurface)
                            .border(1.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "You have $existingCount active copy/copies • Creating #${nextCloneNumber}",
                            color = CyanAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Number of Copies Selector Card
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Number of Copies",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                        Text(
                            text = "Create multiple distinct accounts at once",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (numberOfCopies > 1) ElevatedSurface else ElevatedSurface.copy(alpha = 0.4f))
                                .clickable(enabled = numberOfCopies > 1) { numberOfCopies-- },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "-",
                                color = if (numberOfCopies > 1) PrimaryText else SecondaryText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "$numberOfCopies",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (numberOfCopies < 5) ElevatedSurface else ElevatedSurface.copy(alpha = 0.4f))
                                .clickable(enabled = numberOfCopies < 5) { numberOfCopies++ },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+",
                                color = if (numberOfCopies < 5) PrimaryText else SecondaryText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Account Label Input Section
            Text(
                text = "Account Label",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = accountLabel,
                onValueChange = { accountLabel = it },
                placeholder = {
                    Text(
                        text = if (numberOfCopies > 1) "e.g. Account (will append #1, #2...)" else "Account #$nextCloneNumber",
                        color = SecondaryText
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = PrimaryText,
                    unfocusedTextColor = PrimaryText
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_label_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Preset Label Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetLabels.forEach { label ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (accountLabel == label) PrimaryPurple else ElevatedSurface)
                            .border(1.dp, if (accountLabel == label) CyanAccent else BorderColor, RoundedCornerShape(10.dp))
                            .clickable { accountLabel = label }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (accountLabel == label) PrimaryText else SecondaryText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Private Space Switch Card
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ElevatedSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Add to Private Space",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryText
                            )
                            Text(
                                text = "Require PIN or biometric to view this clone.",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        }
                    }

                    Switch(
                        checked = isPrivateSpace,
                        onCheckedChange = { isPrivateSpace = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PrimaryText,
                            checkedTrackColor = PrimaryPurple,
                            uncheckedThumbColor = SecondaryText,
                            uncheckedTrackColor = ElevatedSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            val context = LocalContext.current
            val isCompatible = appItem.compatibility == CompatibilityStatus.COMPATIBLE && appItem.webContainerUrl != null

            // Architecture / Sandbox Information Card
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SecondaryBackground,
                borderColor = if (isCompatible) BorderColor else ErrorRed.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCompatible) Icons.Default.VerifiedUser else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isCompatible) CyanAccent else ErrorRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCompatible) "Isolated Container Support" else "Native Binary - Cloning Restricted",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isCompatible) {
                            "This clone operates within an isolated session partition with dedicated cookies, localStorage, and storage. Completely isolated from your primary account."
                        } else {
                            "Android OS security enforcement (Linux UID isolation, SELinux, and hardened ART) prevents unprivileged user-space applications from virtualizing secondary native binary instances without a Managed Work Profile."
                        },
                        fontSize = 12.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Primary and Secondary Action Buttons
            if (isCompatible) {
                val buttonText = when {
                    numberOfCopies > 1 -> "Create $numberOfCopies Isolated Copies"
                    existingCount > 0 -> "Create Isolated Copy #$nextCloneNumber"
                    else -> "Create Isolated Clone"
                }
                GradientButton(
                    text = buttonText,
                    onClick = {
                        viewModel.startCloneCreation(
                            appItem = appItem,
                            accountLabel = accountLabel,
                            isPrivate = isPrivateSpace,
                            context = context,
                            count = numberOfCopies
                        )
                    },
                    isLoading = uiState.isCreatingClone,
                    testTag = "submit_create_clone_button"
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ElevatedSurface)
                        .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Native Binary - Cloning Unsupported",
                        color = SecondaryText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryButton(
                text = "Cancel",
                onClick = { viewModel.navigateBack() },
                testTag = "cancel_create_clone_button"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
