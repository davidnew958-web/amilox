package com.example.model

enum class AppCategory(val displayName: String) {
    ALL("All"),
    MESSAGING("Messaging"),
    SOCIAL("Social"),
    GAMES("Games"),
    OTHER("Other")
}

enum class CompatibilityStatus(val label: String) {
    COMPATIBLE("Compatible"),
    CHECKING("Checking"),
    UNSUPPORTED("Unsupported")
}

data class AppCatalogItem(
    val name: String,
    val packageName: String,
    val category: AppCategory,
    val compatibility: CompatibilityStatus,
    val description: String,
    val iconKey: String,
    val defaultStorageMb: Float = 48.5f,
    val supportedMultiClones: Boolean = true,
    val isDeviceInstalled: Boolean = false,
    val webContainerUrl: String? = null
)

data class ClonedApp(
    val id: String,
    val name: String,
    val packageName: String,
    val accountLabel: String,
    val cloneNumber: Int,
    val category: AppCategory,
    val iconKey: String,
    val isPrivateSpace: Boolean = false,
    val storageUsageMb: Float = 142.4f,
    val cacheUsageMb: Float = 34.2f,
    val compatibility: CompatibilityStatus = CompatibilityStatus.COMPATIBLE,
    val status: String = "Idle",
    val createdAt: Long = System.currentTimeMillis(),
    val webContainerUrl: String? = null,
    val containerPath: String? = null
)

enum class PlanTier(
    val id: String,
    val title: String,
    val priceDisplay: String,
    val period: String,
    val description: String,
    val features: List<String>,
    val maxClones: Int,
    val hasAds: Boolean,
    val isPopular: Boolean = false,
    val isBestValue: Boolean = false
) {
    FREE(
        id = "free",
        title = "Free Plan",
        priceDisplay = "$0 USD",
        period = "Forever",
        description = "For everyday users.",
        features = listOf(
            "Multiple copies per app",
            "Isolated local storage"
        ),
        maxClones = 999,
        hasAds = false
    ),
    WEEKLY(
        id = "weekly",
        title = "Weekly Plan",
        priceDisplay = "5.99 MAD",
        period = "/ week",
        description = "For users who need more flexibility.",
        features = listOf(
            "Clone up to 10 apps, subject to compatibility",
            "No ads",
            "App lock with PIN"
        ),
        maxClones = 10,
        hasAds = false
    ),
    MONTHLY(
        id = "monthly",
        title = "Monthly Plan",
        priceDisplay = "$12.99 USD",
        period = "/ month",
        description = "For users who want unlimited cloning.",
        features = listOf(
            "Unlimited app clones, subject to compatibility",
            "No ads",
            "App lock with PIN",
            "Priority support"
        ),
        maxClones = 999,
        hasAds = false,
        isPopular = true
    ),
    YEARLY(
        id = "yearly",
        title = "Yearly Plan",
        priceDisplay = "$38.99 USD",
        period = "/ year",
        description = "The best value for long-term users.",
        features = listOf(
            "Unlimited app clones, subject to compatibility",
            "No ads",
            "Priority support"
        ),
        maxClones = 999,
        hasAds = false,
        isBestValue = true
    )
}

enum class AutoLockTimeout(val label: String) {
    IMMEDIATELY("Immediately"),
    SECONDS_30("After 30 seconds"),
    MINUTE_1("After 1 minute"),
    MINUTES_5("After 5 minutes")
}

data class SecurityState(
    val isAppLockEnabled: Boolean = true,
    val isPrivateSpaceLocked: Boolean = true,
    val currentPin: String = "123456",
    val autoLockTimeout: AutoLockTimeout = AutoLockTimeout.MINUTE_1,
    val isBiometricsEnabled: Boolean = true
)

data class UserAccount(
    val fullName: String = "Alex Vance",
    val email: String = "alex.vance@multiclonx.net",
    val isSignedIn: Boolean = false,
    val isEmailVerified: Boolean = true
)

sealed interface ScreenDestination {
    data object Splash : ScreenDestination
    data class Onboarding(val pageIndex: Int = 0) : ScreenDestination
    data object Home : ScreenDestination
    data object CloneCatalog : ScreenDestination
    data object PrivateSpace : ScreenDestination
    data object Premium : ScreenDestination
    data object Settings : ScreenDestination
    data class CreateClone(val appItem: AppCatalogItem) : ScreenDestination
    data class ClonedAppDetail(val cloneId: String) : ScreenDestination
    data object SecuritySettingsScreen : ScreenDestination
    data class SetPinFlow(val isChangeMode: Boolean = false) : ScreenDestination
    data object SignIn : ScreenDestination
    data object SignUp : ScreenDestination
    data class VerifyEmail(val email: String) : ScreenDestination
    data object ForgotPassword : ScreenDestination
    data object ComponentShowcase : ScreenDestination
    data class IsolatedContainer(val cloneId: String) : ScreenDestination
}
