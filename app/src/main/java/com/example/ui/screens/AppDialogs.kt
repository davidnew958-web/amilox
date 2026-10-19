package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClonedApp
import com.example.ui.components.AppIconView
import com.example.ui.components.GradientButton
import com.example.ui.components.MulticlonxCard
import com.example.ui.components.SecondaryButton
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText

@Composable
fun SandboxLaunchDialog(
    clone: ClonedApp,
    onDismiss: () -> Unit,
    onConfirmedLaunch: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SecondaryBackground,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AppIconView(
                    iconKey = clone.iconKey,
                    packageName = clone.packageName,
                    size = 44.dp,
                    cloneBadgeNumber = clone.cloneNumber,
                    isPrivateSpace = clone.isPrivateSpace
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Launch ${clone.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Text(
                        text = clone.accountLabel,
                        fontSize = 13.sp,
                        color = CyanAccent
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Isolated Session Container",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "This clone runs in a private session sandbox with dedicated cookies, localStorage, and isolated cache partition.",
                    fontSize = 12.sp,
                    color = SecondaryText,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBackground)
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("• Container Type: Web Session Sandbox", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = CyanAccent)
                        Text("• Package: ${clone.packageName}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = SecondaryText)
                        Text("• Storage Path: ${clone.containerPath ?: "Internal Partition"}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = SecondaryText)
                        Text("• Host App Launch: Disabled (Protected)", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = PrimaryPurple)
                    }
                }
            }
        },
        confirmButton = {
            GradientButton(
                text = "Open Container",
                icon = Icons.Default.PlayArrow,
                onClick = onConfirmedLaunch,
                modifier = Modifier.fillMaxWidth(),
                testTag = "confirm_sandbox_launch_button"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SecondaryText)
            }
        }
    )
}

@Composable
fun UnsupportedNativeCloneDialog(
    clone: ClonedApp,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SecondaryBackground,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ErrorRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        title = {
            Text(
                text = "Native Binary Cloning Restricted",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${clone.name} is a compiled native Android APK. Android OS security enforcement (Linux UID isolation, SELinux, and hardened ART) prevents unprivileged user-space applications from virtualizing secondary native binary instances.",
                    fontSize = 13.sp,
                    color = SecondaryText,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Launching your device's original app was blocked to protect your primary account and avoid cross-account data corruption.",
                    fontSize = 12.sp,
                    color = CyanAccent,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Recommended Solution: Use Android's native Work Profile (Shelter / Island / Android Enterprise) or OEM Dual Messenger in device Settings to run a genuine second instance.",
                    fontSize = 12.sp,
                    color = PrimaryText,
                    lineHeight = 16.sp
                )
            }
        },
        confirmButton = {
            GradientButton(
                text = "Open Device Settings",
                onClick = onOpenSettings,
                modifier = Modifier.fillMaxWidth(),
                testTag = "open_settings_unsupported_dialog_button"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = SecondaryText)
            }
        }
    )
}

@Composable
fun RemoveCloneConfirmDialog(
    clone: ClonedApp,
    onDismiss: () -> Unit,
    onConfirmRemove: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SecondaryBackground,
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Remove Clone?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to remove \"${clone.name}\" (${clone.accountLabel})?",
                    fontSize = 14.sp,
                    color = PrimaryText,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                MulticlonxCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CardBackground,
                    borderColor = ErrorRed.copy(alpha = 0.3f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Data deletion warning:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "All sandboxed databases, login cookies, and cached photos (${String.format("%.1f", clone.storageUsageMb + clone.cacheUsageMb)} MB) will be permanently deleted from this device.",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmRemove,
                modifier = Modifier.testTag("confirm_delete_button")
            ) {
                Text("Remove", color = ErrorRed, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_delete_button")
            ) {
                Text("Cancel", color = SecondaryText)
            }
        }
    )
}

@Composable
fun RenameCloneDialog(
    clone: ClonedApp,
    onDismiss: () -> Unit,
    onConfirmRename: (String) -> Unit
) {
    var newLabel by remember { mutableStateOf(clone.accountLabel) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SecondaryBackground,
        shape = RoundedCornerShape(22.dp),
        title = {
            Text(
                text = "Rename Account",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
        },
        text = {
            Column {
                Text(
                    text = "Update the display label for ${clone.name}:",
                    fontSize = 13.sp,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = newLabel,
                    onValueChange = { newLabel = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = PrimaryText,
                        unfocusedTextColor = PrimaryText
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("rename_input_field")
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmRename(newLabel) },
                modifier = Modifier.testTag("save_rename_button")
            ) {
                Text("Save", color = CyanAccent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SecondaryText)
            }
        }
    )
}
