package com.pardess.musicplayer.utils

import android.Manifest
import android.os.Build
import android.provider.MediaStore

object Constants {

    val MEDIA_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS,
        )
    } else {
        listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    val AUDIO_PERMISSIONS = listOf(
        Manifest.permission.RECORD_AUDIO,
    )

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

    val artistProjection = arrayOf(
        MediaStore.Audio.Artists._ID, // 0
        MediaStore.Audio.Artists.ARTIST, // 1
        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS, // 2
        MediaStore.Audio.Artists.NUMBER_OF_TRACKS, // 3
    )

}

const val IS_MUSIC =
    MediaStore.Audio.AudioColumns.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''"

const val DATA_STORE_USER_PREFERENCES = "user_preferences"
