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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.BudgetEntity
import com.example.ui.model.LedgerConstants
import com.example.ui.theme.ExpenseRose
import com.example.ui.theme.ExpenseRoseContainer
import com.example.ui.theme.GoldAmber40
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenContainer
import com.example.ui.viewmodel.LedgerUiState

@Composable
fun BudgetsScreen(
    state: LedgerUiState,
    onAddBudgetClick: () -> Unit,
    onDeleteBudget: (Long) -> Unit
) {
    val currency = state.selectedCurrency
    var budgetToDelete by remember { mutableStateOf<BudgetEntity?>(null) }

    // Calculate actual spending for each category in current month
    val categorySpending = state.allTransactions
        .filter { it.type == "EXPENSE" }
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    val totalBudgetLimits = state.allBudgets.sumOf { it.limitAmount }
    val totalBudgetSpent = state.allBudgets.sumOf { categorySpending[it.category] ?: 0.0 }
    val overallProgress = if (totalBudgetLimits > 0) (totalBudgetSpent / totalBudgetLimits).toFloat() else 0f

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("budgets_screen")
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "الميزانيات وضبط المصاريف",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "حدد سقفاً شهرياً لمصاريف الأسرة والشخصية للتحكم بالإنفاق",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Summary Card
            if (state.allBudgets.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ملخص الميزانيات المحددة لهذا الشهر",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${(overallProgress * 100).toInt()}% مستهلك",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (overallProgress > 1f) ExpenseRose else MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { overallProgress.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (overallProgress > 1f) ExpenseRose else if (overallProgress > 0.8f) GoldAmber40 else IncomeGreen,
                            trackColor = MaterialTheme.colorScheme.surface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "المصروف: ${"%.0f".format(totalBudgetSpent)} $currency",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "السقف الإجمالي: ${"%.0f".format(totalBudgetLimits)} $currency",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Budgets List
            if (state.allBudgets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PieChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "لم تقم بتحديد ميزانيات بعد",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "اضغط على زر (+) لإضافة ميزانية شهرية (مثل البقالة أو الفواتير)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.allBudgets, key = { it.id }) { budget ->
                        val spent = categorySpending[budget.category] ?: 0.0
                        val limit = budget.limitAmount
                        val remaining = (limit - spent).coerceAtLeast(0.0)
                        val ratio = if (limit > 0) (spent / limit).toFloat() else 0f
                        val isExceeded = spent > limit
                        val catInfo = LedgerConstants.getCategoryInfo(budget.category)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("budget_card_${budget.id}"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(catInfo.color.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                catInfo.icon,
                                                contentDescription = null,
                                                tint = catInfo.color,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = budget.category,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = when (budget.scope) {
                                                    "FAMILY" -> "ميزانية عائلية"
                                                    "PERSONAL" -> "ميزانية شخصية"
                                                    else -> "ميزانية شاملة"
                                                },
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { budgetToDelete = budget },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "حذف الميزانية",
                                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Progress Bar
                                LinearProgressIndicator(
                                    progress = { ratio.coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (isExceeded) ExpenseRose else if (ratio > 0.8f) GoldAmber40 else IncomeGreen,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "المصروف الفعلي: ${"%.1f".format(spent)} $currency",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isExceeded) ExpenseRose else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "السقف: ${"%.0f".format(limit)} $currency",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    if (isExceeded) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = ExpenseRoseContainer
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = ExpenseRose,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "تجاوز بـ ${"%.0f".format(spent - limit)} $currency",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ExpenseRose
                                                )
                                            }
                                        }
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = IncomeGreenContainer
                                        ) {
                                            Text(
                                                text = "متبقي: ${"%.0f".format(remaining)} $currency",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = IncomeGreen,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB to add budget
        FloatingActionButton(
            onClick = onAddBudgetClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 76.dp, end = 20.dp)
                .testTag("budgets_fab_add"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "إضافة ميزانية")
        }
    }

    // Delete Confirmation Dialog
    if (budgetToDelete != null) {
        val target = budgetToDelete!!
        AlertDialog(
            onDismissRequest = { budgetToDelete = null },
            title = { Text("حذف الميزانية") },
            text = { Text("هل أنت متأكد من حذف ميزانية \"${target.category}\"؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteBudget(target.id)
                        budgetToDelete = null
                    }
                ) {
                    Text("حذف", color = ExpenseRose, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { budgetToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
