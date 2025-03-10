package com.pardess.model.join

import com.pardess.model.Playlist
import com.pardess.model.PlaylistSong
import com.pardess.model.Song

data class PlaylistSongs(
    val playlist: Playlist,
    val songs: List<PlaylistSong>
)
