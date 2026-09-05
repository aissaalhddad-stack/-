package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // "EXPENSE" or "INCOME"
    val category: String, // e.g. "بقالة ومؤونة", "سكن وفواتير", "مواصلات", etc.
    val scope: String, // "PERSONAL" or "FAMILY"
    val familyMember: String = "أنا", // "أنا", "الزوج/الزوجة", "الأبناء", "مشترك"
    val wallet: String = "كاش", // "نقد (كاش)", "حساب بنكي", "بطاقة ائتمان", "محفظة رقمية"
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
