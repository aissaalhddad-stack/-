package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.DebtEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.LedgerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class LedgerTab(val title: String) {
    OVERVIEW("الرئيسية"),
    TRANSACTIONS("المعاملات"),
    DEBTS("الديون والالتزامات"),
    BUDGETS("الميزانيات")
}

data class CategoryBreakdown(
    val category: String,
    val amount: Double,
    val percentage: Float,
    val isExpense: Boolean
)

data class LedgerUiState(
    val selectedTab: LedgerTab = LedgerTab.OVERVIEW,
    val selectedScope: String = "ALL", // "ALL", "PERSONAL", "FAMILY"
    val selectedCurrency: String = "ر.س",
    val searchQuery: String = "",
    val selectedCategoryFilter: String? = null,
    val selectedMemberFilter: String? = null,
    val selectedPeriod: String = "THIS_MONTH", // "THIS_MONTH", "TODAY", "ALL"
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netBalance: Double = 0.0,
    val personalExpense: Double = 0.0,
    val familyExpense: Double = 0.0,
    val debtsToReceive: Double = 0.0,
    val debtsToPay: Double = 0.0,
    val categoryBreakdowns: List<CategoryBreakdown> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val allTransactions: List<TransactionEntity> = emptyList(),
    val allDebts: List<DebtEntity> = emptyList(),
    val allBudgets: List<BudgetEntity> = emptyList()
)

class LedgerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LedgerRepository

    private val _selectedTab = MutableStateFlow(LedgerTab.OVERVIEW)
    private val _selectedScope = MutableStateFlow("ALL") // ALL, PERSONAL, FAMILY
    private val _selectedCurrency = MutableStateFlow("ر.س")
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    private val _selectedMemberFilter = MutableStateFlow<String?>(null)
    private val _selectedPeriod = MutableStateFlow("THIS_MONTH")

    init {
        val db = AppDatabase.getDatabase(application)
        repository = LedgerRepository(db.ledgerDao())
        seedInitialDataIfEmpty()
    }

    private fun seedInitialDataIfEmpty() {
        viewModelScope.launch {
            if (repository.getTransactionCount() == 0) {
                val now = System.currentTimeMillis()
                val oneDay = 24 * 60 * 60 * 1000L

                // Starter family & personal transactions
                repository.insertTransaction(
                    TransactionEntity(
                        title = "الراتب الشهري",
                        amount = 12500.0,
                        type = "INCOME",
                        category = "راتب شهري",
                        scope = "PERSONAL",
                        familyMember = "أنا",
                        wallet = "حساب بنكي",
                        timestamp = now - 5 * oneDay,
                        note = "إيداع راتب هذا الشهر"
                    )
                )
                repository.insertTransaction(
                    TransactionEntity(
                        title = "مقاضي ومؤونة البيت الشهرية",
                        amount = 1450.0,
                        type = "EXPENSE",
                        category = "بقالة ومؤونة",
                        scope = "FAMILY",
                        familyMember = "الزوج / الزوجة",
                        wallet = "بطاقة مدى / خصم",
                        timestamp = now - 3 * oneDay,
                        note = "مشتريات السوبرماركت للعائلة"
                    )
                )
                repository.insertTransaction(
                    TransactionEntity(
                        title = "فاتورة الكهرباء والإنترنت",
                        amount = 480.0,
                        type = "EXPENSE",
                        category = "سكن وفواتير",
                        scope = "FAMILY",
                        familyMember = "أنا",
                        wallet = "حساب بنكي",
                        timestamp = now - 2 * oneDay,
                        note = "سداد فواتير المنزل"
                    )
                )
                repository.insertTransaction(
                    TransactionEntity(
                        title = "بنزين سيارة العائلة",
                        amount = 180.0,
                        type = "EXPENSE",
                        category = "مواصلات وبنزين",
                        scope = "FAMILY",
                        familyMember = "أنا",
                        wallet = "بطاقة مدى / خصم",
                        timestamp = now - oneDay,
                        note = "تعبئة خزان الوقود"
                    )
                )
                repository.insertTransaction(
                    TransactionEntity(
                        title = "مشروع عمل حر إضافي",
                        amount = 1800.0,
                        type = "INCOME",
                        category = "عمل إضافي / حر",
                        scope = "PERSONAL",
                        familyMember = "أنا",
                        wallet = "حساب بنكي",
                        timestamp = now - oneDay,
                        note = "أتعاب تصميم واستشارة"
                    )
                )
                repository.insertTransaction(
                    TransactionEntity(
                        title = "عشاء عائلي نهاية الأسبوع",
                        amount = 260.0,
                        type = "EXPENSE",
                        category = "مطاعم وكافيهات",
                        scope = "FAMILY",
                        familyMember = "مشترك عائلي",
                        wallet = "نقد (كاش)",
                        timestamp = now,
                        note = "مطعم العائلة"
                    )
                )

                // Starter Debts
                repository.insertDebt(
                    DebtEntity(
                        personName = "صالح (زميل العمل)",
                        type = "I_LENT",
                        totalAmount = 500.0,
                        paidAmount = 200.0,
                        dueDate = now + 10 * oneDay,
                        note = "سلفة مؤقتة"
                    )
                )
                repository.insertDebt(
                    DebtEntity(
                        personName = "ورشة صيانة التكييف",
                        type = "I_BORROWED",
                        totalAmount = 350.0,
                        paidAmount = 0.0,
                        dueDate = now + 7 * oneDay,
                        note = "متبقي إصلاح مكيف الصالة"
                    )
                )

                // Starter Budget
                val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
                repository.insertBudget(
                    BudgetEntity(
                        category = "بقالة ومؤونة",
                        limitAmount = 2500.0,
                        scope = "FAMILY",
                        monthYear = currentMonth
                    )
                )
                repository.insertBudget(
                    BudgetEntity(
                        category = "سكن وفواتير",
                        limitAmount = 1000.0,
                        scope = "FAMILY",
                        monthYear = currentMonth
                    )
                )
                repository.insertBudget(
                    BudgetEntity(
                        category = "مطاعم وكافيهات",
                        limitAmount = 800.0,
                        scope = "ALL",
                        monthYear = currentMonth
                    )
                )
            }
        }
    }

    val uiState: StateFlow<LedgerUiState> = combine(
        repository.allTransactions,
        repository.allDebts,
        repository.allBudgets,
        _selectedTab,
        _selectedScope,
        _selectedCurrency,
        _searchQuery,
        _selectedCategoryFilter,
        _selectedMemberFilter,
        _selectedPeriod
    ) { params ->
        @Suppress("UNCHECKED_CAST")
        val transactions = params[0] as List<TransactionEntity>
        @Suppress("UNCHECKED_CAST")
        val debts = params[1] as List<DebtEntity>
        @Suppress("UNCHECKED_CAST")
        val budgets = params[2] as List<BudgetEntity>
        val currentTab = params[3] as LedgerTab
        val scope = params[4] as String
        val currency = params[5] as String
        val search = params[6] as String
        val catFilter = params[7] as String?
        val memberFilter = params[8] as String?
        val period = params[9] as String

        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        val currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        // Filter transactions according to selected scope and period
        val periodTransactions = transactions.filter { tx ->
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
            when (period) {
                "TODAY" -> txCal.get(Calendar.YEAR) == currentYear && txCal.get(Calendar.DAY_OF_YEAR) == currentDayOfYear
                "THIS_MONTH" -> txCal.get(Calendar.YEAR) == currentYear && txCal.get(Calendar.MONTH) == currentMonth
                else -> true
            }
        }

        val scopeFiltered = if (scope == "ALL") periodTransactions else periodTransactions.filter { it.scope == scope }

        // Compute Financial Summary
        var totalInc = 0.0
        var totalExp = 0.0
        var personalExp = 0.0
        var familyExp = 0.0

        for (tx in scopeFiltered) {
            if (tx.type == "INCOME") {
                totalInc += tx.amount
            } else {
                totalExp += tx.amount
                if (tx.scope == "PERSONAL") personalExp += tx.amount
                if (tx.scope == "FAMILY") familyExp += tx.amount
            }
        }
        val net = totalInc - totalExp

        // Category breakdown for expenses
        val expenseByCategory = scopeFiltered.filter { it.type == "EXPENSE" }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val breakdowns = expenseByCategory.map { (cat, amt) ->
            val percentage = if (totalExp > 0) ((amt / totalExp) * 100).toFloat() else 0f
            CategoryBreakdown(cat, amt, percentage, true)
        }.sortedByDescending { it.amount }

        // Debts summary
        val debtsToReceive = debts.filter { it.type == "I_LENT" && !it.isSettled }
            .sumOf { it.totalAmount - it.paidAmount }
        val debtsToPay = debts.filter { it.type == "I_BORROWED" && !it.isSettled }
            .sumOf { it.totalAmount - it.paidAmount }

        // Detailed search / filters for Transactions screen
        val filteredList = transactions.filter { tx ->
            val matchesScope = (scope == "ALL") || (tx.scope == scope)
            val matchesSearch = search.isBlank() || tx.title.contains(search, ignoreCase = true) ||
                    tx.category.contains(search, ignoreCase = true) ||
                    tx.note.contains(search, ignoreCase = true) ||
                    tx.familyMember.contains(search, ignoreCase = true)
            val matchesCat = catFilter == null || tx.category == catFilter
            val matchesMember = memberFilter == null || tx.familyMember == memberFilter

            matchesScope && matchesSearch && matchesCat && matchesMember
        }

        LedgerUiState(
            selectedTab = currentTab,
            selectedScope = scope,
            selectedCurrency = currency,
            searchQuery = search,
            selectedCategoryFilter = catFilter,
            selectedMemberFilter = memberFilter,
            selectedPeriod = period,
            totalIncome = totalInc,
            totalExpense = totalExp,
            netBalance = net,
            personalExpense = personalExp,
            familyExpense = familyExp,
            debtsToReceive = debtsToReceive,
            debtsToPay = debtsToPay,
            categoryBreakdowns = breakdowns,
            filteredTransactions = filteredList,
            allTransactions = transactions,
            allDebts = debts,
            allBudgets = budgets
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LedgerUiState()
    )

    fun selectTab(tab: LedgerTab) {
        _selectedTab.value = tab
    }

    fun setScope(scope: String) {
        _selectedScope.value = scope
    }

    fun setCurrency(currency: String) {
        _selectedCurrency.value = currency
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategoryFilter.value = category
    }

    fun setMemberFilter(member: String?) {
        _selectedMemberFilter.value = member
    }

    fun setPeriod(period: String) {
        _selectedPeriod.value = period
    }

    fun addTransaction(
        title: String,
        amount: Double,
        type: String,
        category: String,
        scope: String,
        familyMember: String,
        wallet: String,
        note: String = "",
        timestamp: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    title = title,
                    amount = amount,
                    type = type,
                    category = category,
                    scope = scope,
                    familyMember = familyMember,
                    wallet = wallet,
                    timestamp = timestamp,
                    note = note
                )
            )
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransactionById(id)
        }
    }

    fun addDebt(
        personName: String,
        type: String,
        totalAmount: Double,
        paidAmount: Double = 0.0,
        dueDate: Long? = null,
        note: String = ""
    ) {
        viewModelScope.launch {
            repository.insertDebt(
                DebtEntity(
                    personName = personName,
                    type = type,
                    totalAmount = totalAmount,
                    paidAmount = paidAmount,
                    dueDate = dueDate,
                    note = note,
                    isSettled = paidAmount >= totalAmount
                )
            )
        }
    }

    fun recordDebtPayment(debt: DebtEntity, paymentAddition: Double) {
        viewModelScope.launch {
            val newPaid = (debt.paidAmount + paymentAddition).coerceAtMost(debt.totalAmount)
            val isNowSettled = newPaid >= debt.totalAmount
            repository.updateDebt(
                debt.copy(
                    paidAmount = newPaid,
                    isSettled = isNowSettled
                )
            )
        }
    }

    fun toggleDebtSettled(debt: DebtEntity) {
        viewModelScope.launch {
            val newSettled = !debt.isSettled
            val newPaid = if (newSettled) debt.totalAmount else 0.0
            repository.updateDebt(debt.copy(isSettled = newSettled, paidAmount = newPaid))
        }
    }

    fun deleteDebt(id: Long) {
        viewModelScope.launch {
            repository.deleteDebtById(id)
        }
    }

    fun saveBudget(category: String, limitAmount: Double, scope: String) {
        viewModelScope.launch {
            val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
            // Check if budget exists for this category & month
            val existing = uiState.value.allBudgets.find {
                it.category == category && it.monthYear == currentMonth
            }
            if (existing != null) {
                repository.updateBudget(existing.copy(limitAmount = limitAmount, scope = scope))
            } else {
                repository.insertBudget(
                    BudgetEntity(
                        category = category,
                        limitAmount = limitAmount,
                        scope = scope,
                        monthYear = currentMonth
                    )
                )
            }
        }
    }

    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            repository.deleteBudgetById(id)
        }
    }
}
