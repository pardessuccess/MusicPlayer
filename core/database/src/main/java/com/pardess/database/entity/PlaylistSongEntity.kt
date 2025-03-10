package com.pardess.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_song_entity",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlist_id"],
            childColumns = ["playlist_creator_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["playlist_creator_id"])]
)
data class PlaylistSongEntity(
    @PrimaryKey(autoGenerate = true)
    val songPrimaryKey: Long = 0L,

    @ColumnInfo(name = "playlist_creator_id")
    val playlistCreatorId: Long, // ✅ 부모 PlaylistEntity의 ID

    @Embedded // ✅ 중복된 SongEntity 필드를 직접 포함
    val song: SongEntity
)