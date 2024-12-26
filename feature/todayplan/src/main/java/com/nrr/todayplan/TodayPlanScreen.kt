package com.nrr.todayplan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nrr.designsystem.component.AdaptiveText
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.PastelGreen
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.theme.lightOrangeGradient
import com.nrr.todayplan.util.TodayPlanDictionary

@Composable
internal fun TodayPlanScreen(
    modifier: Modifier = Modifier,
    viewModel: TodayPlanViewModel = hiltViewModel()
) {
    val todayPlan by viewModel.todayPlan.collectAsStateWithLifecycle()

    Column(modifier = modifier) {

    }
}

@Composable
internal fun GreetingHeader(
    username: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val initialNameSize = 32

        Column(
            modifier = Modifier.weight(0.6f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdaptiveText(
                text = "${stringResource(TodayPlanDictionary.greeting)}, $username",
                initialFontSize = 24.sp,
                maxLines = 1
            )
            AdaptiveText(
                text = stringResource(TodayPlanDictionary.question),
                initialFontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                lineHeight = (initialNameSize + 2).sp
            )
        }
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(PastelGreen)
                .size((initialNameSize * 2).dp)
        ) {
            Text(
                text = username[0].uppercase(),
                modifier = Modifier.align(Alignment.Center),
                fontSize = initialNameSize.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
private fun GreetingHeaderPreview() {
    TaskifyTheme {
        GreetingHeader(
            username = "Kite1412",
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
        )
    }
}

@Composable
internal fun PlanForToday(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = lightOrangeGradient
                )
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(TaskifyIcon.calendar3d),
            contentDescription = "calendar illustration",
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = stringResource(TodayPlanDictionary.planForToday),
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun PlanForTodayPreview() {
    TaskifyTheme {
        PlanForToday {}
    }
}