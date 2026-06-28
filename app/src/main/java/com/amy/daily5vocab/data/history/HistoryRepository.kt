package com.amy.daily5vocab.data.history

import android.content.Context
import com.amy.daily5vocab.data.words.WordBank
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Single source of truth for the daily words a user is shown and which they've learned.
 *
 * Coordinates three layers, per the required strategy:
 *  - **memory** — survives navigation within a session; the UI reads from it.
 *  - **local DB** ([HistoryDatabase]) — read first for the soonest data; written back when
 *    the server returns something new.
 *  - **Firebase** — the source of record; read after the DB to refresh, paged the same way.
 *
 * History and Today both page DB-first then refresh from Firebase. Toggling a word updates
 * memory, DB, and Firebase together, so reopening History reflects it straight from memory.
 */
object HistoryRepository {
    private const val PAGE_SIZE = 20

    private lateinit var db: HistoryDatabase
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val memory = LinkedHashMap<String, LearnedWord>()
    private var historyLoaded = false
    private var dbOffset = 0
    private var dbDone = false
    private var fbCursor: DocumentSnapshot? = null
    private var fbDone = false

    fun init(context: Context) {
        if (!::db.isInitialized) db = HistoryDatabase.getInstance(context)
    }

    /** Resets all session state (call on sign-out). */
    fun clearSession() {
        memory.clear()
        historyLoaded = false
        dbOffset = 0
        dbDone = false
        fbCursor = null
        fbDone = false
    }

    fun isHistoryLoaded(): Boolean = historyLoaded
    fun hasMore(): Boolean = !dbDone || !fbDone

    fun snapshot(): List<LearnedWord> =
        memory.values.sortedWith(compareByDescending<LearnedWord> { it.date }.thenBy { it.word })

    fun todayWords(): List<LearnedWord> {
        val today = todayDate()
        return memory.values.filter { it.date == today }
    }

    // ---------- History paging (DB first, then Firebase refresh) ----------

    fun loadHistoryPage(onUpdate: (List<LearnedWord>, Boolean) -> Unit) {
        val uid = uid() ?: return
        historyLoaded = true

        if (!dbDone) {
            db.page(uid, PAGE_SIZE, dbOffset) { dbWords ->
                if (dbWords.size < PAGE_SIZE) dbDone = true
                dbOffset += dbWords.size
                put(dbWords)
                onUpdate(snapshot(), hasMore())
            }
        }

        if (!fbDone) {
            var query: Query = wordsCol(uid)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE.toLong())
            fbCursor?.let { query = query.startAfter(it) }
            query.get()
                .addOnSuccessListener { snap ->
                    if (snap.size() < PAGE_SIZE) fbDone = true
                    if (!snap.isEmpty) fbCursor = snap.documents.last()
                    val words = snap.documents.mapNotNull { it.toLearnedWord() }
                    if (words.isNotEmpty()) {
                        put(words)
                        db.upsertAll(uid, words) // cache the server data locally
                    }
                    onUpdate(snapshot(), hasMore())
                }
                .addOnFailureListener { /* keep showing local data */ }
        }
    }

    // ---------- Today ----------

    fun loadToday(topic: String, onUpdate: (List<LearnedWord>) -> Unit) {
        val uid = uid() ?: return
        val today = todayDate()

        val inMemory = todayWords()
        if (inMemory.isNotEmpty() && inMemory.first().topic == topic) {
            onUpdate(inMemory)
            return
        }

        // 1) DB first for the soonest data.
        db.wordsForDate(uid, today) { dbWords ->
            val dbMatches = dbWords.isNotEmpty() && dbWords.first().topic == topic
            if (dbMatches) {
                put(dbWords)
                onUpdate(todayWords())
            }
            // 2) Firebase refresh; generate a fresh set if nothing exists yet.
            wordsCol(uid).whereEqualTo("date", today).get()
                .addOnSuccessListener { snap ->
                    val fbWords = snap.documents.mapNotNull { it.toLearnedWord() }
                        .filter { it.topic == topic }
                    when {
                        fbWords.isNotEmpty() -> {
                            put(fbWords)
                            db.upsertAll(uid, fbWords)
                            onUpdate(todayWords())
                        }
                        dbMatches -> Unit // already emitted from DB
                        else -> generateAndPersistToday(uid, topic, today, onUpdate)
                    }
                }
                .addOnFailureListener {
                    if (!dbMatches) generateAndPersistToday(uid, topic, today, onUpdate)
                }
        }
    }

    fun toggleLearned(id: String, learned: Boolean) {
        val uid = uid() ?: return
        val current = memory[id] ?: return
        val updated = current.copy(learned = learned, updatedAt = System.currentTimeMillis())
        memory[id] = updated
        db.setLearned(uid, id, learned, updated.updatedAt)
        saveToFirebase(uid, updated)
    }

    // ---------- internals ----------

    private fun generateAndPersistToday(
        uid: String,
        topic: String,
        today: String,
        onUpdate: (List<LearnedWord>) -> Unit,
    ) {
        val generated = WordBank.pickWords(topic, count = 5).map {
            LearnedWord(
                id = "${today}_${it.term}",
                word = it.term,
                topic = topic,
                date = today,
                learned = false,
                updatedAt = System.currentTimeMillis(),
            )
        }
        put(generated)
        db.upsertAll(uid, generated)
        generated.forEach { saveToFirebase(uid, it) }
        onUpdate(todayWords())
    }

    private fun put(words: List<LearnedWord>) {
        words.forEach { memory[it.id] = it }
    }

    private fun saveToFirebase(uid: String, w: LearnedWord) {
        wordsCol(uid).document(w.id).set(
            mapOf(
                "word" to w.word,
                "topic" to w.topic,
                "date" to w.date,
                "learned" to w.learned,
                "updatedAt" to w.updatedAt,
            ),
        )
    }

    private fun uid(): String? = auth.currentUser?.uid

    private fun wordsCol(uid: String) =
        firestore.collection("users").document(uid).collection("words")

    private fun DocumentSnapshot.toLearnedWord(): LearnedWord? {
        val word = getString("word") ?: return null
        val date = getString("date") ?: return null
        return LearnedWord(
            id = id,
            word = word,
            topic = getString("topic").orEmpty(),
            date = date,
            learned = getBoolean("learned") ?: false,
            updatedAt = getLong("updatedAt") ?: 0L,
        )
    }

    private fun todayDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}
