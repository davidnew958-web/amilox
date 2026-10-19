package com.example.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.model.AppCatalogItem
import com.example.model.AppCategory
import com.example.model.CompatibilityStatus
import java.io.File
import java.util.Locale

object DeviceAppsManager {

    /**
     * Resolves the dedicated isolated web container URL for supported services.
     * These apps can run in a genuine isolated WebView container with dedicated
     * session storage, cookies, and local database without violating Android security boundaries.
     */
    fun getWebContainerUrl(packageName: String): String? {
        val p = packageName.lowercase()
        return when {
            p.contains("whatsapp") -> "https://web.whatsapp.com"
            p.contains("telegram") || p.contains("challegram") -> "https://web.telegram.org"
            p.contains("signal") -> "https://signal.org"
            p.contains("instagram") -> "https://www.instagram.com"
            p.contains("facebook.katana") || p.contains("facebook.lite") -> "https://m.facebook.com"
            p.contains("facebook.orca") || p.contains("messenger") -> "https://www.messenger.com"
            p.contains("discord") -> "https://discord.com/app"
            p.contains("twitter") || p.contains("tweetdeck") -> "https://x.com"
            p.contains("reddit") -> "https://www.reddit.com"
            p.contains("musically") || p.contains("tiktok") -> "https://www.tiktok.com"
            p.contains("snapchat") -> "https://web.snapchat.com"
            p.contains("threads") -> "https://www.threads.net"
            p.contains("slack") -> "https://app.slack.com"
            p.contains("linkedin") -> "https://www.linkedin.com"
            p.contains("pinterest") -> "https://www.pinterest.com"
            p.contains("tumblr") -> "https://www.tumblr.com"
            p.contains("gmail") || p.contains("google.android.gm") -> "https://mail.google.com"
            p.contains("outlook") || p.contains("hotmail") -> "https://outlook.live.com"
            p.contains("yahoo.mobile.client.android.mail") || p.contains("yahoomail") -> "https://mail.yahoo.com"
            p.contains("spotify") -> "https://open.spotify.com"
            p.contains("twitch") -> "https://m.twitch.tv"
            p.contains("youtube") -> "https://m.youtube.com"
            p.contains("amazon") -> "https://www.amazon.com"
            p.contains("ebay") -> "https://www.ebay.com"
            p.contains("aliexpress") -> "https://www.aliexpress.com"
            p.contains("netflix") -> "https://www.netflix.com"
            p.contains("trello") -> "https://trello.com"
            p.contains("notion") -> "https://www.notion.so"
            p.contains("github") -> "https://github.com"
            p.contains("chrome") || p.contains("firefox") || p.contains("browser") ||
            p.contains("edge") || p.contains("opera") || p.contains("brave") ||
            p.contains("duckduckgo") || p.contains("sbrowser") || p.contains("webview") ||
            p.contains("chromium") || p.contains("search") -> "https://www.google.com"
            else -> null
        }
    }

