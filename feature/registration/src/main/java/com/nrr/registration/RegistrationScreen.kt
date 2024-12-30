package com.nrr.registration

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nrr.designsystem.component.AdaptiveText
import com.nrr.designsystem.component.AppLogo
import com.nrr.designsystem.component.TextField
import com.nrr.designsystem.component.TextFieldDefaults
import com.nrr.designsystem.component.TextFieldWithOptions
import com.nrr.designsystem.component.TextFieldWithOptionsDefaults
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.CharcoalClay
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.theme.softBeigeGradient
import com.nrr.registration.model.FieldAction
import com.nrr.registration.model.FieldData
import com.nrr.registration.util.RegistrationDictionary
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val fieldData = FieldData.fieldData(
        username = viewModel.username,
        onUsernameChange = viewModel::setUserName,
        onLanguageChange = viewModel::setLanguageConfig,
        onThemeChange = viewModel::setThemeConfig
    )
    val pagerState = rememberPagerState { fieldData.size }
    val scope = rememberCoroutineScope()

    Content(
        fieldData = fieldData,
        onAction = {
            scope.launch {
                when (it) {
                    FieldAction.Next -> pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    FieldAction.Previous -> pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    FieldAction.Complete -> viewModel.register()
                }
            }
        },
        modifier = modifier,
        pagerState = pagerState,
        enableNext = viewModel.username.isNotEmpty()
    )
}

// TODO make responsive
@Composable
private fun Content(
    fieldData: List<FieldData>,
    onAction: (FieldAction) -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { fieldData.size },
    enableNext: Boolean = true
) {
    val horizontalPadding = 32
    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = softBeigeGradient,
                    start = Offset(x = 50f, y = 0f),
                    end = Offset(x = 50f, y = Float.POSITIVE_INFINITY)
                )
            )
            .padding(
                top = statusBarHeight + 64.dp,
                bottom = navBarHeight + 16.dp
            ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AppLogo(
            modifier = Modifier
                .padding(start = horizontalPadding.dp)
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(12.dp)
        )
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = horizontalPadding.dp),
            pageSpacing = (horizontalPadding * 2).dp,
            userScrollEnabled = false
        ) {
            Field(
                data = fieldData[it],
                modifier = Modifier.fillMaxWidth()
            )
        }
        FieldActions(
            fieldCount = fieldData.size,
            currentFieldIndex = pagerState.currentPage,
            onClick = onAction,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding.dp),
            enableNext = enableNext
        )
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
        verticalArrangement = Arrangement.spacedBy(if (data.options.size == 1) 0.dp else 8.dp)
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
            lineHeight = (initialFontSize + 8).sp,
            color = Color.Black
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
            },
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black
            )
        ) else Column {
            Text(
                text = stringResource(RegistrationDictionary.changeLater),
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = Color.Black.copy(alpha = 0.7f)
            )
            TextFieldWithOptions(
                options = data.options,
                onValueChange = data.onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldWithOptionsDefaults.colors(
                    optionsBackground = CharcoalClay,
                    optionsColor = Color.White,
                    selectedOptionColor = Color.White,
                    optionsSpacerColor = Color.White,
                    selectedColor = Color.Black
                )
            )
        }
    }
}

@Composable
private fun FieldActions(
    fieldCount: Int,
    currentFieldIndex: Int,
    onClick: (FieldAction) -> Unit,
    modifier: Modifier = Modifier,
    enableNext: Boolean = true
) {
    val actionSize = MaterialTheme.typography.bodyMedium.fontSize.value.toInt()
    val actionColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = { onClick(FieldAction.Previous) },
            enabled = currentFieldIndex != 0
        ) {
            AnimatedVisibility(visible = currentFieldIndex != 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(TaskifyIcon.arrowRight),
                        contentDescription = "previous",
                        modifier = Modifier
                            .rotate(180f)
                            .size(actionSize.dp),
                        tint = actionColor
                    )
                    Text(
                        text = stringResource(RegistrationDictionary.previous),
                        fontSize = actionSize.sp,
                        color = actionColor
                    )
                }
            }
        }
        AnimatedContent(
            targetState = currentFieldIndex != fieldCount - 1,
            label = "next button"
        ) {
            TextButton(
                onClick = {
                    if (it) onClick(FieldAction.Next) else onClick(FieldAction.Complete)
                },
                enabled = enableNext
            ) {
                Text(
                    text = stringResource(
                        id = if (it) RegistrationDictionary.next else RegistrationDictionary.complete
                    ),
                    fontSize = actionSize.sp,
                    color = if (it) actionColor else MaterialTheme.colorScheme.tertiary
                )
                if (it) Icon(
                    painter = painterResource(TaskifyIcon.arrowRight),
                    contentDescription = "next",
                    modifier = Modifier.size(actionSize.dp),
                    tint = actionColor
                )
            }
        }
    }
}

@Preview
@Composable
private fun ContentPreview() {
    val state = rememberPagerState { 3 }
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    TaskifyTheme {
        Content(
            fieldData = FieldData.fieldData(username, { username = it }, {}, {}),
            onAction = {
                scope.launch {
                    when (it) {
                        FieldAction.Next -> state.animateScrollToPage(state.currentPage + 1)
                        FieldAction.Previous -> state.animateScrollToPage(state.currentPage - 1)
                        FieldAction.Complete -> {}
                    }
                }
            },
            pagerState = state
        )
    }
}