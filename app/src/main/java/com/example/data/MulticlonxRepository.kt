package com.example.data

import android.content.Context
import com.example.R
import com.example.model.CloneDocument
import com.example.model.UserDocument
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class MulticlonxRepository(
    private val db: FirebaseFirestore
) {
    companion object {
        fun create(context: Context): MulticlonxRepository {
            val dbId = context.applicationContext.getString(R.string.firestore_database_id)
            val firestore = FirebaseFirestore.getInstance(dbId)
            return MulticlonxRepository(firestore)
        }
    }

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun currentUserIdOrNull(): String? = auth.currentUser?.uid

    // Observe User profile document
    fun observeUser(userId: String): Flow<UserDocument?> = callbackFlow {
        val path = "users/$userId"
        val docRef = db.collection("users").document(userId)
        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.GET, path)
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(UserDocument::class.java)
                trySend(user)
            } else {
                trySend(null)
            }
        }
        awaitClose { registration.remove() }
    }

    // Save or update user profile
    suspend fun saveUserProfile(user: UserDocument) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                val data = mapOf(
                    "userId" to uid,
                    "email" to user.email,
                    "fullName" to user.fullName,
                    "plan" to user.plan,
                    "appLockEnabled" to user.appLockEnabled,
                    "autoLockTimeout" to user.autoLockTimeout,
                    "biometricsEnabled" to user.biometricsEnabled,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(data).await()
            } else {
                val existingCreatedAt = snapshot.getTimestamp("createdAt")
                val data = mapOf(
                    "userId" to uid,
                    "email" to user.email,
                    "fullName" to user.fullName,
                    "plan" to user.plan,
                    "appLockEnabled" to user.appLockEnabled,
                    "autoLockTimeout" to user.autoLockTimeout,
                    "biometricsEnabled" to user.biometricsEnabled,
                    "createdAt" to (existingCreatedAt ?: FieldValue.serverTimestamp()),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(data).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    // Observe Cloned Apps subcollection
    fun observeClones(userId: String): Flow<List<CloneDocument>> = callbackFlow {
        val path = "users/$userId/clones"
        val colRef = db.collection("users").document(userId).collection("clones")
        val registration = colRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, path)
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.toObjects(CloneDocument::class.java) ?: emptyList()
            trySend(list)
        }
        awaitClose { registration.remove() }
    }

    // Add a cloned app
    suspend fun addClone(clone: CloneDocument) {
        val uid = requireUserId()
        val docId = clone.id.ifBlank { db.collection("users").document(uid).collection("clones").document().id }
        val docRef = db.collection("users").document(uid).collection("clones").document(docId)

        val payload = mapOf(
            "id" to docId,
            "userId" to uid,
            "name" to clone.name,
            "packageName" to clone.packageName,
            "accountLabel" to clone.accountLabel,
            "cloneNumber" to clone.cloneNumber,
            "category" to clone.category,
            "iconKey" to clone.iconKey,
            "isPrivateSpace" to clone.isPrivateSpace,
            "storageUsageMb" to clone.storageUsageMb,
            "cacheUsageMb" to clone.cacheUsageMb,
            "status" to clone.status,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    // Update clone metadata (label, private space, cache)
    suspend fun updateClone(clone: CloneDocument) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("clones").document(clone.id)
        try {
            val existing = docRef.get().await()
            val existingCreatedAt = existing.getTimestamp("createdAt")

            val payload = mapOf(
                "id" to clone.id,
                "userId" to uid,
                "name" to clone.name,
                "packageName" to clone.packageName,
                "accountLabel" to clone.accountLabel,
                "cloneNumber" to clone.cloneNumber,
                "category" to clone.category,
                "iconKey" to clone.iconKey,
                "isPrivateSpace" to clone.isPrivateSpace,
                "storageUsageMb" to clone.storageUsageMb,
                "cacheUsageMb" to clone.cacheUsageMb,
                "status" to clone.status,
                "createdAt" to (existingCreatedAt ?: FieldValue.serverTimestamp()),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // Delete clone from Firestore
    suspend fun deleteClone(cloneId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("clones").document(cloneId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }
}
