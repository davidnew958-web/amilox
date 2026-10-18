package com.example.data

import com.example.model.AppCatalogItem
import com.example.model.AppCategory
import com.example.model.ClonedApp
import com.example.model.CompatibilityStatus

object SampleAppsData {
    val catalog: List<AppCatalogItem> = listOf(
        AppCatalogItem(
            name = "WhatsApp",
            packageName = "com.whatsapp",
            category = AppCategory.MESSAGING,
            compatibility = CompatibilityStatus.COMPATIBLE,
            description = "Multi-account messaging with end-to-end sandbox storage.",
            iconKey = "whatsapp",
            defaultStorageMb = 142.4f
        ),
        AppCatalogItem(
            name = "Instagram",
            packageName = "com.instagram.android",
            category = AppCategory.SOCIAL,
            compatibility = CompatibilityStatus.COMPATIBLE,
            description = "Manage business and personal creator feeds independently.",
            iconKey = "instagram",
            defaultStorageMb = 210.8f
        ),
        AppCatalogItem(
            name = "Facebook",
            packageName = "com.facebook.katana",
            category = AppCategory.SOCIAL,
            compatibility = CompatibilityStatus.COMPATIBLE,
            description = "Run secondary profiles with dedicated notification channels.",
            iconKey = "facebook",
            defaultStorageMb = 188.0f
        ),
        AppCatalogItem(
            name = "Telegram",
            packageName = "org.telegram.messenger",
            category = AppCategory.MESSAGING,
            compatibility = CompatibilityStatus.COMPATIBLE,
            description = "High-speed cloud messaging clone with local encryption.",
            iconKey = "telegram",
            defaultStorageMb = 94.2f
        ),
        AppCatalogItem(
            name = "TikTok",
            packageName = "com.zhiliaoapp.musically",
            category = AppCategory.SOCIAL,
            compatibility = CompatibilityStatus.COMPATIBLE,
            description = "Create and browse on alternate creator profiles.",
            iconKey = "tiktok",
            defaultStorageMb = 312.0f
        ),
        AppCatalogItem(
            name = "Discord",
            packageName = "com.discord",
            category = AppCategory.MESSAGING,
            compatibility = CompatibilityStatus.COMPATIBLE,
            description = "Simultaneous community and work server presence.",
            iconKey = "discord",
            defaultStorageMb = 135.5f
        ),
        AppCatalogItem(
            name = "X (Twitter)",
            packageName = "com.twitter.android",
            category = AppCategory.SOCIAL,
            compatibility = CompatibilityStatus.COMPATIBLE,
            description = "Split professional outreach and personal timelines.",
            iconKey = "x_twitter",
            defaultStorageMb = 110.2f
        ),
        AppCatalogItem(
            name = "Messenger",
            packageName = "com.facebook.orca",
            category = AppCategory.MESSAGING,
            compatibility = CompatibilityStatus.COMPATIBLE,
            description = "Separate chat threads for multiple Facebook accounts.",
            iconKey = "messenger",
            defaultStorageMb = 120.6f
        ),
        AppCatalogItem(
            name = "Clash of Clans",
            packageName = "com.supercell.clashofclans",
            category = AppCategory.GAMES,
            compatibility = CompatibilityStatus.COMPATIBLE,
            description = "Run secondary village accounts with distinct Game IDs.",
            iconKey = "clash_of_clans",
            defaultStorageMb = 480.0f
        ),
        AppCatalogItem(
            name = "Roblox",
            packageName = "com.roblox.client",
            category = AppCategory.GAMES,
            compatibility = CompatibilityStatus.COMPATIBLE,
            description = "Dual gaming sessions with separate credentials.",
            iconKey = "roblox",
            defaultStorageMb = 390.4f
        ),
        AppCatalogItem(
            name = "Snapchat",
            packageName = "com.snapchat.android",
            category = AppCategory.SOCIAL,
            compatibility = CompatibilityStatus.CHECKING,
            description = "Verifying safety sandbox compliance on your device.",
            iconKey = "snapchat",
            defaultStorageMb = 175.0f
        ),
        AppCatalogItem(
            name = "Reddit",
            packageName = "com.reddit.frontpage",
            category = AppCategory.SOCIAL,
            compatibility = CompatibilityStatus.COMPATIBLE,
            description = "Keep alternate community handles strictly separated.",
            iconKey = "reddit",
            defaultStorageMb = 160.0f
        ),
        AppCatalogItem(
            name = "Secure Bank Mobile",
            packageName = "com.securebank.app",
            category = AppCategory.OTHER,
            compatibility = CompatibilityStatus.UNSUPPORTED,
            description = "Hardware DRM and safety attestation enforce single-instance security.",
            iconKey = "bank",
            defaultStorageMb = 85.0f,
            supportedMultiClones = false
        )
    )

    fun initialClones(): List<ClonedApp> = emptyList()
}
