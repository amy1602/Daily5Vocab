package com.amy.daily5vocab.data.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * Stores per-user learning preferences in Firestore under `users/{uid}`.
 * The first entry in [save]'s topic list is treated as the user's default topic.
 */
class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private fun userDoc() =
        auth.currentUser?.uid?.let { firestore.collection("users").document(it) }

    /** Persists the selected [topics] for the signed-in user. */
    fun saveTopics(topics: List<String>, onResult: (Result<Unit>) -> Unit) {
        val doc = userDoc() ?: return onResult(notSignedIn())
        doc.set(mapOf("topics" to topics), SetOptions.merge())
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    /** Loads the saved topics for the signed-in user (empty list if none yet). */
    fun getTopics(onResult: (Result<List<String>>) -> Unit) {
        val doc = userDoc() ?: return onResult(notSignedIn())
        doc.get()
            .addOnSuccessListener { snapshot ->
                @Suppress("UNCHECKED_CAST")
                val topics = (snapshot.get("topics") as? List<String>).orEmpty()
                onResult(Result.success(topics))
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    private fun <T> notSignedIn(): Result<T> =
        Result.failure(IllegalStateException("You need to be signed in."))
}
