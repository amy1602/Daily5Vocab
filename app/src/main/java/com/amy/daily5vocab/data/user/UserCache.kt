package com.amy.daily5vocab.data.user

import android.content.Context

/**
 * Two-tier cache for per-user data (topics, reminder time): an in-memory layer backed by
 * [android.content.SharedPreferences]. Lets screens load instantly and keeps Firebase reads
 * to a minimum — the server is only consulted on a cache miss (or an explicit refresh).
 *
 * Entries are keyed by the Firebase uid, so a different signed-in user always misses the
 * previous user's cache. [init] should be called once at app startup; if it isn't, the
 * cache still works in memory only (no persistence).
 */
object UserCache {
    private const val PREFS = "user_cache"
    private const val KEY_UID = "uid"
    private const val KEY_TOPICS = "topics"
    private const val KEY_REMINDER = "reminderTime"
    private const val SEPARATOR = "\n"

    private var prefs: android.content.SharedPreferences? = null

    private var memUid: String? = null
    private var memTopics: List<String>? = null
    private var memReminderTime: String? = null
    private var hydrated = false

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
    }

    /** Loads persisted values into memory once, lazily, on first access. */
    private fun hydrate() {
        if (hydrated) return
        prefs?.let { p ->
            memUid = p.getString(KEY_UID, null)
            memTopics = if (p.contains(KEY_TOPICS)) decodeTopics(p.getString(KEY_TOPICS, "")!!) else null
            memReminderTime = p.getString(KEY_REMINDER, null)
        }
        hydrated = true
    }

    /** Cached topics for [uid], or null on a miss (different/no cached user, or never stored). */
    @Synchronized
    fun topics(uid: String): List<String>? {
        hydrate()
        return if (memUid == uid) memTopics else null
    }

    @Synchronized
    fun setTopics(uid: String, topics: List<String>) {
        hydrate()
        ensureUser(uid)
        memTopics = topics
        prefs?.edit()?.putString(KEY_TOPICS, encodeTopics(topics))?.apply()
    }

    /** Cached reminder time for [uid], or null on a miss (only non-null values are stored). */
    @Synchronized
    fun reminderTime(uid: String): String? {
        hydrate()
        return if (memUid == uid) memReminderTime else null
    }

    @Synchronized
    fun setReminderTime(uid: String, time: String) {
        hydrate()
        ensureUser(uid)
        memReminderTime = time
        prefs?.edit()?.putString(KEY_REMINDER, time)?.apply()
    }

    /** Drops everything (call on sign-out). */
    @Synchronized
    fun clear() {
        memUid = null
        memTopics = null
        memReminderTime = null
        hydrated = true
        prefs?.edit()?.clear()?.apply()
    }

    /** When the active user changes, discard the previous user's cached values. */
    private fun ensureUser(uid: String) {
        if (memUid != uid) {
            memUid = uid
            memTopics = null
            memReminderTime = null
            prefs?.edit()?.clear()?.putString(KEY_UID, uid)?.apply()
        }
    }

    private fun encodeTopics(topics: List<String>): String = topics.joinToString(SEPARATOR)

    private fun decodeTopics(raw: String): List<String> =
        if (raw.isEmpty()) emptyList() else raw.split(SEPARATOR)
}
