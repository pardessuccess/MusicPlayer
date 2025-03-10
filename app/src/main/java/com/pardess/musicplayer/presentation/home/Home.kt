package com.pardess.musicplayer.presentation.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pardess.musicplayer.R
import com.pardess.musicplayer.presentation.common.component.AutoSizeText
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
    onEvent: (HomeUiEvent) -> Unit,
    uiState: HomeUiState,
    bottomBarHeight: Dp
) {

    val tabs = HomeScreen.entries.toList()

    NavigationBar(
        containerColor = PointColor,
        modifier = Modifier
            .background(PointColor)
            .height(bottomBarHeight)
    ) {
        tabs.forEach { section ->
            NavigationBarItem(
                modifier = Modifier.weight(1f),
                icon = section.icon,
                title = section.title,
                isSelected = tabs.indexOf(section) == uiState.selectedBottomIndex,
                onClick = {
                    onEvent(HomeUiEvent.BottomBarSelect(tabs.indexOf(section)))
                    onEvent(HomeUiEvent.NavigateBottomBar(section.route))
                },
                bottomBarHeight = bottomBarHeight,
            )
        }
    }
}

@Composable
fun NavigationBarItem(
    modifier: Modifier,
    icon: Int,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    bottomBarHeight: Dp
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .background(if (isSelected) PointBackgroundColor else PointColor)
            .height(120.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            tint = if (isSelected) PointColor else PointBackgroundColor,
            painter = painterResource(id = icon),
            contentDescription = "navigation icon $icon",
        )
        if (bottomBarHeight == 120.dp) {
            AutoSizeText(
                text = title,
                color = if (isSelected) PointColor else PointBackgroundColor,
                fontWeight = FontWeight.Bold,
            )
        } else {
            Spacer(Modifier.weight(1f))
        }
    }
}
