package com.pardess.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.model.PlaylistSong
import com.pardess.model.Song
import com.pardess.model.join.FavoriteSong
import com.pardess.model.join.PlayCountSong

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MusicImage(
            song.data,
            Modifier
                .size(80.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(text = song.title, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(text = song.artistName, fontSize = 12.sp, color = Color.Gray)
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CheckSongItem(
    modifier: Modifier = Modifier,
    checked: Boolean,
    playlistSong: PlaylistSong,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (checked) {
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .padding(end = 8.dp)
                    .aspectRatio(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = Color(0xFF40C4FF),
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                )
            }
        } else {
            MusicImage(
                playlistSong.song.data,
                Modifier
                    .padding(end = 8.dp)
                    .size(80.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(text = playlistSong.song.title, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(text = playlistSong.song.artistName, fontSize = 12.sp, color = Color.Gray)
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteSongItem(
    modifier: Modifier = Modifier,
    favoriteSong: FavoriteSong,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MusicImage(
            favoriteSong.song.data,
            Modifier
                .padding(end = 8.dp)
                .size(80.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = favoriteSong.song.title, fontWeight = FontWeight.Bold, maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = favoriteSong.song.artistName, fontSize = 12.sp, color = Color.Gray)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                tint = Color.Red,
                painter = painterResource(R.drawable.ic_favorite),
                contentDescription = null
            )
            Text(
                text = favoriteSong.favoriteCount.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayCountSongItem(
    modifier: Modifier = Modifier,
    playCountSong: PlayCountSong,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MusicImage(
            playCountSong.song.data,
            Modifier
                .padding(end = 8.dp)
                .size(80.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = playCountSong.song.title,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = playCountSong.song.artistName, fontSize = 12.sp, color = Color.Gray)
        }
        Text(
            text = playCountSong.playCount.toString() + " 회",
            fontSize = 20.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
}

