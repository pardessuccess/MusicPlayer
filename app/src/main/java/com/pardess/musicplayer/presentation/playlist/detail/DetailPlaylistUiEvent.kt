package com.pardess.musicplayer.presentation.playlist.detail

import com.pardess.musicplayer.data.entity.PlaylistSong

sealed class DetailPlaylistUiEvent {

    object DeleteSongFromPlaylist : DetailPlaylistUiEvent()

    data class SetShowAddSongDialog(val isShow: Boolean) : DetailPlaylistUiEvent()
    data class SetShowDeleteSongDialog(val isShow: Boolean, val deleteSong: PlaylistSong? = null) :
        DetailPlaylistUiEvent()

    data class ToggleSongSelection(val playlistSong: PlaylistSong, val isSelected: Boolean) :
        DetailPlaylistUiEvent()

    object SaveSongToPlaylist : DetailPlaylistUiEvent()

    object ToggleDeleteMode : DetailPlaylistUiEvent()

    object DeleteSelectedSongs : DetailPlaylistUiEvent()

}