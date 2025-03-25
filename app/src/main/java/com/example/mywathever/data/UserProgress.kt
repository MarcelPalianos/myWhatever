package com.example.mywathever.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress (
    @PrimaryKey val id: Int = 1,
    val xp: Int = 0,
    val level: Int = 1
)