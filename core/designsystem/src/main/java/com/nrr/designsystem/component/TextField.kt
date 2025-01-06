package com.nrr.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.TaskifyTheme
import androidx.compose.material3.TextField as TF

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    placeholder: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = TaskifyTextFieldDefaults.colors(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    TF(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        colors = colors,
        singleLine = singleLine,
        readOnly = readOnly,
        trailingIcon = trailingIcon,
        enabled = enabled,
        interactionSource = interactionSource,
        placeholder = placeholder,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

object TaskifyTextFieldDefaults {
    private val whiteOrBlack: Color
        @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

    @Composable
    fun colors(
        unfocusedContainerColor: Color = Color.Transparent,
        focusedContainerColor: Color = Color.Transparent,
        unfocusedIndicatorColor: Color = MaterialTheme.colorScheme.primary,
        focusedIndicatorColor: Color = MaterialTheme.colorScheme.primary,
        unfocusedTextColor: Color = whiteOrBlack,
        focusedTextColor: Color = whiteOrBlack,
        unfocusedPlaceholderColor: Color = Color.Gray,
        focusedPlaceholderColor: Color = Color.Gray
    ): TextFieldColors = TextFieldDefaults.colors(
        unfocusedContainerColor = unfocusedContainerColor,
        focusedContainerColor = focusedContainerColor,
        unfocusedIndicatorColor = unfocusedIndicatorColor,
        focusedIndicatorColor = focusedIndicatorColor,
        unfocusedTextColor = unfocusedTextColor,
        focusedTextColor = focusedTextColor,
        unfocusedPlaceholderColor = unfocusedPlaceholderColor,
        focusedPlaceholderColor = focusedPlaceholderColor
    )
}

// might cause issues if calling composable doesn't handle post config changes
// since selected value saved locally here with rememberSaveable
@Composable
fun TextFieldWithOptions(
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: TextFieldWithOptionsColors = TaskifyTextFieldWithOptionsDefaults.colors()
) {
    if (options.isNotEmpty()) {
        var selected by rememberSaveable { mutableStateOf(options[0]) }
        var showOptions by rememberSaveable { mutableStateOf(false) }
        val rotateValue by animateFloatAsState(
            targetValue = if (showOptions) 180f else 0f,
            label = "chevron rotation"
        )
        val interactionSource = remember { MutableInteractionSource() }

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box {
                TextField(
                    value = selected,
                    onValueChange = {
                        selected = it
                        onValueChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(TaskifyIcon.chevronDown),
                            contentDescription = null,
                            modifier = Modifier
                                .rotate(rotateValue)
                                .size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    readOnly = true,
                    interactionSource = interactionSource.also {
                        LaunchedEffect(true) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    showOptions = !showOptions
                                }
                            }
                        }
                    },
                    colors = TaskifyTextFieldDefaults.colors(
                        unfocusedTextColor = colors.selectedColor,
                        focusedTextColor = colors.selectedColor
                    )
                )
            }
            AnimatedVisibility(visible = showOptions) {
                Options(
                    options = options,
                    selected = selected,
                    onClick = {
                        selected = it
                        showOptions = false
                    },
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun Options(
    options: List<String>,
    selected: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: TextFieldWithOptionsColors = TaskifyTextFieldWithOptionsDefaults.colors()
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.optionsBackground)
    ) {
        options.forEachIndexed { i, t ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (selected == t) colors.selectedOptionBackground else Color.Transparent)
                    .clickable { onClick(t) }
            ) {
                Text(
                    text = t,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    color = if (selected == t) colors.selectedOptionColor else colors.optionsColor
                )
                if (i != options.lastIndex) Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.optionsSpacerColor)
                )
            }
        }
    }
}

data class TextFieldWithOptionsColors(
    val optionsBackground: Color,
    val optionsSpacerColor: Color,
    val optionsColor: Color,
    val selectedOptionBackground: Color,
    val selectedOptionColor: Color,
    val selectedColor: Color
)

object TaskifyTextFieldWithOptionsDefaults {
    private val whiteOrBlack: Color
        @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

    @Composable
    fun colors(
        optionsBackground: Color = MaterialTheme.colorScheme.onBackground,
        optionsSpacerColor: Color = whiteOrBlack,
        optionsColor: Color = whiteOrBlack,
        selectedOptionColor: Color = Color.White,
        selectedOptionBackground: Color = MaterialTheme.colorScheme.primary,
        selectedColor: Color = whiteOrBlack
    ): TextFieldWithOptionsColors = TextFieldWithOptionsColors(
        optionsBackground = optionsBackground,
        optionsSpacerColor = optionsSpacerColor,
        optionsColor = optionsColor,
        selectedOptionColor = selectedOptionColor,
        selectedOptionBackground = selectedOptionBackground,
        selectedColor = selectedColor
    )
}

@Preview
@Composable
private fun TextFieldPreview() {
    var string by remember { mutableStateOf("Hello") }
    TaskifyTheme {
        TextField(
            value = string,
            onValueChange = { string = it },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun TextFieldWithOptionsPreview() {
    TaskifyTheme {
        TextFieldWithOptions(
            options = listOf("Hello", "World"),
            onValueChange = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}