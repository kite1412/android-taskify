package com.nrr.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nrr.designsystem.component.AdaptiveText
import com.nrr.designsystem.component.AppLogo
import com.nrr.designsystem.component.TextField
import com.nrr.designsystem.component.TextFieldWithOptions
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.registration.model.FieldData
import com.nrr.registration.util.RegistrationDictionary
import kotlin.math.roundToInt

@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    Content(
        fieldData = viewModel.fieldData,
        modifier = modifier
    )
}

@Composable
private fun Content(
    fieldData: List<FieldData>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = 64.dp,
                start = 32.dp,
                bottom = 32.dp,
                end = 32.dp
            ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AppLogo(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(12.dp)
        )
        HorizontalPager(
            state = rememberPagerState { 3 }
        ) {
            Field(
                data = fieldData[it],
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun Field(
    data: FieldData,
    modifier: Modifier = Modifier
) {
    var initialFontSize by remember { mutableIntStateOf(24) }
    val shadowOffset by remember {
        derivedStateOf {
            initialFontSize / 5
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(if (data.mandatory) 0.dp else 8.dp)
    ) {
        AdaptiveText(
            text = stringResource(data.stringId),
            initialFontSize = initialFontSize.sp,
            maxLines = 2,
            fontWeight = FontWeight.Bold,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = MaterialTheme.colorScheme.primary,
                    offset = Offset(x = -(shadowOffset).toFloat(), y = shadowOffset.toFloat())
                )
            ),
            onSizeChange = {
                initialFontSize = it.value.roundToInt()
            },
            lineHeight = (initialFontSize + 8).sp
        )
        if (data.options.size == 1) TextField(
            value = data.options[0],
            onValueChange = data.onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = data.placeholder ?: "",
                    color = Color.Black.copy(alpha = 0.7f),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            }
        ) else TextFieldWithOptions(
            options = data.options,
            onValueChange = data.onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        if (!data.mandatory) Text(
            text = stringResource(RegistrationDictionary.changeLater),
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = Color.Black.copy(alpha = 0.7f)
        )
    }
}

@Preview
@Composable
private fun ContentPreview() {
    TaskifyTheme {
        Content(
            fieldData = FieldData.fieldData({}, {}, {})
        )
    }
}