package com.pardess.model

data class PlaylistSong(
    val songPrimaryKey: Long,
    val playlistCreatorId: Long,
    val song: Song
)