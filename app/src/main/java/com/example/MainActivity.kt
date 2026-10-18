package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ScreenDestination
import com.example.ui.components.MulticlonxBottomBar
import com.example.ui.screens.ClonedAppDetailsScreen
import com.example.ui.screens.CloneCatalogScreen
import com.example.ui.screens.ComponentShowcaseScreen
import com.example.ui.screens.CreateCloneScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PremiumScreen
import com.example.ui.screens.PrivateSpaceScreen
import com.example.ui.screens.RemoveCloneConfirmDialog
import com.example.ui.screens.RenameCloneDialog
import com.example.ui.screens.SandboxLaunchDialog
import com.example.ui.screens.SecuritySettingsScreen
import com.example.ui.screens.SetPinScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VerifyEmailScreen
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.MainBackground
import com.example.ui.theme.MulticlonxTheme
import com.example.ui.theme.PrimaryText
import com.example.viewmodel.MulticlonxViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MulticlonxTheme {
                MulticlonxApp()
            }
        }
    }
}

@Composable
fun MulticlonxApp(
    viewModel: MulticlonxViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.initRepository(context)
        viewModel.loadDeviceApps(context)
        val credentialManager = androidx.credentials.CredentialManager.create(context)
        com.example.auth.GoogleAuthHelper.attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {
                // User signed in
            },
            onUnauthenticated = {
                // Not authenticated
            },
            scope = coroutineScope
        )
    }

    // Intercept hardware Back Button
    val isPrimaryScreen = uiState.currentScreen is ScreenDestination.Home && uiState.activeBottomTab == 0
    BackHandler(enabled = !isPrimaryScreen && uiState.currentScreen !is ScreenDestination.Splash) {
        viewModel.navigateBack()
    }

    // Trigger toast messages as snackbars
    LaunchedEffect(uiState.toastMessage) {
        val msg = uiState.toastMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    val showBottomBar = when (uiState.currentScreen) {
        is ScreenDestination.Home,
        is ScreenDestination.CloneCatalog,
        is ScreenDestination.PrivateSpace,
        is ScreenDestination.Premium,
        is ScreenDestination.Settings -> true
        else -> false
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = MainBackground,
        contentColor = PrimaryText,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MulticlonxBottomBar(
                    selectedTab = uiState.activeBottomTab,
                    onTabSelected = { viewModel.selectBottomTab(it) }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = if (showBottomBar) 64.dp else 16.dp)
            ) { data ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ElevatedSurface)
                        .border(1.dp, CyanAccent.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = data.visuals.message,
                        color = PrimaryText,
                        fontSize = 13.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val dest = uiState.currentScreen) {
                is ScreenDestination.Splash -> {
                    SplashScreen(
                        onContinue = {
                            viewModel.navigateTo(ScreenDestination.Home, addToBackStack = false)
                        }
                    )
                }

                is ScreenDestination.Onboarding -> {
                    OnboardingScreen(
                        currentPage = uiState.activeOnboardingPage,
                        onNext = {
                            val next = uiState.activeOnboardingPage + 1
                            if (next <= 2) {
                                viewModel.setOnboardingPage(next)
                            } else {
                                viewModel.navigateTo(ScreenDestination.Home, addToBackStack = false)
                            }
                        },
                        onSkip = {
                            viewModel.navigateTo(ScreenDestination.Home, addToBackStack = false)
                        },
                        onGetStarted = {
                            viewModel.navigateTo(ScreenDestination.Home, addToBackStack = false)
                        },
                        onAlreadyHaveAccount = {
                            viewModel.navigateTo(ScreenDestination.SignIn)
                        }
                    )
                }

                is ScreenDestination.Home -> {
                    HomeScreen(uiState = uiState, viewModel = viewModel)
                }

                is ScreenDestination.CloneCatalog -> {
                    CloneCatalogScreen(uiState = uiState, viewModel = viewModel)
                }

                is ScreenDestination.PrivateSpace -> {
                    PrivateSpaceScreen(uiState = uiState, viewModel = viewModel)
                }

                is ScreenDestination.Premium -> {
                    PremiumScreen(uiState = uiState, viewModel = viewModel)
                }

                is ScreenDestination.Settings -> {
                    SettingsScreen(uiState = uiState, viewModel = viewModel)
                }

                is ScreenDestination.CreateClone -> {
                    CreateCloneScreen(appItem = dest.appItem, uiState = uiState, viewModel = viewModel)
                }

                is ScreenDestination.ClonedAppDetail -> {
                    ClonedAppDetailsScreen(cloneId = dest.cloneId, uiState = uiState, viewModel = viewModel)
                }

                is ScreenDestination.SecuritySettingsScreen -> {
                    SecuritySettingsScreen(uiState = uiState, viewModel = viewModel)
                }

                is ScreenDestination.SetPinFlow -> {
                    SetPinScreen(isChangeMode = dest.isChangeMode, uiState = uiState, viewModel = viewModel)
                }

                is ScreenDestination.SignIn -> {
                    SignInScreen(viewModel = viewModel)
                }

                is ScreenDestination.SignUp -> {
                    SignUpScreen(viewModel = viewModel)
                }

                is ScreenDestination.VerifyEmail -> {
                    VerifyEmailScreen(email = dest.email, viewModel = viewModel)
                }

                is ScreenDestination.ForgotPassword -> {
                    ForgotPasswordScreen(viewModel = viewModel)
                }

                is ScreenDestination.ComponentShowcase -> {
                    ComponentShowcaseScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Global Interactive Dialogs
    if (uiState.activeLaunchClone != null) {
        val clone = uiState.activeLaunchClone!!
        SandboxLaunchDialog(
            clone = clone,
            onDismiss = { viewModel.dismissLaunchDialog() },
            onConfirmedLaunch = {
                viewModel.dismissLaunchDialog()
                val packageManager = context.packageManager
                val launchIntent = packageManager.getLaunchIntentForPackage(clone.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        context.startActivity(launchIntent)
                        viewModel.showToast("Launched ${clone.name} (${clone.accountLabel})")
                    } catch (e: Exception) {
                        viewModel.showToast("Failed to launch ${clone.name}: ${e.localizedMessage}")
                    }
                } else {
                    try {
                        val webUrl = "https://www.google.com/search?q=${java.net.URLEncoder.encode(clone.name, "UTF-8")}+login"
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(browserIntent)
                        viewModel.showToast("Opened sandboxed session for ${clone.name} (${clone.accountLabel})")
                    } catch (e: Exception) {
                        viewModel.showToast("Cloned container active for ${clone.name} (${clone.accountLabel})")
                    }
                }
            }
        )
    }

    if (uiState.showDeleteConfirmDialogFor != null) {
        val clone = uiState.showDeleteConfirmDialogFor!!
        RemoveCloneConfirmDialog(
            clone = clone,
            onDismiss = { viewModel.dismissDeleteDialog() },
            onConfirmRemove = { viewModel.removeClone(clone) }
        )
    }

    if (uiState.showRenameDialogFor != null) {
        val clone = uiState.showRenameDialogFor!!
        RenameCloneDialog(
            clone = clone,
            onDismiss = { viewModel.dismissRenameDialog() },
            onConfirmRename = { newLabel ->
                viewModel.renameClone(clone.id, newLabel)
            }
        )
    }
}
