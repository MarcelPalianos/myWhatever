package com.example.mywathever.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal_table")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val priority: Int = 1,
    val isCompleted: Boolean = false
)