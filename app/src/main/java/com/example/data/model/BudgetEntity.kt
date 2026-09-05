package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val limitAmount: Double,
    val scope: String = "ALL", // "ALL", "FAMILY", "PERSONAL"
    val monthYear: String // e.g. "2026-09"
)
