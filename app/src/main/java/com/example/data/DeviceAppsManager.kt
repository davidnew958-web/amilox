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
     * Queries the Android system for real installed launcher applications and browsers on this device.
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

        // 3. Ensure popular catalog items (WhatsApp, Instagram, Telegram, etc.) are always available in catalog
        for (catalogItem in SampleAppsData.catalog) {
            if (!seenPackages.contains(catalogItem.packageName)) {
                seenPackages.add(catalogItem.packageName)
                result.add(catalogItem)
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

        val iconKey = mapToKnownIconKey(pkg)

        result.add(
            AppCatalogItem(
                name = label,
                packageName = pkg,
                category = category,
                compatibility = CompatibilityStatus.COMPATIBLE,
                description = "Real app installed on your phone. Ready for multi-account cloning.",
                iconKey = iconKey,
                defaultStorageMb = String.format(Locale.US, "%.1f", approximateStorageMb).toFloat(),
                supportedMultiClones = true,
                isDeviceInstalled = true
            )
        )
    }

    private fun isBrowserOrUtility(pkg: String): Boolean {
        val p = pkg.lowercase()
        return p.contains("browser") || p.contains("chrome") || p.contains("firefox") || 
               p.contains("edge") || p.contains("opera") || p.contains("safari") || 
               p.contains("whatsapp") || p.contains("telegram") || p.contains("instagram") ||
               p.contains("facebook") || p.contains("discord") || p.contains("tiktok")
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

    private fun mapToKnownIconKey(pkg: String): String {
        val p = pkg.lowercase()
        return when {
            p.contains("whatsapp") -> "whatsapp"
            p.contains("instagram") -> "instagram"
            p.contains("facebook.katana") || (p.contains("facebook") && !p.contains("orca")) -> "facebook"
            p.contains("telegram") -> "telegram"
            p.contains("tiktok") || p.contains("musically") -> "tiktok"
            p.contains("discord") -> "discord"
            p.contains("twitter") -> "x_twitter"
            p.contains("facebook.orca") || p.contains("messenger") -> "messenger"
            p.contains("clashofclans") -> "clash_of_clans"
            p.contains("roblox") -> "roblox"
            p.contains("snapchat") -> "snapchat"
            p.contains("reddit") -> "reddit"
            p.contains("chrome") || p.contains("browser") -> "chrome"
            p.contains("youtube") -> "youtube"
            p.contains("gmail") || p.contains("mail") -> "mail"
            p.contains("settings") -> "settings"
            p.contains("camera") -> "camera"
            else -> pkg
        }
    }
}
