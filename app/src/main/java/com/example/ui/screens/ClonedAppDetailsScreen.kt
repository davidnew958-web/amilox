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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenDestination
import com.example.ui.components.AppIconView
import com.example.ui.components.GradientButton
import com.example.ui.components.MulticlonxCard
import com.example.ui.components.SecondaryButton
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
import com.example.viewmodel.MulticlonxUiState
import com.example.viewmodel.MulticlonxViewModel

@Composable
fun ClonedAppDetailsScreen(
    cloneId: String,
    uiState: MulticlonxUiState,
    viewModel: MulticlonxViewModel,
    modifier: Modifier = Modifier
) {
    val clone = uiState.clonedApps.find { it.id == cloneId }
    var menuExpanded by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    if (clone == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MainBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("App clone not found", color = SecondaryText)
        }
        return
    }

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
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ElevatedSurface)
                        .testTag("back_button_details")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = PrimaryText,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "App Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ElevatedSurface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = PrimaryText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(ElevatedSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Rename", color = PrimaryText) },
                            onClick = {
                                menuExpanded = false
                                viewModel.promptRenameClone(clone)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Remove Clone", color = ErrorRed) },
                            onClick = {
                                menuExpanded = false
                                viewModel.promptDeleteClone(clone)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Help", color = PrimaryText) },
                            onClick = {
                                menuExpanded = false
                                showHelpDialog = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Large App Icon & Details Hero
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppIconView(
                    iconKey = clone.iconKey,
                    size = 90.dp,
                    cloneBadgeNumber = clone.cloneNumber,
                    isPrivateSpace = clone.isPrivateSpace
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = clone.name,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = clone.accountLabel,
                    fontSize = 15.sp,
                    color = CyanAccent,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Clone Status Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElevatedSurface)
                        .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (clone.isPrivateSpace) CyanAccent else SuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (clone.isPrivateSpace) "Isolated (Private Space)" else "Sandboxed (Ready)",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Button: Open App
            GradientButton(
                text = "Open App",
                icon = Icons.Default.PlayArrow,
                onClick = { viewModel.launchClone(clone) },
                testTag = "open_app_details_button"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Button: Rename Account
            SecondaryButton(
                text = "Rename Account",
                icon = Icons.Default.Edit,
                onClick = { viewModel.promptRenameClone(clone) },
                testTag = "rename_account_details_button"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Add Another Clone Button
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground,
                onClick = {
                    val catItem = uiState.catalogApps.find { it.packageName == clone.packageName }
                    if (catItem != null) {
                        viewModel.navigateTo(ScreenDestination.CreateClone(catItem))
                    }
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Add Another Clone",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                    }
                    Text(text = "→", color = SecondaryText, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Privacy Lock Shortcut
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground,
                onClick = { viewModel.togglePrivateSpaceForClone(clone.id) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (clone.isPrivateSpace) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (clone.isPrivateSpace) CyanAccent else SecondaryText,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (clone.isPrivateSpace) "Remove from Private Space" else "Move to Private Space",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryText
                            )
                            Text(
                                text = if (clone.isPrivateSpace) "Clone is protected by PIN lock" else "Expose in main dashboard",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Storage Breakdown Card
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SecondaryBackground
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Storage Usage",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryText
                            )
                        }

                        Text(
                            text = "${String.format("%.1f", clone.storageUsageMb + clone.cacheUsageMb)} MB",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("App Sandbox Data", color = SecondaryText, fontSize = 13.sp)
                        Text("${String.format("%.1f", clone.storageUsageMb)} MB", color = PrimaryText, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cached Session Files", color = SecondaryText, fontSize = 13.sp)
                        Text("${String.format("%.1f", clone.cacheUsageMb)} MB", color = PrimaryText, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Clear Cache Action
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElevatedSurface)
                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                            .clickable { viewModel.clearCloneCache(clone.id) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = null,
                                tint = SecondaryText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Clear Clone Cache",
                                fontSize = 13.sp,
                                color = PrimaryText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Help Information Dialog
        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                containerColor = SecondaryBackground,
                title = { Text("About This Clone", color = PrimaryText) },
                text = {
                    Text(
                        text = "This clone operates inside Multiclonx's sandboxed virtual application layer. It stores session keys, database files, and caches independently without conflicting with other accounts.",
                        color = SecondaryText,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showHelpDialog = false }) {
                        Text("Got it", color = CyanAccent)
                    }
                }
            )
        }
    }
}
