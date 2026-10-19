package com.example.data

import android.content.Context
import com.example.model.AppCategory
import com.example.model.ClonedApp
import com.example.model.CompatibilityStatus
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object LocalCloneStorageManager {
    private const val FILE_NAME = "multiclonx_clones_v1.json"
    private const val PREFS_NAME = "multiclonx_persistent_prefs"
    private const val KEY_CLONES_BACKUP = "clones_backup_json"

    /**
     * Loads the persistent list of cloned apps from internal device storage.
     * Checks private JSON file first, falling back to SharedPreferences backup.
     */
    fun loadClones(context: Context): List<ClonedApp> {
        val jsonStr = try {
            val file = File(context.filesDir, FILE_NAME)
            if (file.exists() && file.length() > 0) {
                file.readText()
            } else {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getString(KEY_CLONES_BACKUP, null)
            }
        } catch (e: Exception) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_CLONES_BACKUP, null)
        } ?: return emptyList()

        return parseClonesJson(jsonStr)
    }

    /**
     * Saves the list of cloned apps to both internal file and SharedPreferences.
     */
    fun saveClones(context: Context, clones: List<ClonedApp>) {
        val jsonStr = serializeClonesJson(clones)
        try {
            val file = File(context.filesDir, FILE_NAME)
            file.writeText(jsonStr)
        } catch (e: Exception) {
            android.util.Log.e("LocalStorage", "Failed writing clones file: ${e.message}")
        }
        try {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_CLONES_BACKUP, jsonStr)
                .apply()
        } catch (e: Exception) {
            android.util.Log.e("LocalStorage", "Failed writing clones prefs: ${e.message}")
        }
    }

    private fun serializeClonesJson(clones: List<ClonedApp>): String {
        val jsonArray = JSONArray()
        for (c in clones) {
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("name", c.name)
            obj.put("packageName", c.packageName)
            obj.put("accountLabel", c.accountLabel)
            obj.put("cloneNumber", c.cloneNumber)
            obj.put("category", c.category.name)
            obj.put("iconKey", c.iconKey)
            obj.put("isPrivateSpace", c.isPrivateSpace)
            obj.put("storageUsageMb", c.storageUsageMb.toDouble())
            obj.put("cacheUsageMb", c.cacheUsageMb.toDouble())
            obj.put("compatibility", c.compatibility.name)
            obj.put("status", c.status)
            obj.put("createdAt", c.createdAt)
            if (c.webContainerUrl != null) obj.put("webContainerUrl", c.webContainerUrl)
            if (c.containerPath != null) obj.put("containerPath", c.containerPath)
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }

    private fun parseClonesJson(jsonStr: String): List<ClonedApp> {
        val list = mutableListOf<ClonedApp>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val categoryName = obj.optString("category", "MESSAGING")
                val category = AppCategory.entries.find {
                    it.name.equals(categoryName, ignoreCase = true) || it.displayName.equals(categoryName, ignoreCase = true)
                } ?: AppCategory.MESSAGING

                val compatName = obj.optString("compatibility", "COMPATIBLE")
                val compatibility = CompatibilityStatus.entries.find {
                    it.name.equals(compatName, ignoreCase = true)
                } ?: CompatibilityStatus.COMPATIBLE

                val webUrl = if (obj.has("webContainerUrl") && !obj.isNull("webContainerUrl")) {
                    obj.getString("webContainerUrl")
                } else null

                val containerPath = if (obj.has("containerPath") && !obj.isNull("containerPath")) {
                    obj.getString("containerPath")
                } else null

                list.add(
                    ClonedApp(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        packageName = obj.getString("packageName"),
                        accountLabel = obj.optString("accountLabel", "Account #1"),
                        cloneNumber = obj.optInt("cloneNumber", 1),
                        category = category,
                        iconKey = obj.optString("iconKey", obj.getString("packageName")),
                        isPrivateSpace = obj.optBoolean("isPrivateSpace", false),
                        storageUsageMb = obj.optDouble("storageUsageMb", 48.0).toFloat(),
                        cacheUsageMb = obj.optDouble("cacheUsageMb", 0.0).toFloat(),
                        compatibility = compatibility,
                        status = obj.optString("status", "Active Container"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        webContainerUrl = webUrl,
                        containerPath = containerPath
                    )
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("LocalStorage", "Failed parsing clones json: ${e.message}")
        }
        return list
    }
}
