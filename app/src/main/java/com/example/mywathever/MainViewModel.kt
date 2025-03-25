package com.example.mywathever

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mywathever.data.AppDatabase
import com.example.mywathever.data.GoalEntity
import com.example.mywathever.data.UserProgress
import com.example.mywathever.data.GoalsRepository
import com.example.mywathever.data.UserProgressDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val goalsRepository = GoalsRepository(db.goalDao())
    private val progressDao: UserProgressDao = db.userProgressDao()

    private val _goals = MutableStateFlow<List<GoalEntity>>(emptyList())
    val goals: StateFlow<List<GoalEntity>> = _goals

    private val _progress = MutableStateFlow<UserProgress>(UserProgress())
    val progress: StateFlow<UserProgress> = _progress

    init {
        viewModelScope.launch {
            // Collect goals and update progress whenever the goals change.
            goalsRepository.allGoals.collect { storedGoals ->
                _goals.value = storedGoals
                updateProgress(storedGoals)
            }
        }
        // Listen to progress changes (optional, for UI updates).
        viewModelScope.launch {
            progressDao.getProgress().collect { storedProgress ->
                storedProgress?.let { _progress.value = it }
            }
        }
    }

    fun addGoal(goalText: String, priority: Int) {
        viewModelScope.launch {
            goalsRepository.insert(GoalEntity(text = goalText, priority = priority))
            // The flow collector will update progress automatically.
        }
    }

    fun toggleGoalCompletion(goal: GoalEntity) {
        viewModelScope.launch {
            val updatedGoal = goal.copy(isCompleted = !goal.isCompleted)
            goalsRepository.update(updatedGoal)
            // The flow collector will update progress automatically.
        }
    }

    private suspend fun updateProgress(goalsList: List<GoalEntity>) {
        val totalXp = goalsList.filter { it.isCompleted }.sumOf { it.priority }
        val level = totalXp / 10 + 1
        val newProgress = UserProgress(id = 1, xp = totalXp, level = level)
        progressDao.updateProgress(newProgress)
    }
}
