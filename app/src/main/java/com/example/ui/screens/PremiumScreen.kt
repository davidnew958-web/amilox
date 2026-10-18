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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
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
import com.example.model.PlanTier
import com.example.ui.components.GradientButton
import com.example.ui.components.MulticlonxCard
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
import com.example.viewmodel.MulticlonxUiState
import com.example.viewmodel.MulticlonxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    uiState: MulticlonxUiState,
    viewModel: MulticlonxViewModel,
    modifier: Modifier = Modifier
) {
    var showLegalDialog by remember { mutableStateOf<Pair<String, String>?>(null) }

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(listOf(DeepIndigo, PrimaryPurple))
                        )
                        .border(1.5.dp, CyanAccent.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = "Premium",
                        tint = CyanAccent,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Multiclonx Premium",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Expand your account limits with zero interruptions",
                    fontSize = 14.sp,
                    color = SecondaryText,
                    textAlign = TextAlign.Center
                )
            }

            // 4 Pricing Cards: Free, Weekly, Monthly, Yearly
            PlanTier.entries.forEach { plan ->
                val isCurrentPlan = uiState.currentPlan == plan
                val isHighlighted = plan == PlanTier.MONTHLY || plan == PlanTier.YEARLY

                MulticlonxCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 7.dp)
                        .testTag("plan_card_${plan.id}"),
                    cornerRadius = 20.dp,
                    backgroundColor = if (isCurrentPlan) SecondaryBackground else CardBackground,
                    borderColor = when {
                        isCurrentPlan -> CyanAccent
                        isHighlighted -> PrimaryPurple.copy(alpha = 0.8f)
                        else -> BorderColor
                    },
                    borderBrush = if (isHighlighted && !isCurrentPlan) {
                        Brush.linearGradient(
                            listOf(PrimaryPurple, CyanAccent.copy(alpha = 0.5f), BorderColor)
                        )
                    } else null
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = plan.title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryText
                                    )
                                    if (plan.isPopular) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(PrimaryPurple)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "POPULAR",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    } else if (plan.isBestValue) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(SuccessGreen.copy(alpha = 0.2f))
                                                .border(1.dp, SuccessGreen, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "BEST VALUE",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SuccessGreen
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = plan.description,
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                            }

                            if (isCurrentPlan) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CyanAccent.copy(alpha = 0.15f))
                                        .border(1.dp, CyanAccent, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Active",
                                        color = CyanAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Price and Frequency
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = plan.priceDisplay,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = plan.period,
                                fontSize = 14.sp,
                                color = SecondaryText,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Features List
                        plan.features.forEach { feature ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = feature,
                                    fontSize = 13.sp,
                                    color = PrimaryText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Button
                        val buttonText = when {
                            isCurrentPlan -> "Current Plan"
                            plan == PlanTier.FREE -> "Get Started"
                            plan == PlanTier.WEEKLY -> "Choose Weekly"
                            plan == PlanTier.MONTHLY -> "Choose Monthly"
                            plan == PlanTier.YEARLY -> "Choose Yearly"
                            else -> "Select"
                        }

                        GradientButton(
                            text = buttonText,
                            onClick = {
                                if (!isCurrentPlan) {
                                    viewModel.promptPurchasePlan(plan)
                                }
                            },
                            isEnabled = !isCurrentPlan,
                            testTag = "choose_plan_${plan.id}_button"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Restore Purchases Action
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElevatedSurface)
                    .clickable { viewModel.restorePurchases() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Restore Purchases",
                    color = CyanAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Terms and Privacy Links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Terms of Service",
                    color = SecondaryText,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        showLegalDialog = Pair(
                            "Terms of Service",
                            "Multiclonx allows users to maintain separate sandboxed application instances on supported devices. Subscriptions automatically renew unless cancelled at least 24 hours prior to the current billing period."
                        )
                    }
                )
                Text(
                    text = "  •  ",
                    color = SecondaryText,
                    fontSize = 12.sp
                )
                Text(
                    text = "Privacy Policy",
                    color = SecondaryText,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        showLegalDialog = Pair(
                            "Privacy Policy",
                            "Multiclonx does not collect or transmit cloned application data. All sandboxed data files, cookies, and tokens remain stored on your local device storage."
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(96.dp))
        }

        // Purchase Confirmation Bottom Sheet
        if (uiState.showPurchaseDialogForPlan != null) {
            val plan = uiState.showPurchaseDialogForPlan!!
            ModalBottomSheet(
                onDismissRequest = { viewModel.dismissPurchaseDialog() },
                containerColor = SecondaryBackground,
                contentColor = PrimaryText
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 18.dp)
                ) {
                    Text(
                        text = "Confirm Subscription",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "You are subscribing to ${plan.title} at ${plan.priceDisplay} ${plan.period}.",
                        fontSize = 14.sp,
                        color = SecondaryText
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    MulticlonxCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = CardBackground
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Renewal & Cancellation Terms",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Subscription will renew automatically unless cancelled in your Google Play Store settings. You can manage or cancel anytime without penalty.",
                                fontSize = 12.sp,
                                color = SecondaryText,
                                lineHeight = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    GradientButton(
                        text = "Confirm ${plan.priceDisplay}",
                        onClick = { viewModel.confirmPurchasePlan(plan) },
                        testTag = "confirm_purchase_action_button"
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Legal dialog
        if (showLegalDialog != null) {
            val (title, content) = showLegalDialog!!
            AlertDialog(
                onDismissRequest = { showLegalDialog = null },
                containerColor = SecondaryBackground,
                title = { Text(title, color = PrimaryText) },
                text = { Text(content, color = SecondaryText, fontSize = 14.sp) },
                confirmButton = {
                    TextButton(onClick = { showLegalDialog = null }) {
                        Text("Close", color = CyanAccent)
                    }
                }
            )
        }
    }
}
