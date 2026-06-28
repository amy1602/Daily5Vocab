package com.amy.daily5vocab.data.history

/**
 * A single vocabulary word shown to the user on a given [date], with whether they have
 * marked it learned. [id] is stable ("{date}_{word}") so the same word on the same day
 * maps to one row/document across memory, the local DB, and Firebase.
 */
data class LearnedWord(
    val id: String,
    val word: String,
    val topic: String,
    val date: String, // yyyy-MM-dd
    val learned: Boolean,
    val updatedAt: Long,
)
