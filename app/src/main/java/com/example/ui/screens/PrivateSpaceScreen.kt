package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenDestination
import com.example.ui.components.GradientButton
import com.example.ui.components.MulticlonxCard
import com.example.ui.components.PinKeypad
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
fun PrivateSpaceScreen(
    uiState: MulticlonxUiState,
    viewModel: MulticlonxViewModel,
    modifier: Modifier = Modifier
) {
    var showForgotPinDialog by remember { mutableStateOf(false) }
    var isInputtingPin by remember { mutableStateOf(false) }

    val isLocked = uiState.securityState.isPrivateSpaceLocked
    val privateClones = uiState.clonedApps.filter { it.isPrivateSpace }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        if (isLocked) {
            if (isInputtingPin) {
                // PIN Unlock Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            TextButton(onClick = { isInputtingPin = false }) {
                                Text("Cancel", color = SecondaryText, fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ElevatedSurface)
                                .border(1.dp, CyanAccent.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Unlock Multiclonx",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Enter your 6-digit PIN to access Private Space",
                            fontSize = 13.sp,
                            color = SecondaryText
                        )

                        if (uiState.pinErrorMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = uiState.pinErrorMessage,
                                color = ErrorRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Keypad
                    PinKeypad(
                        currentInput = uiState.pinInputBuffer,
                        onDigitClick = { viewModel.inputPinDigit(it) },
                        onBackspace = { viewModel.deletePinDigit() },
                        onBiometricClick = if (uiState.securityState.isBiometricsEnabled) {
                            { viewModel.unlockWithBiometrics() }
                        } else null
                    )

                    // Forgot PIN Link
                    TextButton(
                        onClick = { showForgotPinDialog = true },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Forgot PIN?",
                            color = CyanAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // Locked Welcome State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(DeepIndigo.copy(alpha = 0.7f), Color.Transparent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(CardBackground)
                                .border(1.5.dp, CyanAccent.copy(alpha = 0.8f), RoundedCornerShape(26.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(54.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = PrimaryText,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "Private Space",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "A protected area for your selected apps.",
                        fontSize = 15.sp,
                        color = SecondaryText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Lock Status Card
                    MulticlonxCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = SecondaryBackground
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Locked & Encrypted",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryText
                                )
                                Text(
                                    text = "Requires PIN or biometric authorization",
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    GradientButton(
                        text = "Unlock Private Space",
                        icon = Icons.Default.LockOpen,
                        onClick = { isInputtingPin = true },
                        testTag = "unlock_private_space_button"
                    )
                }
            }
        } else {
            // Unlocked State: Display private apps grid and controls
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Unlocked Header
                item(span = { GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DeepIndigo),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Private Space",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText
                                )
                                Text(
                                    text = "Auto-lock: ${uiState.securityState.autoLockTimeout.label}",
                                    fontSize = 12.sp,
                                    color = CyanAccent
                                )
                            }
                        }

                        // Lock Button in Header
                        IconButton(
                            onClick = { viewModel.lockPrivateSpace() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ElevatedSurface)
                                .border(1.dp, BorderColor, CircleShape)
                                .testTag("lock_private_space_header_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock",
                                tint = PrimaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Clarification disclaimer
                item(span = { GridItemSpan(2) }) {
                    MulticlonxCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = SecondaryBackground
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Private Space controls access within Multiclonx. Clones are isolated inside our application container.",
                                fontSize = 12.sp,
                                color = SecondaryText,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Add to Private Space Button
                item(span = { GridItemSpan(2) }) {
                    GradientButton(
                        text = "+ Add to Private Space",
                        onClick = { viewModel.navigateTo(ScreenDestination.CloneCatalog) },
                        testTag = "add_to_private_space_button"
                    )
                }

                if (privateClones.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        MulticlonxCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            backgroundColor = CardBackground
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No private apps yet",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Clones moved here will be hidden from the home dashboard and protected by your PIN.",
                                    fontSize = 13.sp,
                                    color = SecondaryText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(privateClones, key = { it.id }) { clone ->
                        ClonedAppCard(
                            clone = clone,
                            onOpen = { viewModel.launchClone(clone) },
                            onDetails = { viewModel.navigateTo(ScreenDestination.ClonedAppDetail(clone.id)) },
                            onRename = { viewModel.promptRenameClone(clone) },
                            onTogglePrivate = { viewModel.togglePrivateSpaceForClone(clone.id) },
                            onRemove = { viewModel.promptDeleteClone(clone) }
                        )
                    }
                }
            }
        }

        // Forgot PIN Dialog
        if (showForgotPinDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPinDialog = false },
                containerColor = SecondaryBackground,
                title = { Text("Reset PIN", color = PrimaryText) },
                text = {
                    Text(
                        text = "For security, your default PIN in this environment is 123456. You can re-configure your PIN in Security Settings.",
                        color = SecondaryText,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showForgotPinDialog = false
                            isInputtingPin = false
                            viewModel.navigateTo(ScreenDestination.SecuritySettingsScreen)
                        }
                    ) {
                        Text("Go to Settings", color = CyanAccent)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showForgotPinDialog = false }) {
                        Text("Close", color = SecondaryText)
                    }
                }
            )
        }
    }
}
