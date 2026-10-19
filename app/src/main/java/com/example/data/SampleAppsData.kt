package com.example.data

import com.example.model.AppCatalogItem
import com.example.model.ClonedApp

object SampleAppsData {
    /**
     * No fake catalog apps or hardcoded mock packages.
     * All apps in MultiClonx are discovered live from the user's Android phone using PackageManager.
     */
    val catalog: List<AppCatalogItem> = emptyList()

    /**
     * No demo clones are seeded by default.
     */
    fun initialClones(): List<ClonedApp> = emptyList()
}
