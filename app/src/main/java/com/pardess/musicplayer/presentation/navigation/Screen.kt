package com.pardess.musicplayer.presentation.navigation

enum class Screen(
    val title: String,
    val route: String,
) {
    DetailArtist("DetailArtistScreen", "artist_screen"),
    DetailPlaylist("DetailPlaylistScreen", "detail_playlist_screen"),
    Search("SearchScreen", "search_screen"),
    Favorite("FavoriteScreen", "favorite_screen"),
    History("HistoryScreen", "history_screen"),
    PlayCount("PlayCountScreen", "play_count_screen"),
}

enum class Navigation(
    val route: String,
) {
    Home("home_route"),
    Main("main_route"),
    Playlist("playlist_route"),
    Artist("artist_route"),
    Songs("songs_route"),
}