package com.example.mywathever.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal_table")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val priority: Int,  // XP value
    val type: String,   // The stat this goal contributes to (e.g. "Agility", etc.)
    val isCompleted: Boolean = false,
    val recurring: Boolean = false,
    // For recurring goals, lastReset records the last day (as epoch ms) the goal was reset.
    val lastReset: Long = System.currentTimeMillis(),
    // Indicates that XP was already awarded when the goal was completed.
    // Once awarded, XP stays unless a manual reversal occurs.
    val xpAwarded: Boolean = false,
    // Indicates if the goal’s XP is still eligible for deduction if the goal is unchecked manually.
    // This should remain true until a manual reversal; for recurring goals, the auto-reset will set this to false.
    val xpDeductible: Boolean = false
)
