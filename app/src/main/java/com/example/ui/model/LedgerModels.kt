package com.example.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryInfo(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val isExpense: Boolean
)

object LedgerConstants {

    val expenseCategories = listOf(
        CategoryInfo("بقالة ومؤونة", Icons.Default.Fastfood, Color(0xFFE65100), true),
        CategoryInfo("سكن وفواتير", Icons.Default.Home, Color(0xFF1565C0), true),
        CategoryInfo("مواصلات وبنزين", Icons.Default.DirectionsCar, Color(0xFF0277BD), true),
        CategoryInfo("صحة وعلاج", Icons.Default.LocalHospital, Color(0xFFC2185B), true),
        CategoryInfo("تعليم وأبناء", Icons.Default.School, Color(0xFF6A1B9A), true),
        CategoryInfo("تسوق وملابس", Icons.Default.ShoppingBag, Color(0xFFAD1457), true),
        CategoryInfo("مطاعم وكافيهات", Icons.Default.Fastfood, Color(0xFFD84315), true),
        CategoryInfo("صدقة وهدايا", Icons.Default.CardGiftcard, Color(0xFF2E7D32), true),
        CategoryInfo("مصروف شخصي", Icons.Default.Face, Color(0xFF00838F), true),
        CategoryInfo("التزامات عائلية", Icons.Default.FamilyRestroom, Color(0xFF4527A0), true),
        CategoryInfo("مصاريف أخرى", Icons.Default.MoreHoriz, Color(0xFF546E7A), true)
    )

    val incomeCategories = listOf(
        CategoryInfo("راتب شهري", Icons.Default.Payments, Color(0xFF2E7D32), false),
        CategoryInfo("عمل إضافي / حر", Icons.Default.Work, Color(0xFF00695C), false),
        CategoryInfo("أرباح واستثمار", Icons.Default.TrendingUp, Color(0xFF1565C0), false),
        CategoryInfo("مكافأة وهدايا", Icons.Default.CardGiftcard, Color(0xFFF57F17), false),
        CategoryInfo("دخل آخر", Icons.Default.MonetizationOn, Color(0xFF558B2F), false)
    )

    val familyMembers = listOf(
        "أنا",
        "الزوج / الزوجة",
        "الأبناء",
        "الوالدان",
        "مشترك عائلي"
    )

    val wallets = listOf(
        "نقد (كاش)",
        "حساب بنكي",
        "بطاقة مدى / خصم",
        "بطاقة ائتمانية",
        "محفظة رقمية"
    )

    val currencies = listOf(
        "ر.س" to "ريال سعودي",
        "د.إ" to "درهم إماراتي",
        "د.ك" to "دينار كويتي",
        "ج.م" to "جنيه مصري",
        "د.أ" to "دينار أردني",
        "ر.ع" to "ريال عماني",
        "ر.ق" to "ريال قطري",
        "$" to "دولار أمريكي"
    )

    fun getCategoryInfo(name: String): CategoryInfo {
        return (expenseCategories + incomeCategories).find { it.name == name }
            ?: CategoryInfo(name, Icons.Default.AccountBalance, Color(0xFF546E7A), true)
    }
}
