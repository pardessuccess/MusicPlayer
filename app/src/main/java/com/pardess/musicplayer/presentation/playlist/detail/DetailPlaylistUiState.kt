package com.pardess.musicplayer.presentation.playlist.detail

import com.pardess.musicplayer.data.entity.PlaylistEntity
import com.pardess.musicplayer.data.entity.PlaylistSong

data class DetailPlaylistUiState(
    val playlist: PlaylistEntity? = null,
    val playlistSongs: List<PlaylistSong> = emptyList(),
    val deleteSong: PlaylistSong? = null,
    val deleteMode: Boolean = false,
    val selectedSongs: List<PlaylistSong> = emptyList(),
    val dialogState: DetailPlaylistDialogState = DetailPlaylistDialogState()
)

data class DetailPlaylistDialogState(
    val isShowAddSongDialog: Boolean = false,
    val isShowDeleteSongDialog: Boolean = false,
)