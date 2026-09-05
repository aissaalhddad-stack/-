package com.example.data.repository

import com.example.data.dao.LedgerDao
import com.example.data.model.BudgetEntity
import com.example.data.model.DebtEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

class LedgerRepository(private val dao: LedgerDao) {

    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val allDebts: Flow<List<DebtEntity>> = dao.getAllDebts()
    val allBudgets: Flow<List<BudgetEntity>> = dao.getAllBudgets()

    fun getTransactionsByScope(scope: String): Flow<List<TransactionEntity>> =
        dao.getTransactionsByScope(scope)

    fun getBudgetsForMonth(monthYear: String): Flow<List<BudgetEntity>> =
        dao.getBudgetsForMonth(monthYear)

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        dao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        dao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        dao.deleteTransaction(transaction)

    suspend fun deleteTransactionById(id: Long) =
        dao.deleteTransactionById(id)

    suspend fun getTransactionCount(): Int =
        dao.getTransactionCount()

    suspend fun insertDebt(debt: DebtEntity): Long =
        dao.insertDebt(debt)

    suspend fun updateDebt(debt: DebtEntity) =
        dao.updateDebt(debt)

    suspend fun deleteDebt(debt: DebtEntity) =
        dao.deleteDebt(debt)

    suspend fun deleteDebtById(id: Long) =
        dao.deleteDebtById(id)

    suspend fun insertBudget(budget: BudgetEntity): Long =
        dao.insertBudget(budget)

    suspend fun updateBudget(budget: BudgetEntity) =
        dao.updateBudget(budget)

    suspend fun deleteBudget(budget: BudgetEntity) =
        dao.deleteBudget(budget)

    suspend fun deleteBudgetById(id: Long) =
        dao.deleteBudgetById(id)
}
