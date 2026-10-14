package com.example.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.common.JalaliDate
import com.example.core.ui.components.AppText
import com.example.core.util.MoneyToWordsConverter
import com.example.presentation.ui.components.NumericVisualTransformation
import com.example.ui.theme.VazirmatnFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChequeAmountInputField(
    amountRial: Long,
    onAmountChange: (Long) -> Unit,
    currencyUnit: String = "ریال",
    modifier: Modifier = Modifier
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    val rawString = if (amountRial > 0) amountRial.toString() else ""

    val wordsSubtitle = remember(amountRial, currencyUnit) {
        if (amountRial <= 0L) ""
        else {
            val words = MoneyToWordsConverter.convert(amountRial, isToman = currencyUnit == "تومان")
            "$words $currencyUnit"
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = rawString,
            onValueChange = { input ->
                val clean = JalaliDate.toEnglishDigits(input).filter { it.isDigit() }
                val parsed = clean.toLongOrNull() ?: 0L
                onAmountChange(parsed)
            },
            label = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AppText("مبلغ چک ($currencyUnit)", fontSize = 14.sp)
                }
            },
            placeholder = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AppText("مبلغ را به $currencyUnit وارد کنید", fontSize = 13.sp)
                }
            },
            textStyle = TextStyle(
                fontFamily = VazirmatnFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center // کاملاً وسط‌نویس
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = NumericVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        coroutineScope.launch {
                            delay(250)
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // زیرنویس به حروف کاملاً وسط‌نویس
        AppText(
            text = if (wordsSubtitle.isNotEmpty()) "به حروف: $wordsSubtitle" else "مبلغ را وارد کنید",
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            color = if (wordsSubtitle.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        )
    }
}
