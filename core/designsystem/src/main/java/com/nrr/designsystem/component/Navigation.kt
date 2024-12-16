package com.nrr.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.R
import com.nrr.designsystem.theme.CharcoalClay
import com.nrr.designsystem.theme.TaskifyTheme

data class NavigationData(
    val id: Int,
    val label: String,
    val color: Color = Color.Black,
    val selectedColor: Color = Color.White,
    val indicatorColor: Color = CharcoalClay,
    val height: Dp = 40.dp,
    val width: Dp = 40.dp,
    val showLabel: Boolean = false
)

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}

@Preview
@Composable
private fun BottomNavigationBarPreview() {
    val icons = listOf(
        NavigationData(R.drawable.home, "Home"),
        NavigationData(R.drawable.note, "Tasks"),
        NavigationData(R.drawable.chart, "Analytics"),
        NavigationData(R.drawable.profile, "Profile"),
    )
    TaskifyTheme {
        BottomNavigationBar {
            icons.forEach {
                NavigationItem(it,true)
            }
        }
    }
}

@Composable
fun NavigationItem(
    data: NavigationData,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(if (data.showLabel) 8.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(data.id),
            contentDescription = data.label,
            modifier = Modifier
                .size(data.height, data.width)
                .background(if (selected) data.indicatorColor else Color.Transparent),
            tint = if (selected) data.selectedColor else data.color
        )
        if (data.showLabel) Text(
            text = data.label,
            color = if (selected) data.selectedColor else data.color
        )
    }
}