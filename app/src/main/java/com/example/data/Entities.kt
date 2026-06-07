package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commodities")
data class Commodity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val wholesaleUnit: String, // e.g. "50kg Bag", "25L Jerrican"
    val microUnit: String,     // e.g. "Cup", "Derica", "Sachet", "Tuber"
    val conversionFactor: Double, // Number of microUnits in 1 wholesaleUnit (e.g. 64 cups in a bag)
    val description: String
)

@Entity(tableName = "market_prices")
data class MarketPrice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val commodityId: Int,
    val marketLocation: String, // e.g. "Mile 12 (Mainland)", "Isale Eko (Island)"
    val wholesalePrice: Double, // Wholesale price in NGN
    val retailPrice: Double,    // Custom retail or auto-calculated (wholesalePrice / conversionFactor)
    val lastUpdated: Long = System.currentTimeMillis(),
    val updatedBy: String = "System" // "Initial Import", "Admin", "SMS Agent"
)

@Entity(tableName = "price_history")
data class PriceHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val commodityId: Int,
    val marketLocation: String,
    val wholesalePrice: Double,
    val retailPrice: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "price_alerts")
data class PriceAlert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val commodityId: Int,
    val marketLocation: String,
    val targetPrice: Double, // Target retail or wholesale price to monitor
    val isAbove: Boolean,     // True to trigger when price is above, false when below
    val isTriggered: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "subscription_state")
data class SubscriptionState(
    @PrimaryKey val id: Int = 1, // Single-user local setting
    val tier: String = "FREE", // "FREE" or "PLUS"
    val status: String = "inactive", // "active", "trial", "expired"
    val trialStartDate: Long? = null,
    val expiryDate: Long? = null,
    val dailySmsAlertEnabled: Boolean = false,
    val phoneNumber: String = ""
)

@Entity(tableName = "user_feedback")
data class UserFeedback(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val feedbackType: String, // "Inaccuracy" or "Suggestion"
    val commodityId: Int?, // Nullable if suggesting a entirely new commodity
    val commodityName: String,
    val marketLocation: String,
    val reportedWholesalePrice: Double,
    val reportedRetailPrice: Double,
    val message: String,
    val status: String = "Pending", // "Pending", "Integrated", "Dismissed"
    val timestamp: Long = System.currentTimeMillis()
)

