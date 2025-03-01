package com.pardess.musicplayer.presentation.artist.detail.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pardess.musicplayer.ui.theme.BackgroundColor
import com.pardess.musicplayer.ui.theme.PointColor
import com.pardess.musicplayer.ui.theme.PointColor3
import kotlinx.coroutines.launch

@Composable
fun HorizontalPagerIndicator(
    pagerState: PagerState,
    pageCount: Int,
    pageTitle: List<String> = listOf(),
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // 현재 페이지를 기반으로 애니메이션 적용
    val animatedOffset by animateDpAsState(
        targetValue = (pagerState.currentPage * 160).dp, // ✅ 페이지 변경 시 X축 이동 (패딩 고려)
        animationSpec = tween(durationMillis = 300), // ✅ 300ms 애니메이션 효과
        label = "PagerIndicatorAnimation"
    )

    Box(
        modifier = modifier
            .background(PointColor3, shape = RoundedCornerShape(35))
            .height(80.dp)
            .padding(4.dp)
            .width((160 * pageCount).dp) // 전체 길이 설정
    ) {
        // ✅ 선택된 페이지 강조 효과 (애니메이션 적용)
        Box(
            modifier = Modifier
                .offset(x = animatedOffset)
                .width(160.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(35))
                .background(PointColor)
        )

        // ✅ 페이지 버튼들
        Row(
            modifier = Modifier.fillMaxSize(), // ✅ 클릭 이벤트가 전체 박스에서 가능하도록 수정
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(pageCount) { index ->
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(90.dp)
                        .clip(RoundedCornerShape(35))
                        .clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pageTitle[index],
                        color = if (pagerState.currentPage == index) Color.White else Color.Black,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
