package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserDocument(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val fullName: String = "",
    val plan: String = "free",
    val appLockEnabled: Boolean = true,
    val autoLockTimeout: String = "After 1 minute",
    val biometricsEnabled: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CloneDocument(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val packageName: String = "",
    val accountLabel: String = "",
    val cloneNumber: Long = 1L,
    val category: String = "Messaging",
    val iconKey: String = "whatsapp",
    val isPrivateSpace: Boolean = false,
    val storageUsageMb: Double = 0.0,
    val cacheUsageMb: Double = 0.0,
    val status: String = "Ready",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
