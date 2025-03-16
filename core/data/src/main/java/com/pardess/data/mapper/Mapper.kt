package com.pardess.data.mapper

import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.pardess.database.entity.PlaylistEntity
import com.pardess.database.entity.PlaylistSongEntity
import com.pardess.database.entity.SearchHistoryEntity
import com.pardess.database.entity.SongEntity
import com.pardess.database.entity.join.FavoriteSongDto
import com.pardess.database.entity.join.HistorySongDto
import com.pardess.database.entity.join.PlayCountSongDto
import com.pardess.database.entity.join.PlaylistSongsDto
import com.pardess.model.Playlist
import com.pardess.model.PlaylistSong
import com.pardess.model.SearchHistory
import com.pardess.model.SearchType
import com.pardess.model.Song
import com.pardess.model.join.FavoriteSong
import com.pardess.model.join.HistorySong
import com.pardess.model.join.PlayCountSong
import com.pardess.model.join.PlaylistSongs
import java.time.Duration


// MediaItem → Song 변환
@androidx.annotation.OptIn(UnstableApi::class)
fun MediaItem.toSong(): Song {
    // mediaMetadata.extras에 저장된 값으로 Song의 추가 정보를 복원
    val extras = mediaMetadata.extras
    return Song(
        id = extras?.getLong("id") ?: 0L,
        title = mediaMetadata.title?.toString() ?: "",
        trackNumber = extras?.getInt("trackNumber") ?: 0,
        year = extras?.getInt("year") ?: 0,
        duration = Duration.ofMillis((mediaMetadata.durationMs ?: 10000L)),
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

fun Song.toEntity(): SongEntity {
    return SongEntity(
        id = this.id,
        title = this.title,
        year = this.year,
        duration = this.duration.toMillis(),
        data = this.data,
        albumId = this.albumId,
        albumName = this.albumName,
        artistId = this.artistId,
        artistName = this.artistName
    )
}

fun SongEntity.toDomain(): Song {
    return Song(
        id = this.id,
        title = this.title,
        trackNumber = -1, // SongEntity에는 없으므로 기본값 설정
        year = this.year,
        duration = Duration.ofMillis(this.duration),
        data = this.data,
        dateModified = System.currentTimeMillis(), // 수정 시간이 없으므로 현재 시간
        albumId = this.albumId,
        albumName = this.albumName,
        artistId = this.artistId,
        artistName = this.artistName,
        composer = null, // SongEntity에는 없으므로 기본값 설정
        albumArtist = null, // SongEntity에는 없으므로 기본값 설정
        favorite = false // SongEntity에는 없으므로 기본값 설정
    )
}

fun HistorySongDto.toDomain(): HistorySong {
    return HistorySong(
        song = this.song.toDomain(),
        lastPlayed = this.lastPlayed
    )
}

// 📌 PlayCountSongDto → PlayCountSong 변환
fun PlayCountSongDto.toDomain(): PlayCountSong {
    return PlayCountSong(
        song = this.song.toDomain(),
        playCount = this.playCount
    )
}

// 🎼 Playlist → PlaylistEntity 변환
fun Playlist.toEntity(): PlaylistEntity {
    return PlaylistEntity(
        playlistId = this.playlistId,
        playlistName = this.playlistName,
        createdAt = this.createdAt,
        pinnedAt = this.pinnedAt,
        playlistCover = this.playlistCover,
        displayOrder = this.displayOrder
    )
}

// 🎼 PlaylistEntity → Playlist 변환
fun PlaylistEntity.toDomain(): Playlist {
    return Playlist(
        playlistId = this.playlistId,
        playlistName = this.playlistName,
        createdAt = this.createdAt,
        pinnedAt = this.pinnedAt,
        playlistCover = this.playlistCover,
        displayOrder = this.displayOrder
    )
}

// 🎼 PlaylistSong → PlaylistSong 변환
fun PlaylistSong.toDomain(): PlaylistSong {
    return PlaylistSong(
        songPrimaryKey = this.songPrimaryKey,
        playlistCreatorId = this.playlistCreatorId,
        song = this.song
    )
}

// 🎼 PlaylistSong → PlaylistSongEntity 변환
fun PlaylistSong.toEntity(): PlaylistSongEntity {
    return PlaylistSongEntity(
        songPrimaryKey = this.songPrimaryKey,
        playlistCreatorId = this.playlistCreatorId,
        song = song.toEntity(),
    )
}

// 🎼 PlaylistSongsDto → PlaylistSongs 변환
fun PlaylistSongsDto.toDomain(): PlaylistSongs {
    return PlaylistSongs(
        playlist = this.playlist.toDomain(),
        songs = this.songs.map {
            PlaylistSong(
                songPrimaryKey = it.songPrimaryKey,
                playlistCreatorId = it.playlistCreatorId,
                song = it.song.toDomain()
            )
        }
    )
}


fun FavoriteSongDto.toDomain(): FavoriteSong {
    return FavoriteSong(
        song = this.song.toDomain(),
        favoriteCount = this.favoriteCount
    )
}