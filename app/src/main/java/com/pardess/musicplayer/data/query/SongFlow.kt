package com.pardess.musicplayer.data.query

import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import com.pardess.musicplayer.utils.Constants.songProjection
import com.pardess.musicplayer.utils.IS_MUSIC
import com.pardess.musicplayer.data.SongSortOrder
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.utils.Utils.isOverR
import com.pardess.musicplayer.utils.queryFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration

class SongFlow(
    private val contentResolver: ContentResolver,
    private val sortOrder: String = SongSortOrder.SONG_A_Z
) : QueryFlow<Song>() {
    override fun flowData(): Flow<List<Song>> = flowCursor().map{
        mutableListOf<Song>().apply {
            it?.use { cursor ->
                val idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val trackNumberIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)
                val yearIndex = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
                val durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                val dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                val dateModifiedIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
                val albumIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                val albumNameIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                val artistIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
                val artistNameIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val composerIndex = cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER)
                val albumArtistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ARTIST)

                if (!cursor.moveToFirst()) {
                    return@use
                }

                while (!cursor.isAfterLast) {
                    val id = cursor.getLong(idIndex)
                    val title = cursor.getString(titleIndex)
                    val trackNumber = cursor.getInt(trackNumberIndex)
                    val year = cursor.getInt(yearIndex)
                    val duration = cursor.getLong(durationIndex)
                    val data = cursor.getString(dataIndex)
                    val dateModified = cursor.getLong(dateModifiedIndex)
                    val albumId = cursor.getLong(albumIdIndex)
                    val albumName = cursor.getString(albumNameIndex)
                    val artistId = cursor.getLong(artistIdIndex)
                    val artistName = cursor.getString(artistNameIndex)
                    val composer = cursor.getString(composerIndex)
                    val albumArtist = cursor.getString(albumArtistIndex)

                    add(
                        Song(
                            id = id,
                            title = title,
                            trackNumber = trackNumber,
                            year = year,
                            duration = Duration.ofSeconds(duration),
                            data = data,
                            dateModified = dateModified,
                            albumId = albumId,
                            albumName = albumName,
                            artistId = artistId,
                            artistName = artistName,
                            composer = composer,
                            albumArtist = albumArtist
                        )
                    )

                    cursor.moveToNext()
                }
            }
        }
    }

    override fun flowCursor(): Flow<Cursor?> {

        val uri =
            if (isOverR()) MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL) else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = songProjection

        val selection = IS_MUSIC

        val selectionValues = null

        return contentResolver.queryFlow(
            uri = uri,
            projection = projection,
            queryArgs = Bundle().apply {
                putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionValues)
                putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, sortOrder)
            }
        )
    }
}