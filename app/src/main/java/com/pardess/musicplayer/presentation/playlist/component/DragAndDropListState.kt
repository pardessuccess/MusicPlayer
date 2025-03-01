package com.pardess.musicplayer.presentation.playlist.component

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.channels.Channel

// MutableList 확장 함수로 항목 이동
fun <T> MutableList<T>.move(from: Int, to: Int) {
    if (from == to) return
    val element = this.removeAt(from)
    this.add(to, element)
}

// 드래그 앤 드랍 상태 기억 및 스크롤 이벤트 처리
@Composable
fun rememberDragAndDropListState(
    lazyListState: LazyListState,
    onMove: (Int, Int) -> Unit
): DragAndDropListState {
    val state = remember { DragAndDropListState(lazyListState, onMove) }
    LaunchedEffect(state) {
        while (true) {
            val diff = state.scrollChannel.receive()
            lazyListState.scrollBy(diff)
        }
    }
    return state
}

// 드래그 앤 드랍 상태 클래스: 드래그 시작, 이동, 인터럽트 처리
class DragAndDropListState(
    val lazyListState: LazyListState,
    private val onMove: (Int, Int) -> Unit
) {
    // 스크롤 이벤트를 전송할 채널
    val scrollChannel = Channel<Float>()

    // 현재 드래그 중인 항목의 인덱스
    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
        private set

    // 드래그 시작 시의 항목 정보
    private var initialDraggingElement by mutableStateOf<LazyListItemInfo?>(null)

    // 드래그 동안의 누적 이동 거리
    private var draggingDistance by mutableFloatStateOf(0f)

    // 현재 드래그 중인 항목의 오프셋 계산 (원래 위치 대비 이동량)
    val elementDisplacement: Float?
        get() = currentIndexOfDraggedItem
            ?.let { index ->
                lazyListState.layoutInfo.visibleItemsInfo.getOrNull(index - lazyListState.firstVisibleItemIndex)
                    ?.let { itemInfo ->
                        (initialDraggingElement?.offset ?: 0f).toFloat() + draggingDistance - itemInfo.offset
                    }
            }

    // 드래그 시작 시 호출: 해당 위치의 항목 정보를 저장
    fun onDragStart(offset: Offset) {
        lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { item ->
            offset.y.toInt() in item.offset until (item.offset + item.size)
        }?.let {
            initialDraggingElement = it
            currentIndexOfDraggedItem = it.index
        }
    }

    // 드래그 중 호출: 이동 거리에 따라 대상 항목을 찾고 onMove 콜백 호출
    fun onDrag(offset: Offset) {
        draggingDistance += offset.y

        initialDraggingElement?.let { initial ->
            val startOffset = initial.offset + draggingDistance
            val endOffset = initial.offset + initial.size + draggingDistance
            val middleOffset = (startOffset + endOffset) / 2f

            lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { item ->
                middleOffset.toInt() in item.offset until (item.offset + item.size) &&
                        item.index != currentIndexOfDraggedItem
            }?.let { targetItem ->
                currentIndexOfDraggedItem?.let { currentIndex ->
                    onMove(currentIndex, targetItem.index)
                }
                currentIndexOfDraggedItem = targetItem.index
            } ?: checkOverscroll(initial)
        }
    }

    // 드래그가 종료되거나 취소될 때 상태 초기화
    fun onDragInterrupted() {
        initialDraggingElement = null
        currentIndexOfDraggedItem = null
        draggingDistance = 0f
    }

    // 스크롤 영역 밖으로 드래그 시 오버스크롤 처리
    private fun checkOverscroll(initial: LazyListItemInfo) {
        val startOffset = initial.offset + draggingDistance
        val endOffset = initial.offset + initial.size + draggingDistance
        val overscroll = when {
            draggingDistance > 0 -> {
                (endOffset - lazyListState.layoutInfo.viewportEndOffset).takeIf { it > 0 }
            }
            draggingDistance < 0 -> {
                (startOffset - lazyListState.layoutInfo.viewportStartOffset).takeIf { it < 0 }
            }
            else -> null
        } ?: 0f
        if (overscroll != 0f) {
            scrollChannel.trySend(overscroll)
        }
    }
}
