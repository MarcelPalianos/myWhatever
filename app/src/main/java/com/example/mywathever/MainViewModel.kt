package com.example.mywathever

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mywathever.data.AppDatabase
import com.example.mywathever.data.GoalsRepository
import com.example.mywathever.data.GoalEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GoalsRepository

    // Using a StateFlow to expose the list of goals
    private val _goals = MutableStateFlow<List<GoalEntity>>(emptyList())
    val goals: StateFlow<List<GoalEntity>> = _goals

    init {
        val goalDao = AppDatabase.getDatabase(application).goalDao()
        repository = GoalsRepository(goalDao)
        // Collect goals from the repository and update our StateFlow
        viewModelScope.launch {
            repository.allGoals.collect { storedGoals ->
                _goals.value = storedGoals
            }
        }
    }

    // Add a goal and persist it
    fun addGoal(goalText: String, priority: Int) {
        viewModelScope.launch {
            repository.insert(GoalEntity(text = goalText, priority = priority))
        }
    }

    fun toggleGoalCompletion(goal : GoalEntity){
        viewModelScope.launch{
            val updateGoal = goal.copy(isCompleted = !goal.isCompleted)
            repository.update(updateGoal)
        }
    }
}
