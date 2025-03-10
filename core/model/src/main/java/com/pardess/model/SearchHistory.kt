package com.pardess.model



data class SearchHistory(
    val id: Long,
    val type: SearchType,
    val image: String?,
    val text: String,
    val timestamp: Long,
)

enum class SearchType(val num: Int, val text: String) {
    TEXT(1, "text"),
    SONG(2, "song"),
    ALBUM(3, "album"),
    ARTIST(4, "artist"),
}