    /**
     * Queries the Android system for real installed applications and browsers on this phone.
     * No fake apps or placeholder catalog entries are added.
     */
    fun getInstalledApps(context: Context): List<AppCatalogItem> {
        val packageManager = context.packageManager
        val myPackageName = context.packageName

        val seenPackages = mutableSetOf<String>()
        val result = mutableListOf<AppCatalogItem>()

        // 1. Query via launcher intent actions (MAIN + LAUNCHER)
        val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfoList = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    launcherIntent,
                    PackageManager.ResolveInfoFlags.of(0L)
                )
            } else {
                packageManager.queryIntentActivities(launcherIntent, 0)
            }
        } catch (e: Exception) {
            emptyList()
        }

        for (info in resolveInfoList) {
            val pkg = info.activityInfo?.packageName ?: continue
            if (pkg == myPackageName) continue
            if (seenPackages.contains(pkg)) continue
            seenPackages.add(pkg)

            val appInfo = info.activityInfo?.applicationInfo ?: continue
            processApp(packageManager, pkg, appInfo, info.loadLabel(packageManager)?.toString(), result)
        }

        // 2. Also query installed packages to ensure browsers and other launchable apps aren't missed
        val installedPackages = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0L))
            } else {
                packageManager.getInstalledPackages(0)
            }
        } catch (e: Exception) {
            emptyList()
        }

        for (pkgInfo in installedPackages) {
            val pkg = pkgInfo.packageName ?: continue
            if (pkg == myPackageName) continue
            if (seenPackages.contains(pkg)) continue

            val appInfo = pkgInfo.applicationInfo ?: continue
            val launchIntent = packageManager.getLaunchIntentForPackage(pkg)
            if (launchIntent != null || isBrowserOrUtility(pkg)) {
                seenPackages.add(pkg)
                val label = try {
                    appInfo.loadLabel(packageManager)?.toString()
                } catch (e: Exception) {
                    null
                } ?: pkg
                processApp(packageManager, pkg, appInfo, label, result)
            }
        }

        // Sort alphabetically by app name
        return result.sortedBy { it.name.lowercase() }
    }

    private fun processApp(
        packageManager: PackageManager,
        pkg: String,
        appInfo: ApplicationInfo,
        rawLabel: String?,
        result: MutableList<AppCatalogItem>
    ) {
        val label = rawLabel?.trim()?.ifBlank { pkg } ?: pkg
        val category = detectCategory(label, pkg, appInfo)
        val approximateStorageMb = try {
            val file = File(appInfo.sourceDir ?: "")
            if (file.exists()) {
                (file.length() / (1024f * 1024f)).coerceIn(10f, 800f)
            } else 45.0f
        } catch (e: Exception) {
            45.0f
        }

        val webUrl = getWebContainerUrl(pkg)
        val isCompatible = webUrl != null
        val compatibility = if (isCompatible) CompatibilityStatus.COMPATIBLE else CompatibilityStatus.UNSUPPORTED
        val description = if (isCompatible) {
            "Supported for isolated container session with separate data & cookies."
        } else {
            "Native binary APK. Android OS prevents user-space cloning without a Managed Work Profile."
        }

        result.add(
            AppCatalogItem(
                name = label,
                packageName = pkg,
                category = category,
                compatibility = compatibility,
                description = description,
                iconKey = pkg,
                defaultStorageMb = String.format(Locale.US, "%.1f", approximateStorageMb).toFloat(),
                supportedMultiClones = isCompatible,
                isDeviceInstalled = true,
                webContainerUrl = webUrl
            )
        )
    }

    private fun isBrowserOrUtility(pkg: String): Boolean {
        val p = pkg.lowercase()
        return p.contains("browser") || p.contains("chrome") || p.contains("firefox") || 
               p.contains("edge") || p.contains("opera") || p.contains("safari") || 
               p.contains("whatsapp") || p.contains("telegram") || p.contains("instagram") ||
               p.contains("facebook") || p.contains("discord") || p.contains("tiktok") ||
               p.contains("twitter") || p.contains("reddit") || p.contains("slack") ||
               p.contains("linkedin") || p.contains("pinterest") || p.contains("spotify") ||
               p.contains("youtube") || p.contains("amazon") || p.contains("mail") ||
               p.contains("webview") || p.contains("search") || p.contains("duckduckgo")
    }

    private fun detectCategory(label: String, pkg: String, appInfo: ApplicationInfo?): AppCategory {
        val text = "${label.lowercase()} ${pkg.lowercase()}"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appInfo != null) {
            when (appInfo.category) {
                ApplicationInfo.CATEGORY_GAME -> return AppCategory.GAMES
                ApplicationInfo.CATEGORY_SOCIAL -> return AppCategory.SOCIAL
                ApplicationInfo.CATEGORY_AUDIO, ApplicationInfo.CATEGORY_VIDEO -> return AppCategory.OTHER
            }
        }

        return when {
            text.contains("whatsapp") || text.contains("telegram") || text.contains("signal") ||
            text.contains("messenger") || text.contains("discord") || text.contains("chat") ||
            text.contains("message") || text.contains("sms") || text.contains("viber") ||
            text.contains("wechat") || text.contains("slack") || text.contains("skype") -> AppCategory.MESSAGING

            text.contains("instagram") || text.contains("facebook") || text.contains("tiktok") ||
            text.contains("twitter") || text.contains("reddit") || text.contains("snapchat") ||
            text.contains("threads") || text.contains("linkedin") || text.contains("social") ||
            text.contains("pinterest") || text.contains("tumblr") -> AppCategory.SOCIAL

            text.contains("game") || text.contains("clash") || text.contains("roblox") ||
            text.contains("pubg") || text.contains("play") || text.contains("craft") ||
            text.contains("racing") || text.contains("arcade") -> AppCategory.GAMES

            else -> AppCategory.OTHER
        }
    }
}
