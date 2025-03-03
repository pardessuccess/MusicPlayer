package com.pardess.musicplayer.data.mapper

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.pardess.musicplayer.data.entity.SearchHistoryEntity
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.model.SearchType
import com.pardess.musicplayer.domain.model.Song
import java.time.Duration


// MediaItem → Song 변환
@OptIn(UnstableApi::class)
fun MediaItem.toSong(): Song {
    // mediaMetadata.extras에 저장된 값으로 Song의 추가 정보를 복원
    val extras = mediaMetadata.extras
    return Song(
        id = extras?.getLong("id") ?: 0L,
        title = mediaMetadata.title?.toString() ?: "",
        trackNumber = extras?.getInt("trackNumber") ?: 0,
        year = extras?.getInt("year") ?: 0,
        duration = Duration.ofMillis((mediaMetadata.durationMs ?: 10000L) / 1000),
        data = localConfiguration?.uri?.toString() ?: "",
        dateModified = extras?.getLong("dateModified") ?: 0L,
        albumId = extras?.getLong("albumId") ?: 0L,
        albumName = mediaMetadata.albumTitle?.toString() ?: "",
        artistId = extras?.getLong("artistId") ?: 0L,
        artistName = mediaMetadata.artist?.toString() ?: "",
        composer = extras?.getString("composer"),
        albumArtist = extras?.getString("albumArtist"),
        favorite = extras?.getBoolean("favorite") ?: false
    )
}

fun SearchHistoryEntity.toSearchHistory(): SearchHistory {
    return SearchHistory(
        id = id,
        type = SearchType.entries.firstOrNull { it.num == type } ?: SearchType.TEXT,
        image = image,
        text = text,
        timestamp = timestamp
    )
}

fun SearchHistory.toEntity(): SearchHistoryEntity {
    return SearchHistoryEntity(
        id = id,
        type = type.num,
        image = image,
        text = text,
        timestamp = timestamp
    )
}