package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.DebtEntity
import com.example.ui.dialogs.AddBudgetDialog
import com.example.ui.dialogs.AddDebtDialog
import com.example.ui.dialogs.AddTransactionDialog
import com.example.ui.dialogs.CurrencyDialog
import com.example.ui.dialogs.RecordPaymentDialog
import com.example.ui.screens.BudgetsScreen
import com.example.ui.screens.DebtsScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LedgerTab
import com.example.ui.viewmodel.LedgerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    LedgerApp()
                }
            }
        }
    }
}

@Composable
fun LedgerApp(viewModel: LedgerViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var showAddDebtDialog by remember { mutableStateOf(false) }
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var debtForPayment by remember { mutableStateOf<DebtEntity?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("ledger_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Tab 1: Overview
                NavigationBarItem(
                    selected = state.selectedTab == LedgerTab.OVERVIEW,
                    onClick = { viewModel.selectTab(LedgerTab.OVERVIEW) },
                    icon = {
                        Icon(
                            if (state.selectedTab == LedgerTab.OVERVIEW) Icons.Default.Dashboard else Icons.Outlined.Dashboard,
                            contentDescription = "الرئيسية",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            "الرئيسية",
                            fontSize = 11.sp,
                            fontWeight = if (state.selectedTab == LedgerTab.OVERVIEW) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("nav_tab_overview")
                )

                // Tab 2: Transactions
                NavigationBarItem(
                    selected = state.selectedTab == LedgerTab.TRANSACTIONS,
                    onClick = { viewModel.selectTab(LedgerTab.TRANSACTIONS) },
                    icon = {
                        Icon(
                            if (state.selectedTab == LedgerTab.TRANSACTIONS) Icons.Default.ReceiptLong else Icons.Outlined.ReceiptLong,
                            contentDescription = "المعاملات",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            "المعاملات",
                            fontSize = 11.sp,
                            fontWeight = if (state.selectedTab == LedgerTab.TRANSACTIONS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("nav_tab_transactions")
                )

                // Tab 3: Debts
                NavigationBarItem(
                    selected = state.selectedTab == LedgerTab.DEBTS,
                    onClick = { viewModel.selectTab(LedgerTab.DEBTS) },
                    icon = {
                        Icon(
                            if (state.selectedTab == LedgerTab.DEBTS) Icons.Default.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                            contentDescription = "الديون",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            "الديون",
                            fontSize = 11.sp,
                            fontWeight = if (state.selectedTab == LedgerTab.DEBTS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("nav_tab_debts")
                )

                // Tab 4: Budgets
                NavigationBarItem(
                    selected = state.selectedTab == LedgerTab.BUDGETS,
                    onClick = { viewModel.selectTab(LedgerTab.BUDGETS) },
                    icon = {
                        Icon(
                            if (state.selectedTab == LedgerTab.BUDGETS) Icons.Default.PieChart else Icons.Outlined.PieChart,
                            contentDescription = "الميزانيات",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            "الميزانيات",
                            fontSize = 11.sp,
                            fontWeight = if (state.selectedTab == LedgerTab.BUDGETS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("nav_tab_budgets")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = state.selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_transition"
            ) { targetTab ->
                when (targetTab) {
                    LedgerTab.OVERVIEW -> {
                        OverviewScreen(
                            state = state,
                            onScopeSelected = { viewModel.setScope(it) },
                            onPeriodSelected = { viewModel.setPeriod(it) },
                            onAddExpenseClick = { showAddTransactionDialog = true },
                            onAddIncomeClick = { showAddTransactionDialog = true },
                            onAddDebtClick = { showAddDebtDialog = true },
                            onOpenCurrencyDialog = { showCurrencyDialog = true },
                            onNavigateToTab = { viewModel.selectTab(it) },
                            onDeleteTransaction = { viewModel.deleteTransaction(it) }
                        )
                    }

                    LedgerTab.TRANSACTIONS -> {
                        TransactionsScreen(
                            state = state,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onCategoryFilterChange = { viewModel.setCategoryFilter(it) },
                            onMemberFilterChange = { viewModel.setMemberFilter(it) },
                            onScopeChange = { viewModel.setScope(it) },
                            onDeleteTransaction = { viewModel.deleteTransaction(it) },
                            onAddTransactionClick = { showAddTransactionDialog = true }
                        )
                    }

                    LedgerTab.DEBTS -> {
                        DebtsScreen(
                            state = state,
                            onAddDebtClick = { showAddDebtDialog = true },
                            onRecordPaymentClick = { debtForPayment = it },
                            onToggleSettled = { viewModel.toggleDebtSettled(it) },
                            onDeleteDebt = { viewModel.deleteDebt(it) }
                        )
                    }

                    LedgerTab.BUDGETS -> {
                        BudgetsScreen(
                            state = state,
                            onAddBudgetClick = { showAddBudgetDialog = true },
                            onDeleteBudget = { viewModel.deleteBudget(it) }
                        )
                    }
                }
            }
        }
    }

    // Dialog: Add Transaction
    if (showAddTransactionDialog) {
        AddTransactionDialog(
            currency = state.selectedCurrency,
            onDismiss = { showAddTransactionDialog = false },
            onConfirm = { title, amount, type, category, scope, familyMember, wallet, note ->
                viewModel.addTransaction(
                    title = title,
                    amount = amount,
                    type = type,
                    category = category,
                    scope = scope,
                    familyMember = familyMember,
                    wallet = wallet,
                    note = note
                )
                showAddTransactionDialog = false
            }
        )
    }

    // Dialog: Add Debt
    if (showAddDebtDialog) {
        AddDebtDialog(
            currency = state.selectedCurrency,
            onDismiss = { showAddDebtDialog = false },
            onConfirm = { personName, type, totalAmount, paidAmount, note ->
                viewModel.addDebt(
                    personName = personName,
                    type = type,
                    totalAmount = totalAmount,
                    paidAmount = paidAmount,
                    note = note
                )
                showAddDebtDialog = false
            }
        )
    }

    // Dialog: Record Debt Payment
    debtForPayment?.let { debt ->
        RecordPaymentDialog(
            debt = debt,
            currency = state.selectedCurrency,
            onDismiss = { debtForPayment = null },
            onConfirm = { paymentAmount ->
                viewModel.recordDebtPayment(debt, paymentAmount)
                debtForPayment = null
            }
        )
    }

    // Dialog: Add Budget
    if (showAddBudgetDialog) {
        AddBudgetDialog(
            currency = state.selectedCurrency,
            onDismiss = { showAddBudgetDialog = false },
            onConfirm = { category, limitAmount, scope ->
                viewModel.saveBudget(
                    category = category,
                    limitAmount = limitAmount,
                    scope = scope
                )
                showAddBudgetDialog = false
            }
        )
    }

    // Dialog: Currency Selector
    if (showCurrencyDialog) {
        CurrencyDialog(
            selectedCurrency = state.selectedCurrency,
            onDismiss = { showCurrencyDialog = false },
            onSelect = { newCurrency ->
                viewModel.setCurrency(newCurrency)
            }
        )
    }
}
