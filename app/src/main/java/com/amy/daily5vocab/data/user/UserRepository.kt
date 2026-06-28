package com.amy.daily5vocab.data.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * Stores per-user learning preferences in Firestore under `users/{uid}`, fronted by
 * [UserCache] so reads are served from memory/SharedPreferences and only fall through to
 * the server on a cache miss (or when [forceRefresh] is requested). Writes update the cache
 * immediately so the UI reflects changes without waiting on the network.
 *
 * The first entry in [saveTopics]'s list is treated as the user's default topic.
 */
class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private fun userDoc() =
        auth.currentUser?.uid?.let { firestore.collection("users").document(it) }

    /** Persists the selected [topics] for the signed-in user. */
    fun saveTopics(topics: List<String>, onResult: (Result<Unit>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(notSignedIn())
        UserCache.setTopics(uid, topics)
        firestore.collection("users").document(uid)
            .set(mapOf("topics" to topics), SetOptions.merge())
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    /** Loads the saved topics (empty list if none yet), cache-first. */
    fun getTopics(forceRefresh: Boolean = false, onResult: (Result<List<String>>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(notSignedIn())
        if (!forceRefresh) {
            UserCache.topics(uid)?.let { return onResult(Result.success(it)) }
        }
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { snapshot ->
                @Suppress("UNCHECKED_CAST")
                val topics = (snapshot.get("topics") as? List<String>).orEmpty()
                UserCache.setTopics(uid, topics)
                onResult(Result.success(topics))
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    /** Persists the daily-reminder [time] (formatted like "09:00 AM") for the signed-in user. */
    fun saveReminderTime(time: String, onResult: (Result<Unit>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(notSignedIn())
        UserCache.setReminderTime(uid, time)
        firestore.collection("users").document(uid)
            .set(mapOf("reminderTime" to time), SetOptions.merge())
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    /** Loads the saved reminder time (null if not set yet), cache-first. */
    fun getReminderTime(forceRefresh: Boolean = false, onResult: (Result<String?>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(notSignedIn())
        if (!forceRefresh) {
            UserCache.reminderTime(uid)?.let { return onResult(Result.success(it)) }
        }
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { snapshot ->
                val time = snapshot.getString("reminderTime")
                if (time != null) UserCache.setReminderTime(uid, time)
                onResult(Result.success(time))
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    private fun <T> notSignedIn(): Result<T> =
        Result.failure(IllegalStateException("You need to be signed in."))
}
