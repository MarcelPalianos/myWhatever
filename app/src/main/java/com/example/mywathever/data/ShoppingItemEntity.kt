package com.example.mywathever.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_item_table")
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val goalId: Int,       // Reference to a shopping goal in your GoalEntity table
    val itemName: String,  // Name of the item (e.g., "Milk")
    val amount: Int,       // Quantity needed (you can adjust the type as needed)
    val price: Double      // Price of the item
)
