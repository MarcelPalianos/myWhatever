package com.example.mywathever.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {
    // Insert a shopping item into the database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItemEntity)

    // Get all items for a specific shopping goal.
    @Query("SELECT * FROM shopping_item_table WHERE goalId = :goalId")
    fun getItemsForGoal(goalId: Int): Flow<List<ShoppingItemEntity>>

    // Query for suggestions based on the item name entered by the user.
    // The query uses SQL wildcards so that as the user types part of an item's name,
    // matching records from previous shopping items (which include their price) are returned.
    @Query("SELECT * FROM shopping_item_table WHERE itemName LIKE :query ORDER BY id DESC")
    fun searchItems(query: String): Flow<List<ShoppingItemEntity>>
}
