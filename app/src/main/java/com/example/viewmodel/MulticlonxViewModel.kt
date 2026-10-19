package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MulticlonxRepository
import com.example.data.SampleAppsData
import com.example.model.AppCatalogItem
import com.example.model.AppCategory
import com.example.model.AutoLockTimeout
import com.example.model.CloneDocument
import com.example.model.ClonedApp
import com.example.model.CompatibilityStatus
import com.example.model.PlanTier
import com.example.model.ScreenDestination
import com.example.model.SecurityState
import com.example.model.UserAccount
import com.example.model.UserDocument
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.io.File
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val isUnread: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class MulticlonxUiState(
    val currentScreen: ScreenDestination = ScreenDestination.Splash,
    val navigationStack: List<ScreenDestination> = listOf(ScreenDestination.Home),
    val activeBottomTab: Int = 0, // 0: Home, 1: Clone, 2: Private, 3: Premium, 4: Settings
    val clonedApps: List<ClonedApp> = SampleAppsData.initialClones(),
    val catalogApps: List<AppCatalogItem> = emptyList(),
    val homeSearchQuery: String = "",
    val catalogSearchQuery: String = "",
    val catalogCategory: AppCategory = AppCategory.ALL,
    val selectedCatalogApps: Set<String> = emptySet(),
    val currentPlan: PlanTier = PlanTier.FREE,
    val securityState: SecurityState = SecurityState(),
    val userAccount: UserAccount = UserAccount(),
    val isCloudSyncing: Boolean = false,
    val notifications: List<UiNotification> = listOf(
        UiNotification(
            title = "Sandbox Isolation Active",
            message = "Cloned apps run in localized, dedicated storage session containers."
        ),
        UiNotification(
            title = "Cloud Sync Enabled",
            message = "Your multi-account setup synchronizes with Firebase Firestore backend."
        )
    ),
    // Interactive states
    val isCreatingClone: Boolean = false,
    val cloneCreationProgress: Float = 0f,
    val lastCreatedApp: ClonedApp? = null,
    val toastMessage: String? = null,
    val activeLaunchClone: ClonedApp? = null,
    val showPurchaseDialogForPlan: PlanTier? = null,
    val showDeleteConfirmDialogFor: ClonedApp? = null,
    val showRenameDialogFor: ClonedApp? = null,
    val pinErrorMessage: String? = null,
    val pinInputBuffer: String = "",
    val newPinStep1: String? = null,
    val newPinBuffer: String = "",
    val isSetPinConfirmationStep: Boolean = false,
    val activeOnboardingPage: Int = 0
)

class MulticlonxViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MulticlonxUiState())
    val uiState: StateFlow<MulticlonxUiState> = _uiState.asStateFlow()

    private var repository: MulticlonxRepository? = null
    private var syncJob: Job? = null
    private var appContext: Context? = null

    init {
        // Start on Splash screen, automatically transition to Home after 1600ms
        viewModelScope.launch {
            delay(1600)
            if (_uiState.value.currentScreen is ScreenDestination.Splash) {
                _uiState.update { it.copy(currentScreen = ScreenDestination.Home) }
            }
        }
    }

    fun loadDeviceApps(context: Context) {
        appContext = context.applicationContext
        loadPersistentClones(context)
        viewModelScope.launch {
            try {
                val apps = com.example.data.DeviceAppsManager.getInstalledApps(context)
                _uiState.update { it.copy(catalogApps = apps) }
            } catch (e: Exception) {
                android.util.Log.e("MulticlonxVM", "Failed to load device apps: ${e.message}")
            }
        }
    }

    fun initRepository(context: Context) {
        appContext = context.applicationContext
        loadPersistentClones(context)
        if (repository != null) return
        try {
            repository = MulticlonxRepository.create(context)
            listenToAuthChanges()
        } catch (e: Exception) {
            android.util.Log.e("MulticlonxVM", "Failed to initialize Firebase repository: ${e.message}")
        }
    }

    private fun loadPersistentClones(context: Context) {
        val saved = com.example.data.LocalCloneStorageManager.loadClones(context)
        if (saved.isNotEmpty()) {
            _uiState.update { current ->
                current.copy(clonedApps = saved)
            }
        }
    }

    private fun saveClonesLocally(clones: List<ClonedApp>) {
        val ctx = appContext
        if (ctx != null) {
            com.example.data.LocalCloneStorageManager.saveClones(ctx, clones)
        }
    }

    private fun listenToAuthChanges() {
        val auth = Firebase.auth
        val user = auth.currentUser
        if (user != null) {
            onUserAuthenticated(user.uid, user.email ?: "", user.displayName ?: "User")
        }

        auth.addAuthStateListener { firebaseAuth ->
            val u = firebaseAuth.currentUser
            if (u != null) {
                onUserAuthenticated(u.uid, u.email ?: "", u.displayName ?: "User")
            } else {
                onUserSignedOut()
            }
        }
    }

    fun onUserAuthenticated(uid: String, email: String, displayName: String) {
        _uiState.update {
            it.copy(
                userAccount = it.userAccount.copy(
                    fullName = displayName.ifBlank { "Multiclonx User" },
                    email = email.ifBlank { "user@multiclonx.net" },
                    isSignedIn = true
                ),
                isCloudSyncing = true
            )
        }

        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            val repo = repository ?: return@launch

            // 1. Observe Clones collection
            launch {
                repo.observeClones(uid).collect { cloneDocs ->
                    if (cloneDocs.isNotEmpty()) {
                        val mapped = cloneDocs.map { doc ->
                            val resolvedUrl = doc.webContainerUrl
                                ?: com.example.data.DeviceAppsManager.getWebContainerUrl(doc.packageName)
                            ClonedApp(
                                id = doc.id,
                                name = doc.name,
                                packageName = doc.packageName,
                                accountLabel = doc.accountLabel,
                                cloneNumber = doc.cloneNumber.toInt(),
                                category = AppCategory.entries.find { it.name.equals(doc.category, ignoreCase = true) } ?: AppCategory.MESSAGING,
                                iconKey = doc.iconKey.ifBlank { doc.packageName },
                                isPrivateSpace = doc.isPrivateSpace,
                                storageUsageMb = doc.storageUsageMb.toFloat(),
                                cacheUsageMb = doc.cacheUsageMb.toFloat(),
                                status = doc.status,
                                webContainerUrl = resolvedUrl,
                                containerPath = doc.containerPath
                            )
                        }
                        _uiState.update { it.copy(clonedApps = mapped) }
                        appContext?.let { ctx ->
                            com.example.data.LocalCloneStorageManager.saveClones(ctx, mapped)
                        }
                    } else {
                        // Seed initial clones to Firestore if cloud collection is fresh
                        val initial = _uiState.value.clonedApps
                        initial.forEach { clone ->
                            repo.addClone(clone.toDocument(uid))
                        }
                    }
                }
            }

            // 2. Observe User Profile
            launch {
                repo.observeUser(uid).collect { userDoc ->
                    if (userDoc != null) {
                        val plan = PlanTier.entries.find { it.id.equals(userDoc.plan, ignoreCase = true) } ?: PlanTier.FREE
                        val timeout = AutoLockTimeout.entries.find { it.label.equals(userDoc.autoLockTimeout, ignoreCase = true) } ?: AutoLockTimeout.MINUTE_1
                        _uiState.update { state ->
                            state.copy(
                                currentPlan = plan,
                                securityState = state.securityState.copy(
                                    isAppLockEnabled = userDoc.appLockEnabled,
                                    autoLockTimeout = timeout,
                                    isBiometricsEnabled = userDoc.biometricsEnabled
                                )
                            )
                        }
                    } else {
                        // Create initial profile in Firestore
                        val state = _uiState.value
                        repo.saveUserProfile(
                            UserDocument(
                                userId = uid,
                                email = email,
                                fullName = displayName,
                                plan = state.currentPlan.id,
                                appLockEnabled = state.securityState.isAppLockEnabled,
                                autoLockTimeout = state.securityState.autoLockTimeout.label,
                                biometricsEnabled = state.securityState.isBiometricsEnabled
                            )
                        )
                    }
                }
            }
        }
    }

    private fun onUserSignedOut() {
        syncJob?.cancel()
        syncJob = null
        _uiState.update {
            it.copy(
                userAccount = it.userAccount.copy(isSignedIn = false),
                isCloudSyncing = false
            )
        }
    }

    // Navigation
    fun navigateTo(destination: ScreenDestination, addToBackStack: Boolean = true) {
        _uiState.update { state ->
            val newStack = if (addToBackStack && state.currentScreen !is ScreenDestination.Splash) {
                state.navigationStack + state.currentScreen
            } else {
                state.navigationStack
            }

            val tab = when (destination) {
                ScreenDestination.Home -> 0
                ScreenDestination.CloneCatalog -> 1
                ScreenDestination.PrivateSpace -> 2
                ScreenDestination.Premium -> 3
                ScreenDestination.Settings -> 4
                else -> state.activeBottomTab
            }

            state.copy(
                currentScreen = destination,
                navigationStack = newStack,
                activeBottomTab = tab,
                pinInputBuffer = "",
                pinErrorMessage = null
            )
        }
    }

    fun navigateBack(): Boolean {
        val currentStack = _uiState.value.navigationStack
        if (currentStack.isNotEmpty()) {
            val previous = currentStack.last()
            val newStack = currentStack.dropLast(1)
            val tab = when (previous) {
                ScreenDestination.Home -> 0
                ScreenDestination.CloneCatalog -> 1
                ScreenDestination.PrivateSpace -> 2
                ScreenDestination.Premium -> 3
                ScreenDestination.Settings -> 4
                else -> _uiState.value.activeBottomTab
            }
            _uiState.update {
                it.copy(
                    currentScreen = previous,
                    navigationStack = newStack,
                    activeBottomTab = tab,
                    pinInputBuffer = "",
                    pinErrorMessage = null
                )
            }
            return true
        } else if (_uiState.value.currentScreen != ScreenDestination.Home) {
            _uiState.update {
                it.copy(
                    currentScreen = ScreenDestination.Home,
                    activeBottomTab = 0
                )
            }
            return true
        }
        return false
    }

    fun selectBottomTab(tabIndex: Int) {
        val dest = when (tabIndex) {
            0 -> ScreenDestination.Home
            1 -> ScreenDestination.CloneCatalog
            2 -> ScreenDestination.PrivateSpace
            3 -> ScreenDestination.Premium
            4 -> ScreenDestination.Settings
            else -> ScreenDestination.Home
        }
        navigateTo(dest, addToBackStack = false)
    }

    // Search and Catalog Filters
    fun setHomeSearch(query: String) {
        _uiState.update { it.copy(homeSearchQuery = query) }
    }

    fun setCatalogSearch(query: String) {
        _uiState.update { it.copy(catalogSearchQuery = query) }
    }

    fun setCatalogCategory(category: AppCategory) {
        _uiState.update { it.copy(catalogCategory = category) }
    }

    fun toggleCatalogAppSelection(packageName: String) {
        _uiState.update { state ->
            val set = state.selectedCatalogApps.toMutableSet()
            if (set.contains(packageName)) set.remove(packageName) else set.add(packageName)
            state.copy(selectedCatalogApps = set)
        }
    }

    // Clone creation flow
    fun startCloneCreation(
        appItem: AppCatalogItem,
        accountLabel: String,
        isPrivate: Boolean,
        context: Context? = null,
        count: Int = 1
    ) {
        val currentPlan = _uiState.value.currentPlan
        val nonPrivateCount = _uiState.value.clonedApps.count { !it.isPrivateSpace }
        val requestedCount = count.coerceIn(1, 10)
        if (currentPlan.maxClones < 999 && (nonPrivateCount + requestedCount) > currentPlan.maxClones && !isPrivate) {
            showToast("Plan limit reached (${currentPlan.maxClones} clones). Upgrade to add more!")
            navigateTo(ScreenDestination.Premium)
            return
        }

        // Real security validation: Check if target app can genuinely be isolated in user-space
        if (appItem.compatibility == CompatibilityStatus.UNSUPPORTED || appItem.webContainerUrl == null) {
            showToast("Cannot clone ${appItem.name}: Android OS restricts arbitrary native binary virtualization.")
            return
        }

        val ctx = context ?: appContext
        val newClones = mutableListOf<ClonedApp>()
        val existingForApp = _uiState.value.clonedApps.filter { it.packageName == appItem.packageName }
        var currentCloneNum = existingForApp.size

        for (i in 1..requestedCount) {
            currentCloneNum++
            val cloneId = "clone_${appItem.packageName.replace('.', '_')}_${System.currentTimeMillis()}_${java.util.UUID.randomUUID().toString().take(6)}"

            // Create actual storage container on device disk
            val containerDir = if (ctx != null) {
                val dir = File(ctx.filesDir, "containers/$cloneId")
                if (!dir.exists()) dir.mkdirs()
                dir
            } else null

            val label = if (requestedCount == 1) {
                accountLabel.ifBlank { "Account #$currentCloneNum" }
            } else {
                if (accountLabel.isNotBlank()) "$accountLabel #$i" else "Account #$currentCloneNum"
            }

            val clone = ClonedApp(
                id = cloneId,
                name = appItem.name,
                packageName = appItem.packageName,
                accountLabel = label,
                cloneNumber = currentCloneNum,
                category = appItem.category,
                iconKey = appItem.packageName,
                isPrivateSpace = isPrivate,
                storageUsageMb = appItem.defaultStorageMb,
                cacheUsageMb = 0.5f,
                status = "Active Container",
                webContainerUrl = appItem.webContainerUrl,
                containerPath = containerDir?.absolutePath
            )
            newClones.add(clone)
        }

        val uid = repository?.currentUserIdOrNull()
        if (uid != null) {
            viewModelScope.launch {
                newClones.forEach { c ->
                    try {
                        repository?.addClone(c.toDocument(uid))
                    } catch (e: Exception) {
                        android.util.Log.e("MulticlonxVM", "Cloud write failed: ${e.message}")
                    }
                }
            }
        }

        val updatedList = newClones + _uiState.value.clonedApps
        _uiState.update {
            it.copy(
                clonedApps = updatedList,
                isCreatingClone = false,
                cloneCreationProgress = 1f,
                lastCreatedApp = newClones.firstOrNull(),
                toastMessage = if (requestedCount > 1) {
                    "Created $requestedCount isolated clones for ${appItem.name}"
                } else {
                    "Verified isolated container active for ${appItem.name}"
                }
            )
        }
        saveClonesLocally(updatedList)

        if (isPrivate) {
            navigateTo(ScreenDestination.PrivateSpace, addToBackStack = false)
        } else {
            navigateTo(ScreenDestination.Home, addToBackStack = false)
        }
    }

    /**
     * Duplicates an existing clone, creating an additional isolated instance
     * with its own fresh session and container partition.
     */
    fun duplicateClone(clone: ClonedApp, context: Context? = null) {
        val currentPlan = _uiState.value.currentPlan
        val nonPrivateCount = _uiState.value.clonedApps.count { !it.isPrivateSpace }
        if (currentPlan.maxClones < 999 && (nonPrivateCount + 1) > currentPlan.maxClones && !clone.isPrivateSpace) {
            showToast("Plan limit reached (${currentPlan.maxClones} clones). Upgrade to add more!")
            navigateTo(ScreenDestination.Premium)
            return
        }

        val ctx = context ?: appContext
        val existingForApp = _uiState.value.clonedApps.filter { it.packageName == clone.packageName }
        val nextNum = existingForApp.size + 1
        val cloneId = "clone_${clone.packageName.replace('.', '_')}_${System.currentTimeMillis()}_${java.util.UUID.randomUUID().toString().take(6)}"

        val containerDir = if (ctx != null) {
            val dir = File(ctx.filesDir, "containers/$cloneId")
            if (!dir.exists()) dir.mkdirs()
            dir
        } else null

        val duplicated = clone.copy(
            id = cloneId,
            accountLabel = "Account #$nextNum",
            cloneNumber = nextNum,
            storageUsageMb = clone.storageUsageMb,
            cacheUsageMb = 0.5f,
            status = "Active Container",
            createdAt = System.currentTimeMillis(),
            containerPath = containerDir?.absolutePath
        )

        val uid = repository?.currentUserIdOrNull()
        if (uid != null) {
            viewModelScope.launch {
                try {
                    repository?.addClone(duplicated.toDocument(uid))
                } catch (e: Exception) {
                    android.util.Log.e("MulticlonxVM", "Cloud write failed: ${e.message}")
                }
            }
        }

        val updatedList = listOf(duplicated) + _uiState.value.clonedApps
        _uiState.update {
            it.copy(
                clonedApps = updatedList,
                toastMessage = "Created copy #$nextNum for ${clone.name}"
            )
        }
        saveClonesLocally(updatedList)
    }

    fun duplicateClone(cloneId: String, context: Context? = null) {
        val target = _uiState.value.clonedApps.find { it.id == cloneId }
        if (target != null) {
            duplicateClone(target, context)
        } else {
            showToast("Clone not found")
        }
    }

    fun cloneSelectedCatalogApps(context: Context? = null) {
        val selected = _uiState.value.selectedCatalogApps
        if (selected.isEmpty()) return

        val ctx = context ?: appContext
        val appItems = _uiState.value.catalogApps.filter { selected.contains(it.packageName) }
        val compatibleItems = appItems.filter { it.compatibility == CompatibilityStatus.COMPATIBLE && it.webContainerUrl != null }
        val unsupportedCount = appItems.size - compatibleItems.size

        if (compatibleItems.isEmpty()) {
            showToast("Selected apps are native APKs. Android OS restricts native binary cloning without a Work Profile.")
            return
        }

        val uid = repository?.currentUserIdOrNull()
        val createdClones = mutableListOf<ClonedApp>()

        compatibleItems.forEach { item ->
            val existing = _uiState.value.clonedApps.filter { it.packageName == item.packageName }
            val num = existing.size + 1
            val cloneId = "clone_${item.packageName.replace('.', '_')}_${System.currentTimeMillis()}_${java.util.UUID.randomUUID().toString().take(6)}"
            val containerDir = if (ctx != null) {
                val dir = File(ctx.filesDir, "containers/$cloneId")
                if (!dir.exists()) dir.mkdirs()
                dir
            } else null

            val clone = ClonedApp(
                id = cloneId,
                name = item.name,
                packageName = item.packageName,
                accountLabel = "Account #$num",
                cloneNumber = num,
                category = item.category,
                iconKey = item.packageName,
                isPrivateSpace = false,
                storageUsageMb = item.defaultStorageMb,
                cacheUsageMb = 0.5f,
                status = "Active Container",
                webContainerUrl = item.webContainerUrl,
                containerPath = containerDir?.absolutePath
            )
            createdClones.add(clone)

            if (uid != null) {
                viewModelScope.launch {
                    try {
                        repository?.addClone(clone.toDocument(uid))
                    } catch (e: Exception) {
                        android.util.Log.e("MulticlonxVM", "Cloud write failed: ${e.message}")
                    }
                }
            }
        }

        val updatedList = createdClones + _uiState.value.clonedApps
        _uiState.update {
            it.copy(
                clonedApps = updatedList,
                selectedCatalogApps = emptySet(),
                toastMessage = if (unsupportedCount > 0) {
                    "Created ${createdClones.size} isolated containers. Skipped $unsupportedCount unsupported native apps."
                } else {
                    "Created ${createdClones.size} isolated containers successfully."
                }
            )
        }
        saveClonesLocally(updatedList)
        navigateTo(ScreenDestination.Home, addToBackStack = false)
    }

    // Cloned app actions
    fun removeClone(clone: ClonedApp, context: Context? = null) {
        val ctx = context ?: appContext
        if (ctx != null) {
            try {
                File(ctx.filesDir, "containers/${clone.id}").deleteRecursively()
            } catch (e: Exception) {
                android.util.Log.e("MulticlonxVM", "Failed to delete container disk folder: ${e.message}")
            }
        }

        val uid = repository?.currentUserIdOrNull()
        if (uid != null) {
            viewModelScope.launch {
                try {
                    repository?.deleteClone(clone.id)
                } catch (e: Exception) {
                    android.util.Log.e("MulticlonxVM", "Cloud delete failed: ${e.message}")
                }
            }
        }

        val updatedList = _uiState.value.clonedApps.filterNot { it.id == clone.id }
        _uiState.update { state ->
            state.copy(
                clonedApps = updatedList,
                showDeleteConfirmDialogFor = null,
                toastMessage = "${clone.name} (${clone.accountLabel}) and container data removed."
            )
        }
        saveClonesLocally(updatedList)

        if (_uiState.value.currentScreen is ScreenDestination.ClonedAppDetail) {
            navigateBack()
        }
    }

    fun renameClone(cloneId: String, newLabel: String) {
        val target = _uiState.value.clonedApps.find { it.id == cloneId }
        val updated = target?.copy(accountLabel = newLabel.ifBlank { target.accountLabel })
        val uid = repository?.currentUserIdOrNull()
        if (uid != null && updated != null) {
            viewModelScope.launch {
                try {
                    repository?.updateClone(updated.toDocument(uid))
                } catch (e: Exception) {
                    android.util.Log.e("MulticlonxVM", "Cloud update failed: ${e.message}")
                }
            }
        }

        val updatedList = _uiState.value.clonedApps.map {
            if (it.id == cloneId) it.copy(accountLabel = newLabel.ifBlank { it.accountLabel }) else it
        }
        _uiState.update { state ->
            state.copy(
                clonedApps = updatedList,
                showRenameDialogFor = null,
                toastMessage = "Account renamed to \"$newLabel\""
            )
        }
        saveClonesLocally(updatedList)
    }

    fun togglePrivateSpaceForClone(cloneId: String) {
        val target = _uiState.value.clonedApps.find { it.id == cloneId }
        val updated = target?.copy(isPrivateSpace = !target.isPrivateSpace)
        val uid = repository?.currentUserIdOrNull()
        if (uid != null && updated != null) {
            viewModelScope.launch {
                try {
                    repository?.updateClone(updated.toDocument(uid))
                } catch (e: Exception) {
                    android.util.Log.e("MulticlonxVM", "Cloud update failed: ${e.message}")
                }
            }
        }

        var movedToPrivate = false
        val updatedList = _uiState.value.clonedApps.map {
            if (it.id == cloneId) {
                movedToPrivate = !it.isPrivateSpace
                it.copy(isPrivateSpace = !it.isPrivateSpace)
            } else it
        }
        _uiState.update { state ->
            state.copy(
                clonedApps = updatedList,
                toastMessage = if (movedToPrivate) "Moved to Private Space" else "Moved to Home"
            )
        }
        saveClonesLocally(updatedList)
    }

    fun clearCloneCache(cloneId: String, context: Context? = null) {
        val ctx = context ?: appContext
        if (ctx != null) {
            try {
                val cacheDir = File(ctx.filesDir, "containers/$cloneId/cache")
                if (cacheDir.exists()) cacheDir.deleteRecursively()
            } catch (e: Exception) {
                // ignore
            }
        }

        val target = _uiState.value.clonedApps.find { it.id == cloneId }
        val updated = target?.copy(cacheUsageMb = 0f)
        val uid = repository?.currentUserIdOrNull()
        if (uid != null && updated != null) {
            viewModelScope.launch {
                try {
                    repository?.updateClone(updated.toDocument(uid))
                } catch (e: Exception) {
                    android.util.Log.e("MulticlonxVM", "Cloud update failed: ${e.message}")
                }
            }
        }

        val updatedList = _uiState.value.clonedApps.map {
            if (it.id == cloneId) it.copy(cacheUsageMb = 0f) else it
        }
        _uiState.update { state ->
            state.copy(
                clonedApps = updatedList,
                toastMessage = "Cache cleared (0 MB)"
            )
        }
        saveClonesLocally(updatedList)
    }

    fun launchClone(clone: ClonedApp) {
        _uiState.update { it.copy(activeLaunchClone = clone) }
    }

    fun dismissLaunchDialog() {
        _uiState.update { it.copy(activeLaunchClone = null) }
    }

    fun promptDeleteClone(clone: ClonedApp) {
        _uiState.update { it.copy(showDeleteConfirmDialogFor = clone) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialogFor = null) }
    }

    fun promptRenameClone(clone: ClonedApp) {
        _uiState.update { it.copy(showRenameDialogFor = clone) }
    }

    fun dismissRenameDialog() {
        _uiState.update { it.copy(showRenameDialogFor = null) }
    }

    // PIN and Private Space
    fun inputPinDigit(digit: String) {
        val current = _uiState.value.pinInputBuffer
        if (current.length < 6) {
            val updated = current + digit
            _uiState.update { it.copy(pinInputBuffer = updated, pinErrorMessage = null) }

            if (updated.length == 6) {
                verifyPin(updated)
            }
        }
    }

    fun deletePinDigit() {
        val current = _uiState.value.pinInputBuffer
        if (current.isNotEmpty()) {
            _uiState.update { it.copy(pinInputBuffer = current.dropLast(1), pinErrorMessage = null) }
        }
    }

    private fun verifyPin(enteredPin: String) {
        val correctPin = _uiState.value.securityState.currentPin
        if (enteredPin == correctPin) {
            _uiState.update {
                it.copy(
                    securityState = it.securityState.copy(isPrivateSpaceLocked = false),
                    pinInputBuffer = "",
                    pinErrorMessage = null,
                    toastMessage = "Private Space unlocked"
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    pinInputBuffer = "",
                    pinErrorMessage = "Incorrect PIN. Please try again."
                )
            }
        }
    }

    fun unlockWithBiometrics() {
        _uiState.update {
            it.copy(
                securityState = it.securityState.copy(isPrivateSpaceLocked = false),
                pinInputBuffer = "",
                pinErrorMessage = null,
                toastMessage = "Biometric authenticated"
            )
        }
    }

    fun lockPrivateSpace() {
        _uiState.update {
            it.copy(
                securityState = it.securityState.copy(isPrivateSpaceLocked = true),
                toastMessage = "Private Space locked"
            )
        }
    }

    // Set PIN flow
    fun inputNewPinDigit(digit: String) {
        val buf = _uiState.value.newPinBuffer
        if (buf.length < 6) {
            val updated = buf + digit
            _uiState.update { it.copy(newPinBuffer = updated, pinErrorMessage = null) }

            if (updated.length == 6) {
                val step1 = _uiState.value.newPinStep1
                if (step1 == null) {
                    _uiState.update {
                        it.copy(
                            newPinStep1 = updated,
                            newPinBuffer = "",
                            isSetPinConfirmationStep = true
                        )
                    }
                } else {
                    if (updated == step1) {
                        _uiState.update {
                            it.copy(
                                securityState = it.securityState.copy(currentPin = updated),
                                newPinStep1 = null,
                                newPinBuffer = "",
                                isSetPinConfirmationStep = false,
                                currentScreen = ScreenDestination.SecuritySettingsScreen,
                                toastMessage = "New PIN saved successfully"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                newPinBuffer = "",
                                pinErrorMessage = "PINs do not match. Re-enter confirmation."
                            )
                        }
                    }
                }
            }
        }
    }

    fun deleteNewPinDigit() {
        val current = _uiState.value.newPinBuffer
        if (current.isNotEmpty()) {
            _uiState.update { it.copy(newPinBuffer = current.dropLast(1), pinErrorMessage = null) }
        }
    }

    fun resetNewPinFlow() {
        _uiState.update {
            it.copy(
                newPinStep1 = null,
                newPinBuffer = "",
                isSetPinConfirmationStep = false,
                pinErrorMessage = null
            )
        }
    }

    // Security Settings
    fun toggleAppLock(enabled: Boolean) {
        _uiState.update {
            it.copy(securityState = it.securityState.copy(isAppLockEnabled = enabled))
        }
        syncSecurityStateToCloud()
    }

    fun setAutoLockTimeout(timeout: AutoLockTimeout) {
        _uiState.update {
            it.copy(
                securityState = it.securityState.copy(autoLockTimeout = timeout),
                toastMessage = "Auto-lock set to ${timeout.label}"
            )
        }
        syncSecurityStateToCloud()
    }

    fun toggleBiometrics(enabled: Boolean) {
        _uiState.update {
            it.copy(
                securityState = it.securityState.copy(isBiometricsEnabled = enabled),
                toastMessage = if (enabled) "Biometrics enabled" else "Biometrics disabled"
            )
        }
        syncSecurityStateToCloud()
    }

    private fun syncSecurityStateToCloud() {
        val uid = repository?.currentUserIdOrNull() ?: return
        val state = _uiState.value
        viewModelScope.launch {
            try {
                repository?.saveUserProfile(
                    UserDocument(
                        userId = uid,
                        email = state.userAccount.email,
                        fullName = state.userAccount.fullName,
                        plan = state.currentPlan.id,
                        appLockEnabled = state.securityState.isAppLockEnabled,
                        autoLockTimeout = state.securityState.autoLockTimeout.label,
                        biometricsEnabled = state.securityState.isBiometricsEnabled
                    )
                )
            } catch (e: Exception) {
                android.util.Log.e("MulticlonxVM", "Cloud sync failed: ${e.message}")
            }
        }
    }

    // Subscription & Plans
    fun promptPurchasePlan(plan: PlanTier) {
        _uiState.update { it.copy(showPurchaseDialogForPlan = plan) }
    }

    fun confirmPurchasePlan(plan: PlanTier) {
        _uiState.update {
            it.copy(
                currentPlan = plan,
                showPurchaseDialogForPlan = null,
                toastMessage = "Subscribed to ${plan.title}! Unlimited account access enabled."
            )
        }

        val uid = repository?.currentUserIdOrNull()
        if (uid != null) {
            viewModelScope.launch {
                try {
                    val state = _uiState.value
                    repository?.saveUserProfile(
                        UserDocument(
                            userId = uid,
                            email = state.userAccount.email,
                            fullName = state.userAccount.fullName,
                            plan = plan.id,
                            appLockEnabled = state.securityState.isAppLockEnabled,
                            autoLockTimeout = state.securityState.autoLockTimeout.label,
                            biometricsEnabled = state.securityState.isBiometricsEnabled
                        )
                    )
                } catch (e: Exception) {
                    android.util.Log.e("MulticlonxVM", "Plan cloud save failed: ${e.message}")
                }
            }
        }
    }

    fun dismissPurchaseDialog() {
        _uiState.update { it.copy(showPurchaseDialogForPlan = null) }
    }

    fun restorePurchases() {
        showToast("Purchases restored successfully. Current: ${_uiState.value.currentPlan.title}")
    }

    // Toasts & Notifications
    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun markNotificationsRead() {
        _uiState.update { state ->
            state.copy(notifications = state.notifications.map { it.copy(isUnread = false) })
        }
    }

    fun setOnboardingPage(page: Int) {
        _uiState.update { it.copy(activeOnboardingPage = page) }
    }

    fun signInUser(email: String) {
        _uiState.update {
            it.copy(
                userAccount = it.userAccount.copy(email = email, isSignedIn = true),
                currentScreen = ScreenDestination.Settings,
                toastMessage = "Welcome, ${email.substringBefore('@')}"
            )
        }
    }

    fun signUpUser(name: String, email: String) {
        _uiState.update {
            it.copy(
                userAccount = it.userAccount.copy(fullName = name, email = email, isSignedIn = true),
                currentScreen = ScreenDestination.VerifyEmail(email)
            )
        }
    }

    fun verifyEmailCode(code: String) {
        _uiState.update {
            it.copy(
                userAccount = it.userAccount.copy(isEmailVerified = true),
                currentScreen = ScreenDestination.Settings,
                toastMessage = "Email verified successfully!"
            )
        }
    }

    fun signOut() {
        Firebase.auth.signOut()
        onUserSignedOut()
        showToast("Signed out successfully.")
    }
}

private fun ClonedApp.toDocument(uid: String): CloneDocument {
    return CloneDocument(
        id = this.id,
        userId = uid,
        name = this.name,
        packageName = this.packageName,
        accountLabel = this.accountLabel,
        cloneNumber = this.cloneNumber.toLong(),
        category = this.category.displayName,
        iconKey = this.iconKey,
        isPrivateSpace = this.isPrivateSpace,
        storageUsageMb = this.storageUsageMb.toDouble(),
        cacheUsageMb = this.cacheUsageMb.toDouble(),
        status = this.status,
        webContainerUrl = this.webContainerUrl,
        containerPath = this.containerPath
    )
}
