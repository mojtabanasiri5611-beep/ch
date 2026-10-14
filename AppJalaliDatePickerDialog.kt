package com.example.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.core.common.JalaliDate
import com.example.core.utils.PersianNumberUtils
import com.example.ui.theme.VazirmatnFontFamily

@Composable
fun AppJalaliDatePickerDialog(
    isOpen: Boolean = true,
    title: String = "انتخاب تاریخ",
    initialDateString: String? = null,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val parsedInitial = remember(initialDateString) {
        if (!initialDateString.isNullOrBlank()) {
            JalaliDate.parse(initialDateString) ?: JalaliDate.now()
        } else {
            JalaliDate.now()
        }
    }

    var displayedYear by remember { mutableIntStateOf(parsedInitial.year) }
    var displayedMonth by remember { mutableIntStateOf(parsedInitial.month) }
    var selectedDay by remember { mutableIntStateOf(parsedInitial.day) }

    val monthNames = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // هدر دیالوگ: عنوان مشخص در گوشه سمت راست بالا و نام ماه/سال
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = title,
                            fontFamily = VazirmatnFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Text(
                        text = "${monthNames.getOrElse(displayedMonth - 1) { "" }} ${PersianNumberUtils.toPersianDigits(displayedYear)}",
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // نوار جابجایی ماه‌ها فقط با کلیک روی دکمه (سوایپ حذف شده است)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (displayedMonth < 12) displayedMonth++ else {
                            displayedMonth = 1
                            displayedYear++
                        }
                    }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "ماه بعد")
                    }

                    Text(
                        text = "انتخاب ماه و سال",
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.outline
                    )

                    IconButton(onClick = {
                        if (displayedMonth > 1) displayedMonth-- else {
                            displayedMonth = 12
                            displayedYear--
                        }
                    }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "ماه قبل")
                    }
                }

                // روزهای هفته
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("ش", "ی", "د", "س", "چ", "پ", "ج").forEachIndexed { idx, dayName ->
                        Text(
                            text = dayName,
                            fontFamily = VazirmatnFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (idx == 6) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(32.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // شبکه روزهای ماه با چینش بر اساس روز اول ماه
                val daysInMonth = JalaliDate.getMonthLength(displayedYear, displayedMonth)
                val firstDayOfWeek = JalaliDate(displayedYear, displayedMonth, 1).dayOfWeek
                val totalCells = firstDayOfWeek + daysInMonth

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth().height(210.dp),
                    userScrollEnabled = false // حذف هرگونه ژستور اسکرول/سوایپ ناخواسته
                ) {
                    items(totalCells) { cellIndex ->
                        if (cellIndex < firstDayOfWeek) {
                            Box(modifier = Modifier.aspectRatio(1f))
                        } else {
                            val day = cellIndex - firstDayOfWeek + 1
                            val isSelected = (day == selectedDay)
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { selectedDay = day },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = PersianNumberUtils.toPersianDigits(day.toString()),
                                    fontFamily = VazirmatnFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                // بخش پایین تقویم: دکمه امروز در پایین سمت چپ + دکمه‌های تایید و انصراف
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // آیکون کوچک بازگشت به تاریخ امروز در پایین سمت چپ
                    IconButton(
                        onClick = {
                            val today = JalaliDate.now()
                            displayedYear = today.year
                            displayedMonth = today.month
                            selectedDay = today.day
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = "برو به امروز",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row {
                        TextButton(onClick = onDismiss) {
                            Text("انصراف", fontFamily = VazirmatnFontFamily, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val selectedDate = JalaliDate(displayedYear, displayedMonth, selectedDay)
                                onDateSelected(selectedDate.formattedString)
                                onDismiss()
                            }
                        ) {
                            Text("انتخاب", fontFamily = VazirmatnFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppJalaliDatePickerDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    AppJalaliDatePickerDialog(
        isOpen = isOpen,
        title = "انتخاب تاریخ",
        initialDateString = null,
        onDateSelected = onDateSelected,
        onDismiss = onDismiss
    )
}

@Composable
fun AppJalaliDatePickerDialog(
    datePickerTypeTitle: String,
    currentDate: JalaliDate? = null,
    onDateSelected: (JalaliDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    AppJalaliDatePickerDialog(
        isOpen = true,
        title = datePickerTypeTitle,
        initialDateString = currentDate?.formattedString,
        onDateSelected = { str ->
            val parsed = JalaliDate.parse(str) ?: JalaliDate.now()
            onDateSelected(parsed)
        },
        onDismiss = onDismissRequest
    )
}

fun convertMillisToJalaliString(millis: Long): String {
    return JalaliDate.fromTimestamp(millis).formattedString
}
