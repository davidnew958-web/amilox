package com.example.ui.screens

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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClonedApp
import com.example.model.PlanTier
import com.example.model.ScreenDestination
import com.example.ui.components.AppIconView
import com.example.ui.components.GradientButton
import com.example.ui.components.MulticlonxCard
import com.example.ui.components.MulticlonxLogo
import com.example.ui.components.UsageProgressBar
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MainBackground
import com.example.ui.theme.NavActiveColor
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.viewmodel.MulticlonxUiState
import com.example.viewmodel.MulticlonxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: MulticlonxUiState,
    viewModel: MulticlonxViewModel,
    modifier: Modifier = Modifier
) {
    var showNotificationSheet by remember { mutableStateOf(false) }

    val homeClones = uiState.clonedApps.filter { !it.isPrivateSpace }
    val filteredClones = if (uiState.homeSearchQuery.isBlank()) {
        homeClones
    } else {
        homeClones.filter {
            it.name.contains(uiState.homeSearchQuery, ignoreCase = true) ||
            it.accountLabel.contains(uiState.homeSearchQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header: Logo, Notification, Profile
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MulticlonxLogo(
                        size = 38.dp,
                        showWordmark = true,
                        wordmarkSize = 18.sp,
                        showTagline = false
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Notifications
                        Box(contentAlignment = Alignment.TopEnd) {
                            IconButton(
                                onClick = {
                                    showNotificationSheet = true
                                    viewModel.markNotificationsRead()
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(ElevatedSurface)
                                    .border(1.dp, BorderColor, CircleShape)
                                    .testTag("notification_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = PrimaryText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            if (uiState.notifications.any { it.isUnread }) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(CyanAccent)
                                        .border(1.5.dp, MainBackground, CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Profile Avatar
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(PrimaryPurple, DeepIndigo))
                                )
                                .border(1.dp, CyanAccent.copy(alpha = 0.5f), CircleShape)
                                .clickable {
                                    viewModel.navigateTo(ScreenDestination.Settings)
                                }
                                .testTag("profile_avatar_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.userAccount.fullName.take(1),
                                color = PrimaryText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Welcome Section
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "Welcome to Multiclonx",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Manage all your accounts in one place.",
                        fontSize = 14.sp,
                        color = SecondaryText
                    )
                }
            }

            // Subscription Overview Card
            item(span = { GridItemSpan(2) }) {
                MulticlonxCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    backgroundColor = SecondaryBackground,
                    borderBrush = Brush.linearGradient(
                        listOf(PrimaryPurple.copy(alpha = 0.6f), CyanAccent.copy(alpha = 0.3f), BorderColor)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            Brush.linearGradient(listOf(DeepIndigo, PrimaryPurple))
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Current plan",
                                        fontSize = 11.sp,
                                        color = SecondaryText
                                    )
                                    Text(
                                        text = uiState.currentPlan.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryText
                                    )
                                }
                            }

                            if (uiState.currentPlan == PlanTier.FREE) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            Brush.horizontalGradient(listOf(PrimaryPurple, DeepIndigo))
                                        )
                                        .border(1.dp, CyanAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                        .clickable {
                                            viewModel.navigateTo(ScreenDestination.Premium)
                                        }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("upgrade_to_pro_button")
                                ) {
                                    Text(
                                        text = "Upgrade to Pro",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryText
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        UsageProgressBar(
                            used = homeClones.size,
                            total = uiState.currentPlan.maxClones
                        )
                    }
                }
            }

            // Main Action Button: + Add App
            item(span = { GridItemSpan(2) }) {
                GradientButton(
                    text = "+ Add App",
                    onClick = {
                        viewModel.navigateTo(ScreenDestination.CloneCatalog)
                    },
                    modifier = Modifier.padding(vertical = 4.dp),
                    testTag = "add_app_main_button"
                )
            }

            // Cloned Apps Section Header + Search
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Cloned Apps",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = "${homeClones.size} active",
                            fontSize = 13.sp,
                            color = CyanAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.homeSearchQuery,
                        onValueChange = { viewModel.setHomeSearch(it) },
                        placeholder = {
                            Text(
                                text = "Search your apps",
                                color = SecondaryText,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = SecondaryText
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
                            .testTag("search_cloned_apps_input")
                    )
                }
            }

            // Empty state if no clones found
            if (filteredClones.isEmpty()) {
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
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(ElevatedSurface)
                                    .border(1.dp, BorderColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Your space is waiting",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (uiState.homeSearchQuery.isBlank()) {
                                    "No cloned applications yet. Tap below to create your first isolated clone."
                                } else {
                                    "No cloned applications match \"${uiState.homeSearchQuery}\"."
                                },
                                fontSize = 13.sp,
                                color = SecondaryText,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            GradientButton(
                                text = "Add Your First App",
                                onClick = {
                                    viewModel.navigateTo(ScreenDestination.CloneCatalog)
                                },
                                testTag = "add_first_app_empty_button"
                            )
                        }
                    }
                }
            } else {
                // Two-column grid of cloned apps
                items(filteredClones, key = { it.id }) { clone ->
                    ClonedAppCard(
                        clone = clone,
                        onOpen = { viewModel.launchClone(clone) },
                        onDetails = { viewModel.navigateTo(ScreenDestination.ClonedAppDetail(clone.id)) },
                        onRename = { viewModel.promptRenameClone(clone) },
                        onTogglePrivate = { viewModel.togglePrivateSpaceForClone(clone.id) },
                        onRemove = { viewModel.promptDeleteClone(clone) },
                        onDuplicate = { viewModel.duplicateClone(clone) }
                    )
                }
            }

            // Compact Upgrade Promotion (for free users)
            if (uiState.currentPlan == PlanTier.FREE) {
                item(span = { GridItemSpan(2) }) {
                    MulticlonxCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        cornerRadius = 18.dp,
                        backgroundColor = SecondaryBackground,
                        borderBrush = Brush.linearGradient(
                            listOf(CyanAccent.copy(alpha = 0.4f), PrimaryPurple.copy(alpha = 0.5f))
                        )
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
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            Brush.linearGradient(listOf(DeepIndigo, PrimaryPurple))
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Diamond,
                                        contentDescription = "Premium",
                                        tint = CyanAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Unlock More Accounts",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryText
                                    )
                                    Text(
                                        text = "Get more room for your supported apps.",
                                        fontSize = 12.sp,
                                        color = SecondaryText
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ElevatedSurface)
                                    .border(1.dp, CyanAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.navigateTo(ScreenDestination.Premium)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("explore_premium_promo_button")
                            ) {
                                Text(
                                    text = "Explore Premium",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CyanAccent
                                )
                            }
                        }
                    }
                }
            }
        }

        // Notification Modal Bottom Sheet
        if (showNotificationSheet) {
            ModalBottomSheet(
                onDismissRequest = { showNotificationSheet = false },
                containerColor = SecondaryBackground,
                contentColor = PrimaryText,
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Notifications",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    uiState.notifications.forEach { notif ->
                        MulticlonxCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            backgroundColor = CardBackground
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = notif.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notif.message,
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ClonedAppCard(
    clone: ClonedApp,
    onOpen: () -> Unit,
    onDetails: () -> Unit,
    onRename: () -> Unit,
    onTogglePrivate: () -> Unit,
    onRemove: () -> Unit,
    onDuplicate: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    MulticlonxCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cloned_card_${clone.packageName}"),
        cornerRadius = 18.dp,
        backgroundColor = CardBackground,
        borderColor = BorderColor,
        onClick = onOpen
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: App Icon + Overflow Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                AppIconView(
                    iconKey = clone.iconKey,
                    packageName = clone.packageName,
                    size = 48.dp,
                    cloneBadgeNumber = clone.cloneNumber,
                    isPrivateSpace = clone.isPrivateSpace
                )

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("overflow_menu_${clone.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = SecondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(ElevatedSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Open App", color = PrimaryText) },
                            onClick = {
                                menuExpanded = false
                                onOpen()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Copy App (+1)", color = PrimaryText)
                                }
                            },
                            onClick = {
                                menuExpanded = false
                                onDuplicate()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("View Details", color = PrimaryText) },
                            onClick = {
                                menuExpanded = false
                                onDetails()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Rename Account", color = PrimaryText) },
                            onClick = {
                                menuExpanded = false
                                onRename()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (clone.isPrivateSpace) "Move to Home" else "Move to Private Space",
                                    color = CyanAccent
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onTogglePrivate()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Remove Clone", color = ErrorRed) },
                            onClick = {
                                menuExpanded = false
                                onRemove()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // App Name
            Text(
                text = clone.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Account Label
            Text(
                text = clone.accountLabel,
                fontSize = 12.sp,
                color = SecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Quick launch pill + Quick Copy button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElevatedSurface)
                        .clickable { onOpen() }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Launch",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyanAccent
                    )
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElevatedSurface)
                        .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                        .clickable { onDuplicate() }
                        .testTag("duplicate_clone_${clone.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy App Again",
                        tint = CyanAccent,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}
