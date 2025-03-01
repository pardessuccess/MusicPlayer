package com.pardess.musicplayer.presentation.playlist

import com.pardess.musicplayer.data.entity.PlaylistEntity
import com.pardess.musicplayer.data.entity.PlaylistSong

data class PlaylistUiState(
    val selectedPlaylist: PlaylistEntity? = null,
    val playlists: List<PlaylistEntity> = emptyList(),
    val dialogState: PlaylistDialogState = PlaylistDialogState()
)

data class PlaylistDialogState(
    val isShowCreatePlaylistDialog: Boolean = false,
    val isShowAddSongDialog: Boolean = false,
    val isShowDeletePlaylistDialog: Boolean = false,
    val selectedSongs: List<PlaylistSong> = emptyList()
)