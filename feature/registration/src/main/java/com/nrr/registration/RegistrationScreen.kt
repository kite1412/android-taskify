package com.nrr.registration

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import com.nrr.designsystem.component.TaskifyTextFieldDefaults
import com.nrr.designsystem.component.TaskifyTextFieldWithOptionsDefaults
import com.nrr.designsystem.component.TextField
import com.nrr.designsystem.component.TextFieldWithOptions
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.CharcoalClay
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.theme.softBeigeGradient
import com.nrr.model.LanguageConfig
import com.nrr.model.ThemeConfig
import com.nrr.registration.model.FieldAction
import com.nrr.registration.model.FieldData
import com.nrr.registration.util.RegistrationDictionary
import com.nrr.ui.LocalSnackbarHostState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val fieldData = FieldData.fieldsData(
        username = viewModel.username,
        languageConfig = viewModel.languageConfig,
        themeConfig = viewModel.themeConfig,
        onUsernameChange = viewModel::updateUsername,
        onLanguageChange = viewModel::updateLanguageConfig,
        onThemeChange = viewModel::updateThemeConfig
    )
    val pagerState = rememberPagerState { fieldData.size }
    val scope = rememberCoroutineScope()
    val snackbarState = LocalSnackbarHostState.current
    val greeting = stringResource(RegistrationDictionary.greeting)
    val register by rememberUpdatedState(viewModel::register)

    Content(
        fieldData = fieldData,
        onAction = {
            scope.launch {
                when (it) {
                    FieldAction.Next -> pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    FieldAction.Previous -> pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    FieldAction.Complete -> {
                        snackbarState.showSnackbar("$greeting ${viewModel.username}!") {
                            if (it == SnackbarResult.Dismissed) register()
                        }
                    }
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
        ) { page ->
            val data = fieldData[page]

            if (data.options.size == 1) Field(
                data = data,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Text(
                        text = "${it.length}/20",
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        color = Color.Black
                    )
                }
            ) else FieldWithOptions(
                data = data,
                options = data.options,
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
    modifier: Modifier = Modifier,
    trailingIcon: @Composable ((String) -> Unit)? = null
) {
    FieldScaffold(
        data = data,
        modifier = modifier
    ) {
        TextField(
            value = data.currentValue,
            onValueChange = data.onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = data.placeholder ?: "",
                    color = Color.Black.copy(alpha = 0.7f),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            },
            colors = TaskifyTextFieldDefaults.colors(
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black
            ),
            trailingIcon = if (trailingIcon != null) {
                { trailingIcon.invoke(data.options[0]) }
            } else null
        )
    }
}

@Composable
private fun FieldWithOptions(
    data: FieldData,
    options: List<String>,
    modifier: Modifier = Modifier
) {
    FieldScaffold(
        data = data,
        modifier = modifier
    ) {
        Column {
            Text(
                text = stringResource(RegistrationDictionary.changeLater),
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = Color.Black.copy(alpha = 0.7f)
            )
            TextFieldWithOptions(
                selected = data.currentValue,
                options = options,
                onValueChange = data.onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = TaskifyTextFieldWithOptionsDefaults.colors(
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
private inline fun FieldScaffold(
    data: FieldData,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
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
        content()
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
                enabled = enableNext,
                colors = ButtonDefaults.textButtonColors(
                    disabledContentColor = Color.Gray,
                    contentColor = if (it) actionColor else MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text(
                    text = stringResource(
                        id = if (it) RegistrationDictionary.next else RegistrationDictionary.complete
                    ),
                    fontSize = actionSize.sp,
                )
                if (it) Icon(
                    painter = painterResource(TaskifyIcon.arrowRight),
                    contentDescription = "next",
                    modifier = Modifier.size(actionSize.dp),
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
            fieldData = FieldData.fieldsData(
                username = username,
                languageConfig = LanguageConfig.SYSTEM_DEFAULT,
                themeConfig = ThemeConfig.SYSTEM_DEFAULT,
                onUsernameChange = { username = it },
                onLanguageChange = {},
                onThemeChange = {}
            ),
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