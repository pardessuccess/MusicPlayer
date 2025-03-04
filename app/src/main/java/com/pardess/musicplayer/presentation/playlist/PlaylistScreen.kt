package com.pardess.musicplayer.presentation.playlist

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pardess.musicplayer.R
import com.pardess.musicplayer.data.entity.PlaylistEntity
import com.pardess.musicplayer.presentation.base.BaseScreen
import com.pardess.musicplayer.presentation.navigation.Screen
import com.pardess.musicplayer.presentation.playlist.component.DragAndDropListState
import com.pardess.musicplayer.presentation.playlist.component.move
import com.pardess.musicplayer.presentation.playlist.component.rememberDragAndDropListState
import com.pardess.musicplayer.presentation.playlist.dialog.CreatePlaylistDialog
import com.pardess.musicplayer.ui.theme.BackgroundColor
import com.pardess.musicplayer.ui.theme.Gray300
import com.pardess.musicplayer.ui.theme.NavigationBarHeight
import com.pardess.musicplayer.ui.theme.PointColor
import com.pardess.musicplayer.ui.theme.TextColor


@Composable
fun PlaylistScreen(
    onNavigateToRoute: (String) -> Unit,
) {
    val viewModel = hiltViewModel<PlaylistViewModel>()
    val context = LocalContext.current
    BaseScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is PlaylistUiEffect.Navigate -> {
                    onNavigateToRoute(effect.route)
                }

                is PlaylistUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        },
    ) { uiState, onEvent ->
        PlaylistScreen(
            uiState = uiState,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun PlaylistScreen(
    uiState: PlaylistUiState,
    onEvent: (PlaylistUiEvent) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val dragAndDropListState = rememberDragAndDropListState(lazyListState) { from, to ->
        if (to in 0 until uiState.playlists.size) {
            onEvent(PlaylistUiEvent.ChangePlaylistOrder(uiState.playlists.toMutableList().apply {
                move(from, to)
            }))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .dragContainer(dragAndDropListState = dragAndDropListState),
                state = dragAndDropListState.lazyListState,
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                itemsIndexed(uiState.playlists) { index, playlist ->
                    val checked = uiState.selectedPlaylistIds.contains(playlist.playlistId)
                    DraggableItem(
                        dragAndDropListState = dragAndDropListState,
                        index = index
                    ) { modifier ->
                        PlaylistItem(
                            modifier = modifier,
                            playlist = playlist,
                            onClick = {
                                if (uiState.deleteMode) {
                                    onEvent(
                                        PlaylistUiEvent.TogglePlaylistSelection(
                                            playlist,
                                            !checked
                                        )
                                    )
                                } else {
                                    onEvent(PlaylistUiEvent.Navigate(Screen.DetailPlaylist.route + "/${playlist.playlistId}"))
                                }
                            },
                            checked = checked
                        )
                    }
                }
                item {
                    PlaylistActions(
                        deleteMode = uiState.deleteMode,
                        onDeleteToggle = { onEvent(PlaylistUiEvent.ToggleDeleteMode) },
                        onDeleteConfirm = { onEvent(PlaylistUiEvent.DeletePlaylists) },
                        onCreatePlaylist = { onEvent(PlaylistUiEvent.SetShowPlaylistDialog(true)) }
                    )
                }
            }
            if (uiState.isShowCreateDialog) {
                CreatePlaylistDialog(onEvent = onEvent)
            }
            Spacer(
                modifier = Modifier.height(
                    NavigationBarHeight + WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding()
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    checked: Boolean,
    playlist: PlaylistEntity,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                shape = RoundedCornerShape(20.dp),
                color = if (checked) Gray300 else PointColor
            )
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = onClick,
            )
            .padding(horizontal = 10.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (checked) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = PointColor,
                    modifier = Modifier
                        .size(100.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                modifier = Modifier
                    .weight(1f)
                    .basicMarquee(1),
                text = playlist.playlistName,
                fontSize = 36.sp,
                maxLines = 1,
                color = TextColor,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp,
            )
            Icon(
                modifier = Modifier.size(70.dp),
                painter = painterResource(R.drawable.ic_swap),
                contentDescription = null,
                tint = TextColor
            )
        }
    }
}

@Composable
fun PlaylistActions(
    deleteMode: Boolean,
    onDeleteToggle: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onCreatePlaylist: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clickable(onClick = onDeleteToggle)
                .weight(1f)
        ) {
            Icon(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center),
                painter = painterResource(R.drawable.ic_remove),
                contentDescription = null
            )
        }
        Box(
            modifier = Modifier
                .clickable {
                    if (deleteMode) onDeleteConfirm() else onCreatePlaylist()
                }
                .weight(1f)
        ) {
            Icon(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center),
                painter = painterResource(if (deleteMode) R.drawable.ic_check else R.drawable.ic_add),
                contentDescription = null
            )
        }
    }
}

@Composable
fun Header() {

}



// 드래그 제스처를 감지할 Modifier
fun Modifier.dragContainer(
    dragAndDropListState: DragAndDropListState,
): Modifier {
    return this.pointerInput(Unit) {
        detectDragGesturesAfterLongPress(
            onDrag = { change, offset ->
                change.consume()
                dragAndDropListState.onDrag(offset)
            },
            onDragStart = { offset ->
                dragAndDropListState.onDragStart(offset)
            },
            onDragEnd = { dragAndDropListState.onDragInterrupted() },
            onDragCancel = { dragAndDropListState.onDragInterrupted() }
        )
    }
}

// 드래그 중 항목에 적용할 Modifier
@Composable
fun LazyItemScope.DraggableItem(
    dragAndDropListState: DragAndDropListState,
    index: Int,
    content: @Composable LazyItemScope.(Modifier) -> Unit
) {
    val draggingModifier = Modifier.composed {
        val offsetOrNull = dragAndDropListState.elementDisplacement.takeIf {
            index == dragAndDropListState.currentIndexOfDraggedItem
        }
        Modifier.graphicsLayer {
            translationY = offsetOrNull ?: 0f
        }
    }
    content(draggingModifier)
}