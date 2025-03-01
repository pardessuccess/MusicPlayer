package com.pardess.musicplayer.data

import android.provider.MediaStore

enum class SortField(val column: String){
    ARTIST(MediaStore.Audio.Artists.ARTIST),
    ALBUM(MediaStore.Audio.Media.ALBUM),
    TITLE(MediaStore.Audio.Media.TITLE),
    YEAR(MediaStore.Audio.Media.YEAR),
    DURATION(MediaStore.Audio.Media.DURATION),
    DATE_ADDED(MediaStore.Audio.Media.DATE_ADDED),
    DATE_MODIFIED(MediaStore.Audio.Media.DATE_MODIFIED),
    TRACK(MediaStore.Audio.Media.TRACK)
}

fun orderBy(field: SortField, ascending: Boolean = true): String {
    return if (ascending) field.column else "${field.column} DESC"
}

object ArtistSortOrder {
    val ARTIST_A_Z = orderBy(SortField.ARTIST)
    val ARTIST_Z_A = orderBy(SortField.ARTIST, ascending = false)
}

object AlbumSortOrder {
    val ALBUM_A_Z = orderBy(SortField.ALBUM)
    val ALBUM_Z_A = orderBy(SortField.ALBUM, ascending = false)
    val ALBUM_YEAR = orderBy(SortField.YEAR, ascending = false)
}

object SongSortOrder {
    val SONG_A_Z = orderBy(SortField.TITLE)
    val SONG_Z_A = orderBy(SortField.TITLE, ascending = false)
    val SONG_ARTIST = orderBy(SortField.ARTIST, ascending = false)
    val SONG_ALBUM = orderBy(SortField.ALBUM)
    val SONG_YEAR = orderBy(SortField.YEAR, ascending = false)
    val SONG_DURATION = orderBy(SortField.DURATION, ascending = false)
    val SONG_DATE_ADDED = orderBy(SortField.DATE_ADDED, ascending = false)
    val SONG_DATE_MODIFIED = orderBy(SortField.DATE_MODIFIED, ascending = false)
    val SONG_TRACK_LIST = orderBy(SortField.TRACK)
}