package com.example.presentation.ui.archive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.common.JalaliDate
import com.example.domain.model.Cheque
import com.example.presentation.ui.cheques.components.ChequeItemCard
import com.example.presentation.ui.components.AppDatePickerField
import com.example.ui.theme.VazirmatnFontFamily

@Composable
fun ArchiveReportScreen(
    allCheques: List<Cheque>,
    isToman: Boolean,
    modifier: Modifier = Modifier
) {
    val today = remember { JalaliDate.now() }
    var selectedBank by remember { mutableStateOf("همه بانک‌ها") }
    var targetCounterparty by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("1400/01/01") }
    var endDate by remember { mutableStateOf(today.addDays(365).formattedString) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var bankDropdownExpanded by remember { mutableStateOf(false) }

    val bankList = remember(allCheques) {
        listOf("همه بانک‌ها") + allCheques.map { it.bankName }.distinct().sorted()
    }

    // فیلتر هوشمند آرشیو بر اساس بانک، گیرنده و بازه تاریخی
    val filteredArchive = remember(allCheques, selectedBank, targetCounterparty, startDate, endDate) {
        allCheques.filter { cheque ->
            val matchBank = selectedBank == "همه بانک‌ها" || cheque.bankName == selectedBank
            val matchParty = targetCounterparty.isBlank() || cheque.counterpartyName.contains(targetCounterparty, ignoreCase = true)
            // مقایسه رشته‌ای تاریخ‌های جلالی (فرمت YYYY/MM/DD)
            val matchDate = cheque.dueDateJalali >= startDate && cheque.dueDateJalali <= endDate
            matchBank && matchParty && matchDate
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "آرشیو و گزارش‌گیری پیشرفته چک‌ها",
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Bold)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { bankDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(selectedBank, style = MaterialTheme.typography.labelMedium, fontFamily = VazirmatnFontFamily)
                        }
                        DropdownMenu(expanded = bankDropdownExpanded, onDismissRequest = { bankDropdownExpanded = false }) {
                            bankList.forEach { bank ->
                                DropdownMenuItem(
                                    text = { Text(bank, fontFamily = VazirmatnFontFamily) },
                                    onClick = {
                                        selectedBank = bank
                                        bankDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = targetCounterparty,
                        onValueChange = { targetCounterparty = it },
                        modifier = Modifier.weight(1f).height(48.dp),
                        placeholder = { Text("جستجوی طرف حساب...", fontSize = 10.sp, fontFamily = VazirmatnFontFamily) },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = VazirmatnFontFamily)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppDatePickerField(
                        label = "از تاریخ",
                        date = JalaliDate.toPersianDigits(startDate),
                        onClick = { showStartDatePicker = true },
                        modifier = Modifier.weight(1f)
                    )
                    AppDatePickerField(
                        label = "تا تاریخ",
                        date = JalaliDate.toPersianDigits(endDate),
                        onClick = { showEndDatePicker = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "یافته شده: ${filteredArchive.size} فقره | مجموع: ${JalaliDate.formatCurrency(filteredArchive.sumOf { it.amount }, isToman)}",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filteredArchive, key = { it.id }) { cheque ->
                ChequeItemCard(
                    cheque = cheque,
                    isToman = isToman,
                    onEditClick = {},
                    onDeleteClick = {},
                    onUpdateStatus = {},
                    onEndorse = {}
                )
            }
        }
    }

    if (showStartDatePicker) {
        com.example.presentation.ui.components.AppJalaliDatePickerDialog(
            title = "از تاریخ",
            initialDateString = startDate,
            onDateSelected = { startDate = it },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        com.example.presentation.ui.components.AppJalaliDatePickerDialog(
            title = "تا تاریخ",
            initialDateString = endDate,
            onDateSelected = { endDate = it },
            onDismiss = { showEndDatePicker = false }
        )
    }
}
