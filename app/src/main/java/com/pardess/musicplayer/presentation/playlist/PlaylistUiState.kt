package com.pardess.musicplayer.presentation.playlist

import com.pardess.musicplayer.data.entity.PlaylistEntity

data class PlaylistUiState(
    val selectedPlaylist: PlaylistEntity? = null,
    val playlists: List<PlaylistEntity> = emptyList(),
    val selectedPlaylistIds: List<Long> = emptyList(),
    val dialogState: PlaylistDialogState = PlaylistDialogState(),
    val deleteMode: Boolean = false,
)

data class PlaylistDialogState(
    val isShowCreatePlaylistDialog: Boolean = false,
    val isShowDeletePlaylistDialog: Boolean = false,
)