package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personName: String,
    val type: String, // "I_LENT" (دين لي على الآخرين) or "I_BORROWED" (دين علي للآخرين)
    val totalAmount: Double,
    val paidAmount: Double = 0.0,
    val dueDate: Long? = null,
    val note: String = "",
    val isSettled: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
