package com.pardess.root

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
import com.pardess.designsystem.PointBackgroundColor
import com.pardess.designsystem.PointColor
import com.pardess.navigation.HomeScreen
import com.pardess.ui.AutoSizeText

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun MusicBottomNavigationBar(
    onEvent: (RootUiEvent) -> Unit,
    uiState: RootUiState,
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
                    onEvent(RootUiEvent.BottomBarSelect(tabs.indexOf(section)))
                    onEvent(RootUiEvent.NavigateBottomBar(section.route))
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
