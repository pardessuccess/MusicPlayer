package com.pardess.media_query.utils

import android.provider.MediaStore

val songProjection = arrayOf(
    MediaStore.Audio.AudioColumns._ID, // 0
    MediaStore.Audio.AudioColumns.TITLE, // 1
    MediaStore.Audio.AudioColumns.TRACK, // 2
    MediaStore.Audio.AudioColumns.YEAR, // 3
    MediaStore.Audio.AudioColumns.DURATION, // 4
    MediaStore.Audio.AudioColumns.DATA, // 5
    MediaStore.Audio.AudioColumns.DATE_MODIFIED, // 6
    MediaStore.Audio.AudioColumns.ALBUM_ID, // 7
    MediaStore.Audio.AudioColumns.ALBUM, // 8
    MediaStore.Audio.AudioColumns.ARTIST_ID, // 9
    MediaStore.Audio.AudioColumns.ARTIST, // 10
    MediaStore.Audio.AudioColumns.COMPOSER, // 11
    MediaStore.Audio.AudioColumns.ALBUM_ARTIST, // 12
)