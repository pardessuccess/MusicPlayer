package com.pardess.musicplayer.presentation.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.Song
import com.pardess.musicplayer.presentation.base.BaseScreen
import com.pardess.musicplayer.presentation.component.MusicImage
import com.pardess.musicplayer.presentation.navigation.Screen
import com.pardess.musicplayer.ui.theme.BackgroundColor
import com.pardess.musicplayer.ui.theme.NavigationBarHeight
import com.pardess.musicplayer.ui.theme.PointColor
import com.pardess.musicplayer.ui.theme.TextColor
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun ArtistScreen(
    onNavigateToRoute: (String) -> Unit,
    upPress: () -> Unit,
    allSongs: List<Song>
) {
    val viewModel: ArtistViewModel = hiltViewModel()
    viewModel.onEvent(ArtistUiEvent.LoadArtists(allSongs))
    BaseScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is ArtistEffect.SelectArtist -> {
                    onNavigateToRoute(Screen.DetailArtist.route + "/${effect.artistId}")
                }
            }
        },
    ) { uiState, onEvent ->
        ArtistScreen(
            onEvent = onEvent,
            artists = uiState.artists
        )
    }
}

@Composable
private fun ArtistScreen(
    onEvent: (ArtistUiEvent) -> Unit,
    artists: List<Artist>
) {
    val state = rememberLazyListState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            LazyColumnScrollbar(
                state = state,
                settings = ScrollbarSettings.Default.copy(
                    thumbThickness = 20.dp,
                    enabled = artists.size > 20,
                    thumbUnselectedColor = PointColor
                )
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier,
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(artists) { artist ->
                        ArtistItem(
                            artist = artist,
                            onClickArtist = {
                                onEvent(ArtistUiEvent.SelectArtist(artist.id))
                            }
                        )
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier.height(
                NavigationBarHeight + WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding()
            )
        )
    }
}

@Composable
fun ArtistItem(
    artist: Artist,
    onClickArtist: () -> Unit
) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(5.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(PointColor)
                .clickable {
                    onClickArtist()
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
            ) {
                MusicImage(
                    filePath = artist.songs.first().data,
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f),
                    type = "artist"
                )
            }

            Text(
                text = artist.name,
                color = TextColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}