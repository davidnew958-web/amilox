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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCatalogItem
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
    var accountLabel by remember { mutableStateOf("") }
    var isPrivateSpace by remember { mutableStateOf(false) }

    val presetLabels = listOf("Work Account", "Personal Account", "Second Account", "Gaming")

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
                    text = "Create Clone",
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
                AppIconView(iconKey = appItem.iconKey, size = 80.dp, cloneBadgeNumber = 2)

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
            }

            Spacer(modifier = Modifier.height(28.dp))

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
                    Text("e.g. Work Account", color = SecondaryText)
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

            // Privacy Information Card
            MulticlonxCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SecondaryBackground,
                borderColor = BorderColor
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sandbox Isolation Notice",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "This clone operates within a private local data directory. Login tokens, cache files, and app databases will remain completely isolated from other accounts.",
                        fontSize = 12.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Creation in progress indicator
            if (uiState.isCreatingClone) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Creating isolated sandbox container...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = CyanAccent
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = uiState.cloneCreationProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CyanAccent,
                        trackColor = ElevatedSurface
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

            // Primary and Secondary Action Buttons
            GradientButton(
                text = "Create Clone",
                onClick = {
                    viewModel.startCloneCreation(
                        appItem = appItem,
                        accountLabel = accountLabel,
                        isPrivate = isPrivateSpace
                    )
                },
                isLoading = uiState.isCreatingClone,
                testTag = "submit_create_clone_button"
            )

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
