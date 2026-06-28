package com.amy.daily5vocab.ui.settings

/** Parses an "hh:mm AM/PM" string into a (hour-of-day, minute) pair; falls back to 09:00. */
fun parseTime(text: String): Pair<Int, Int> = runCatching {
    val (clock, meridiem) = text.trim().split(" ", limit = 2)
    val (hour12, minute) = clock.split(":").map { it.toInt() }
    val hour = when {
        meridiem.equals("AM", ignoreCase = true) -> if (hour12 == 12) 0 else hour12
        else -> if (hour12 == 12) 12 else hour12 + 12
    }
    hour to minute
}.getOrDefault(9 to 0)

/** Formats a 24-hour (hour, minute) into a display string like "09:00 AM". */
fun formatTime(hour: Int, minute: Int): String {
    val meridiem = if (hour < 12) "AM" else "PM"
    val hour12 = if (hour % 12 == 0) 12 else hour % 12
    return "%02d:%02d %s".format(hour12, minute, meridiem)
}
