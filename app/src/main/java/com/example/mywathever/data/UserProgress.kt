package com.example.mywathever.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress (
    @PrimaryKey val id: Int = 1,
    val xp: Int = 0,
    val level: Int = 1,
    val strength: Int = 1,
    val dexterity: Int = 1,
    val speed: Int = 1,
    val intelligence: Int = 1,
    val wisdom: Int = 1,
    val health: Int = 1,
    val charisma: Int = 1
)