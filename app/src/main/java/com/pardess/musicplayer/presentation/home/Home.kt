package com.pardess.musicplayer.presentation.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.musicplayer.R
import com.pardess.musicplayer.presentation.component.AutoResizeText
import com.pardess.musicplayer.presentation.component.FontSizeRange
import com.pardess.musicplayer.ui.theme.PointBackgroundColor
import com.pardess.musicplayer.ui.theme.PointColor

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

@Composable
fun MusicBottomNavigationBar(
    modifier: Modifier = Modifier,
    tabs: List<HomeScreen>,
    currentRoute: String,
    navigateToBottomBarRoute: (String) -> Unit,
) {

    val routes = remember { tabs.map { it.route } }
    val currentSection = tabs.find { it.route == currentRoute }
        ?: remember { tabs.first() }
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    when (currentRoute) {
        HomeScreen.Main.route -> selectedItem = 0
        HomeScreen.Playlist.route -> selectedItem = 1
        HomeScreen.Artist.route -> selectedItem = 2
        HomeScreen.Songs.route -> selectedItem = 3
    }
    NavigationBar(
        containerColor = PointColor,
        modifier = modifier.background(PointColor)
    ) {
        tabs.forEach { section ->
            NavigationBarItem(
                modifier = Modifier.weight(1f),
                icon = section.icon,
                title = section.title,
                isSelected = tabs.indexOf(section) == selectedItem,
                onClick = {
                    selectedItem = tabs.indexOf(section)
                    navigateToBottomBarRoute(section.route)
                }
            )
        }
    }
}

@Composable
fun NavigationBarItem(
    modifier: Modifier,
    icon: Int, title: String, isSelected: Boolean, onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .background(if (isSelected) PointBackgroundColor else PointColor)
            .height(100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            tint = if (isSelected) PointColor else PointBackgroundColor,
            painter = painterResource(id = icon),
            contentDescription = "navigation icon $icon",
        )
        AutoResizeText(
            text = title,
            fontSizeRange = FontSizeRange(30.sp, 160.sp),
            color = if (isSelected) PointColor else PointBackgroundColor,
            fontWeight = FontWeight.Bold,
        )
    }
}
