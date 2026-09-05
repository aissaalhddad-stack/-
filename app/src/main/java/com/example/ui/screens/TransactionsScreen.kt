package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.TransactionEntity
import com.example.ui.model.LedgerConstants
import com.example.ui.theme.ExpenseRose
import com.example.ui.theme.FamilyBlue
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.LedgerUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionsScreen(
    state: LedgerUiState,
    onSearchChange: (String) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
    onMemberFilterChange: (String?) -> Unit,
    onScopeChange: (String) -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    onAddTransactionClick: () -> Unit
) {
    val currency = state.selectedCurrency
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale("ar"))
    var transactionToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("transactions_screen")
        ) {
            // Screen Title & Search
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "سجل المعاملات والعمليات",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transactions_search_input"),
                    placeholder = { Text("بحث في المعاملات، الفئات، أفراد العائلة...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "بحث")
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    singleLine = true
                )
            }

            // Horizontal Filter Chips: Scope & Members
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Scope Filters
                item {
                    FilterChip(
                        selected = state.selectedScope == "ALL",
                        onClick = { onScopeChange("ALL") },
                        label = { Text("الكل") }
                    )
                }
                item {
                    FilterChip(
                        selected = state.selectedScope == "FAMILY",
                        onClick = { onScopeChange(if (state.selectedScope == "FAMILY") "ALL" else "FAMILY") },
                        label = { Text("عائلي فقط") }
                    )
                }
                item {
                    FilterChip(
                        selected = state.selectedScope == "PERSONAL",
                        onClick = { onScopeChange(if (state.selectedScope == "PERSONAL") "ALL" else "PERSONAL") },
                        label = { Text("شخصي فقط") }
                    )
                }

                // Member Filter Chips
                items(LedgerConstants.familyMembers) { member ->
                    FilterChip(
                        selected = state.selectedMemberFilter == member,
                        onClick = {
                            onMemberFilterChange(if (state.selectedMemberFilter == member) null else member)
                        },
                        label = { Text(member) }
                    )
                }
            }

            // Filtered Transactions Count & Totals Bar
            val filteredCount = state.filteredTransactions.size
            val filteredExpenses = state.filteredTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            val filteredIncome = state.filteredTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$filteredCount معاملة",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "دخل: +${"%.0f".format(filteredIncome)} $currency",
                            fontSize = 12.sp,
                            color = IncomeGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "صرف: -${"%.0f".format(filteredExpenses)} $currency",
                            fontSize = 12.sp,
                            color = ExpenseRose,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Transactions List
            if (state.filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "لا توجد معاملات مطابقة",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اضغط على زر (+) بالأسفل لإضافة معاملة جديدة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.filteredTransactions, key = { it.id }) { tx ->
                        val isExpense = tx.type == "EXPENSE"
                        val catInfo = LedgerConstants.getCategoryInfo(tx.category)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("transaction_row_${tx.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(catInfo.color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            catInfo.icon,
                                            contentDescription = null,
                                            tint = catInfo.color,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = tx.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (tx.scope == "FAMILY") FamilyBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                            ) {
                                                Text(
                                                    text = "${if (tx.scope == "FAMILY") "عائلي" else "شخصي"} • ${tx.familyMember}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (tx.scope == "FAMILY") FamilyBlue else MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            Text(
                                                text = "• ${tx.wallet}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        if (tx.note.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = tx.note,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }

                                        Text(
                                            text = dateFormat.format(Date(tx.timestamp)),
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${if (isExpense) "-" else "+"}${"%.1f".format(tx.amount)} $currency",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = if (isExpense) ExpenseRose else IncomeGreen
                                    )

                                    IconButton(
                                        onClick = { transactionToDelete = tx },
                                        modifier = Modifier.testTag("delete_tx_${tx.id}")
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "حذف المعاملة",
                                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to add transaction
        FloatingActionButton(
            onClick = onAddTransactionClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 76.dp, end = 20.dp)
                .testTag("transactions_fab_add"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "إضافة معاملة")
        }
    }

    // Delete Confirmation Dialog
    if (transactionToDelete != null) {
        val target = transactionToDelete!!
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("حذف المعاملة") },
            text = { Text("هل أنت متأكد من رغبتك في حذف \"${target.title}\" بمبلغ ${target.amount} $currency؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTransaction(target.id)
                        transactionToDelete = null
                    }
                ) {
                    Text("حذف", color = ExpenseRose, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
