package com.pardess.musicplayer.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.musicplayer.R
import com.pardess.musicplayer.domain.model.SearchHistory
import com.pardess.musicplayer.domain.model.SearchType
import com.pardess.musicplayer.presentation.common.component.MusicImage
import com.pardess.musicplayer.presentation.home.HomeUiEvent
import com.pardess.musicplayer.presentation.main.MainUiEvent
import com.pardess.musicplayer.ui.theme.BackgroundColor

@Composable
fun SearchHistorySection(
    modifier: Modifier = Modifier,
    onEvent: (MainUiEvent) -> Unit,
    searchHistories: List<SearchHistory>,
    onHomeUiEvent: (HomeUiEvent) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = BackgroundColor
            ),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            items(searchHistories) { history ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clickable {
                            onHomeUiEvent(HomeUiEvent.SearchBoxShrink)
                            onEvent(MainUiEvent.Search(history.text))
                        }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (history.type) {
                        SearchType.TEXT -> {
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 8.dp, horizontal = 8.dp),
                                text = history.text,
                                fontSize = 28.sp,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                maxLines = 2,
                            )
                        }

                        else -> {
                            MusicImage(
                                filePath = history.image ?: "",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(80.dp),
                                type = history.type.text
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = history.text,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 30.sp,
                                    color = Color.Black,
                                    maxLines = 2,
                                )
                            }
                        }
                    }

                    IconButton(
                        modifier = Modifier.size(45.dp),
                        onClick = {
                            onEvent(MainUiEvent.RemoveSearchHistory(history.id))
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(45.dp),
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "ic_close",
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.5.dp)
                        .padding(horizontal = 6.dp)
                        .background(Color.Gray.copy(0.2f))
                )
            }
        }
    }
}