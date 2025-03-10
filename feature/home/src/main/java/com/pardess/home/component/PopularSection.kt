package com.pardess.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.designsystem.BackgroundColor
import com.pardess.designsystem.PointColor
import com.pardess.designsystem.TextColor
import com.pardess.home.MainUiEvent
import com.pardess.home.R
import com.pardess.model.Album
import com.pardess.model.Artist
import com.pardess.navigation.Screen
import com.pardess.ui.MusicImage


@Composable
fun PopularSection(
    onEvent: (MainUiEvent) -> Unit,
    artists: List<Artist>,
    albums: List<Album>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PointColor
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
            onClick = {

            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("인기 아티스트", fontWeight = FontWeight.Bold, fontSize = 40.sp, color = TextColor)
                Icon(
                    tint = TextColor,
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "ic_arrow_right",
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(artists) { artist ->
                ItemPopularArtist(
                    artist = artist,
                    onEvent = onEvent
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PointColor
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
            onClick = {

            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("인기 앨범", fontWeight = FontWeight.Bold, fontSize = 40.sp, color = TextColor)
                Icon(
                    tint = TextColor,
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "ic_arrow_right",
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(6.dp),
        ) {
            items(albums) { album ->
                ItemPopularAlbum(
                    album = album,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
fun ItemPopularAlbum(
    album: Album,
    onEvent: (MainUiEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .width(152.dp)
            .padding(end = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.elevatedCardElevation(5.dp),
            onClick = {
                onEvent(MainUiEvent.Navigate(Screen.DetailArtist.route + "/${album.artistId}/${album.id}"))
            }
        ) {
            MusicImage(
                filePath = album.songs.first().data,
                modifier = Modifier
                    .size(140.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
                type = "album"
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = album.title,
            fontSize = 20.sp,
            maxLines = 2,
            minLines = 2,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun ItemPopularArtist(
    artist: Artist,
    onEvent: (MainUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .width(146.dp)
            .padding(end = 6.dp)
            .clip(RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.elevatedCardElevation(5.dp),
            shape = CircleShape,
            onClick = {
                onEvent(MainUiEvent.Navigate(Screen.DetailArtist.route + "/${artist.id}"))
            }
        ) {
            MusicImage(
                filePath = artist.songs.first().data,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape),
                type = "artist"
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = artist.name,
            fontSize = 20.sp,
            maxLines = 2,
            minLines = 2,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
