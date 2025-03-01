package com.pardess.musicplayer.presentation.playlist

import com.pardess.musicplayer.data.entity.PlaylistEntity
import com.pardess.musicplayer.data.entity.PlaylistSong

sealed class PlaylistUiEvent {
    data class ChangePlaylistOrder(val changedPlaylists: List<PlaylistEntity>) : PlaylistUiEvent()

    // CreatePlaylistDialog 관련 이벤트
    data class SetShowPlaylistDialog(val isShow: Boolean) : PlaylistUiEvent()

    // AddSongToPlaylistDialog 관련 이벤트
    data class SetShowAddSongDialog(val isShow: Boolean) : PlaylistUiEvent()

    data class SetShowDeletePlaylistDialog(val isShow: Boolean, val playlistEntity: PlaylistEntity? = null) : PlaylistUiEvent()

    data class CreatePlaylist(val playlistName: String) : PlaylistUiEvent()

    data class ToggleSongSelection(val song: PlaylistSong, val isSelected: Boolean) :
        PlaylistUiEvent()

    object DeletePlaylist : PlaylistUiEvent()

    object SavePlaylist : PlaylistUiEvent()

}