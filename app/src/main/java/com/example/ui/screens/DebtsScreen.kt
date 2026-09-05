package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DebtEntity
import com.example.ui.theme.DebtOrange
import com.example.ui.theme.DebtOrangeContainer
import com.example.ui.theme.ExpenseRose
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenContainer
import com.example.ui.viewmodel.LedgerUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DebtsScreen(
    state: LedgerUiState,
    onAddDebtClick: () -> Unit,
    onRecordPaymentClick: (DebtEntity) -> Unit,
    onToggleSettled: (DebtEntity) -> Unit,
    onDeleteDebt: (Long) -> Unit
) {
    val currency = state.selectedCurrency
    var selectedDebtType by remember { mutableStateOf("I_LENT") } // I_LENT or I_BORROWED
    var debtToDelete by remember { mutableStateOf<DebtEntity?>(null) }
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("ar"))

    val activeDebts = state.allDebts.filter { it.type == selectedDebtType }
    val totalOutstanding = activeDebts.filter { !it.isSettled }.sumOf { it.totalAmount - it.paidAmount }
    val totalSettled = activeDebts.sumOf { it.paidAmount }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("debts_screen")
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "دفتر الديون والالتزامات",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "متابعة السلف والمستحقات العائلية والشخصية",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Tab Selector: ديون لي vs ديون علي
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // I Lent
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedDebtType == "I_LENT") IncomeGreen else Color.Transparent)
                        .clickable { selectedDebtType = "I_LENT" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CallReceived,
                            contentDescription = null,
                            tint = if (selectedDebtType == "I_LENT") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ديون لي (مستحقات)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedDebtType == "I_LENT") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // I Borrowed
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedDebtType == "I_BORROWED") DebtOrange else Color.Transparent)
                        .clickable { selectedDebtType = "I_BORROWED" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CallMade,
                            contentDescription = null,
                            tint = if (selectedDebtType == "I_BORROWED") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ديون علي (التزامات)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedDebtType == "I_BORROWED") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Debt Totals Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                color = if (selectedDebtType == "I_LENT") IncomeGreenContainer else DebtOrangeContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (selectedDebtType == "I_LENT") "إجمالي المستحق لي بذمة الآخرين:" else "إجمالي المستحق علي للآخرين:",
                            fontSize = 12.sp,
                            color = if (selectedDebtType == "I_LENT") IncomeGreen else DebtOrange,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${"%.1f".format(totalOutstanding)} $currency",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (selectedDebtType == "I_LENT") IncomeGreen else DebtOrange
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "المسدد حتى الآن:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${"%.1f".format(totalSettled)} $currency",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Debt Items List
            if (activeDebts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (selectedDebtType == "I_LENT") "لا توجد ديون مستحقة لك حالياً" else "لا توجد التزامات مسجلة عليك حالياً",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "اضغط على زر (+) لتسجيل التزام أو سلفة جديدة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeDebts, key = { it.id }) { debt ->
                        val remaining = (debt.totalAmount - debt.paidAmount).coerceAtLeast(0.0)
                        val progress = if (debt.totalAmount > 0) (debt.paidAmount / debt.totalAmount).toFloat() else 1f

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("debt_card_${debt.id}"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (debt.isSettled) IncomeGreen.copy(alpha = 0.15f)
                                                    else if (selectedDebtType == "I_LENT") IncomeGreen.copy(alpha = 0.15f)
                                                    else DebtOrange.copy(alpha = 0.15f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = debt.personName.take(1),
                                                fontWeight = FontWeight.Bold,
                                                color = if (debt.isSettled) IncomeGreen else if (selectedDebtType == "I_LENT") IncomeGreen else DebtOrange,
                                                fontSize = 16.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = debt.personName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "سُجِّل بتاريخ: ${dateFormat.format(Date(debt.timestamp))}",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Status Badge
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (debt.isSettled) IncomeGreenContainer else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = if (debt.isSettled) "مسدد بالكامل" else "متبقي: ${"%.0f".format(remaining)} $currency",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (debt.isSettled) IncomeGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                if (debt.note.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = debt.note,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Progress Bar
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (debt.isSettled) IncomeGreen else MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "المسدد: ${"%.1f".format(debt.paidAmount)} $currency",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "الإجمالي: ${"%.1f".format(debt.totalAmount)} $currency",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (!debt.isSettled) {
                                        Button(
                                            onClick = { onRecordPaymentClick(debt) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("تسجيل دفعة", fontSize = 12.sp)
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { onToggleSettled(debt) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = if (debt.isSettled) IncomeGreen else MaterialTheme.colorScheme.outline
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            if (debt.isSettled) "إلغاء الإغلاق" else "تم السداد",
                                            fontSize = 12.sp
                                        )
                                    }

                                    IconButton(
                                        onClick = { debtToDelete = debt },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "حذف",
                                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB to add new debt
        FloatingActionButton(
            onClick = onAddDebtClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 76.dp, end = 20.dp)
                .testTag("debts_fab_add"),
            containerColor = if (selectedDebtType == "I_LENT") IncomeGreen else DebtOrange,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "إضافة دين")
        }
    }

    // Delete Confirmation Dialog
    if (debtToDelete != null) {
        val target = debtToDelete!!
        AlertDialog(
            onDismissRequest = { debtToDelete = null },
            title = { Text("حذف سجل الدين") },
            text = { Text("هل أنت متأكد من حذف سجل الدين الخاص بـ \"${target.personName}\"؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteDebt(target.id)
                        debtToDelete = null
                    }
                ) {
                    Text("حذف", color = ExpenseRose, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { debtToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
