package com.amy.daily5vocab.data.history

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

/**
 * Local SQLite store of [LearnedWord] rows, keyed by user. All reads/writes run on a
 * single background thread; results are posted back on the main thread so callers keep
 * the app's callback style. Supports paged reads via LIMIT/OFFSET.
 */
class HistoryDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, NAME, null, VERSION) {

    private val io = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE (" +
                "id TEXT NOT NULL, uid TEXT NOT NULL, word TEXT, topic TEXT, " +
                "date TEXT, learned INTEGER, updatedAt INTEGER, PRIMARY KEY(uid, id))",
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    /** A page of the user's words, most recent day first. */
    fun page(uid: String, limit: Int, offset: Int, callback: (List<LearnedWord>) -> Unit) = io.execute {
        val list = read(
            "SELECT id, word, topic, date, learned, updatedAt FROM $TABLE " +
                "WHERE uid=? ORDER BY date DESC, word ASC LIMIT ? OFFSET ?",
            arrayOf(uid, limit.toString(), offset.toString()),
        )
        main.post { callback(list) }
    }

    fun wordsForDate(uid: String, date: String, callback: (List<LearnedWord>) -> Unit) = io.execute {
        val list = read(
            "SELECT id, word, topic, date, learned, updatedAt FROM $TABLE " +
                "WHERE uid=? AND date=? ORDER BY word ASC",
            arrayOf(uid, date),
        )
        main.post { callback(list) }
    }

    fun upsertAll(uid: String, words: List<LearnedWord>) = io.execute {
        val db = writableDatabase
        db.beginTransaction()
        try {
            words.forEach { db.insertWithOnConflict(TABLE, null, values(uid, it), SQLiteDatabase.CONFLICT_REPLACE) }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun setLearned(uid: String, id: String, learned: Boolean, updatedAt: Long) = io.execute {
        val cv = ContentValues().apply {
            put("learned", if (learned) 1 else 0)
            put("updatedAt", updatedAt)
        }
        writableDatabase.update(TABLE, cv, "uid=? AND id=?", arrayOf(uid, id))
    }

    private fun read(sql: String, args: Array<String>): List<LearnedWord> {
        readableDatabase.rawQuery(sql, args).use { c ->
            val result = ArrayList<LearnedWord>(c.count)
            while (c.moveToNext()) result.add(c.toLearnedWord())
            return result
        }
    }

    private fun values(uid: String, w: LearnedWord) = ContentValues().apply {
        put("id", w.id)
        put("uid", uid)
        put("word", w.word)
        put("topic", w.topic)
        put("date", w.date)
        put("learned", if (w.learned) 1 else 0)
        put("updatedAt", w.updatedAt)
    }

    private fun Cursor.toLearnedWord() = LearnedWord(
        id = getString(0),
        word = getString(1),
        topic = getString(2),
        date = getString(3),
        learned = getInt(4) == 1,
        updatedAt = getLong(5),
    )

    companion object {
        private const val NAME = "history.db"
        private const val VERSION = 1
        private const val TABLE = "words"

        @Volatile
        private var instance: HistoryDatabase? = null

        fun getInstance(context: Context): HistoryDatabase =
            instance ?: synchronized(this) {
                instance ?: HistoryDatabase(context).also { instance = it }
            }
    }
}
