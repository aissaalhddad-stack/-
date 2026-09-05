package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DebtOrange
import com.example.ui.theme.IncomeGreen

@Composable
fun AddDebtDialog(
    currency: String,
    onDismiss: () -> Unit,
    onConfirm: (
        personName: String,
        type: String,
        totalAmount: Double,
        paidAmount: Double,
        note: String
    ) -> Unit
) {
    var isLent by remember { mutableStateOf(true) } // true: I Lent (دين لي), false: I Borrowed (دين علي)
    var personName by remember { mutableStateOf("") }
    var totalAmountText by remember { mutableStateOf("") }
    var paidAmountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تسجيل دين جديد",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Debt Type Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // I Lent (لي عند الآخرين)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isLent) IncomeGreen else Color.Transparent)
                            .clickable { isLent = true }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CallReceived,
                                contentDescription = null,
                                tint = if (isLent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "دين لي (مستحق)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isLent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // I Borrowed (علي للآخرين)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isLent) DebtOrange else Color.Transparent)
                            .clickable { isLent = false }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CallMade,
                                contentDescription = null,
                                tint = if (!isLent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "دين علي (التزام)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (!isLent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Person / Entity Name
                OutlinedTextField(
                    value = personName,
                    onValueChange = {
                        personName = it
                        nameError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("debt_person_name_input"),
                    label = { Text(if (isLent) "اسم المدين (الطرف المستلف)" else "اسم الدائن (صاحب الدين)") },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("يرجى إدخال اسم الشخص أو الجهة") }
                    } else null,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Total Amount
                OutlinedTextField(
                    value = totalAmountText,
                    onValueChange = {
                        totalAmountText = it
                        amountError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("debt_total_amount_input"),
                    label = { Text("المبلغ الإجمالي") },
                    trailingIcon = {
                        Text(
                            text = currency,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Paid Amount (if already partially paid)
                OutlinedTextField(
                    value = paidAmountText,
                    onValueChange = { paidAmountText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("المدفوع مسبقاً (اختياري)") },
                    placeholder = { Text("0") },
                    trailingIcon = {
                        Text(
                            text = currency,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("سبب الدين / موعد الاستحقاق (اختياري)") },
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Button
                Button(
                    onClick = {
                        if (personName.isBlank()) {
                            nameError = true
                            return@Button
                        }
                        val total = totalAmountText.toDoubleOrNull()
                        if (total == null || total <= 0.0) {
                            amountError = true
                            return@Button
                        }
                        val paid = paidAmountText.toDoubleOrNull() ?: 0.0
                        onConfirm(
                            personName.trim(),
                            if (isLent) "I_LENT" else "I_BORROWED",
                            total,
                            paid.coerceAtMost(total),
                            note.trim()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_add_debt_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLent) IncomeGreen else DebtOrange
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حفظ بيانات الدين", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
