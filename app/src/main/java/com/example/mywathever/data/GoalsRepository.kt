package com.example.mywathever.data

import kotlinx.coroutines.flow.Flow

class GoalsRepository(private val goalDao: GoalDao) {
    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()

    suspend fun insert(goal: GoalEntity) {
        goalDao.insertGoal(goal)
    }

    suspend fun update(goal: GoalEntity) {
        goalDao.updateGoal(goal)
    }
}
