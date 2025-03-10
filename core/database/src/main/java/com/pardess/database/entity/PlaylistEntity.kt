package com.pardess.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")
    val playlistId: Long = 0,

    @ColumnInfo(name = "playlist_name")
    val playlistName: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(), // 생성 날짜

    @ColumnInfo(name = "pinned_at")
    val pinnedAt: Long? = null, // 고정 날짜

    @ColumnInfo(name = "playlist_cover")
    val playlistCover: String? = null, // 커버 이미지 URI 또는 Base64

    // 순서를 저장할 필드 (0부터 시작하는 순번)
    @ColumnInfo(name = "display_order")
    var displayOrder: Int = 0


)