package com.pardess.musicplayer.presentation

import com.pardess.musicplayer.data.entity.PlaylistSong
import com.pardess.musicplayer.data.entity.SongEntity
import com.pardess.musicplayer.domain.model.Song
import java.time.Duration

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

fun SongEntity.toSong(): Song {
    return Song(
        id = this.id,
        title = this.title,
        trackNumber = -1, // SongEntity에는 없으므로 기본값 설정
        year = this.year,
        duration = Duration.ofSeconds(this.duration),
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

fun SongEntity.toPlaylistSong(playlistCreatorId: Long): PlaylistSong {
    return PlaylistSong(
        songPrimaryKey = 0L, // autoGenerate이므로 기본값 설정
        playlistCreatorId = playlistCreatorId, // 해당 플레이리스트 ID 지정
        song = this // SongEntity 자체를 포함
    )
}

fun PlaylistSong.toSongEntity(): SongEntity {
    return this.song // `@Embedded`된 `SongEntity` 반환
}

fun Song.toPlaylistSong(playlistCreatorId: Long): PlaylistSong {
    return PlaylistSong(
        songPrimaryKey = 0L, // Room의 자동 생성 ID
        playlistCreatorId = playlistCreatorId,
        song = this.toEntity() // `Song`을 `SongEntity`로 변환 후 포함
    )
}

fun PlaylistSong.toSong(): Song {
    return this.song.toSong() // `SongEntity`를 `Song`으로 변환
}
