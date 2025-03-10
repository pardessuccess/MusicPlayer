package com.pardess.navigation

import androidx.annotation.DrawableRes

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


enum class HomeScreen(
    val title: String,
    @DrawableRes val icon: Int,
    val route: String,
) {
    Main("홈", R.drawable.ic_home_tab, "main_screen"),
    Playlist("목록", R.drawable.ic_playlist_tab, "playlist_screen"),
    Artist("가수", R.drawable.ic_artist_tab, "artist_screen"),
    Songs("노래", R.drawable.ic_songs_tab, "songs_screen"),
}