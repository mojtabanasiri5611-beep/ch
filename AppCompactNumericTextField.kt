package com.example.presentation.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VazirmatnFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppCompactNumericTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    maxLength: Int? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    subText: String? = null,
    subTextColor: Color = Color(0xFF8E24AA),
    isSayadId: Boolean = false,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }

    val activeVisualTransformation = when {
        visualTransformation != null -> visualTransformation
        isSayadId || (maxLength == 16 && label.contains("صیاد")) -> remember { SayadVisualTransformation() }
        else -> androidx.compose.ui.text.input.VisualTransformation.None
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .onGloballyPositioned { coordinates -> fieldSize = coordinates.size }
            .padding(bottom = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, fontFamily = VazirmatnFontFamily),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() }
                    if (maxLength == null || filtered.length <= maxLength) onValueChange(filtered)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                delay(300)
                                val nextFieldExpectedHeight = with(density) { 110.dp.toPx() }
                                val expandedBounds = Rect(
                                    left = 0f,
                                    top = 0f,
                                    right = fieldSize.width.toFloat(),
                                    bottom = fieldSize.height.toFloat() + nextFieldExpectedHeight
                                )
                                bringIntoViewRequester.bringIntoView(expandedBounds)
                            }
                        }
                    },
                singleLine = true,
                isError = isError,
                visualTransformation = activeVisualTransformation,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                textStyle = TextStyle(
                    fontFamily = VazirmatnFontFamily, fontSize = 16.5.sp, lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                    textDirection = TextDirection.Ltr, platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon ?: {
                    if (value.isNotEmpty()) {
                        IconButton(onClick = { onValueChange("") }) { Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (subText != null && !isError) {
            Text(
                text = subText,
                color = subTextColor,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = VazirmatnFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, top = 6.dp)
            )
        }
    }
}
