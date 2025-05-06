package com.example.mywathever

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mywathever.data.AppDatabase
import com.example.mywathever.data.GoalEntity
import com.example.mywathever.data.ShoppingItemEntity
import com.example.mywathever.data.UserProgress
import com.example.mywathever.data.UserProgressDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val goalDao = db.goalDao()
    private val progressDao: UserProgressDao = db.userProgressDao()
    // New DAO for shopping items.
    private val shoppingItemDao = db.shoppingItemDao()

    // Existing flows
    val goals = MutableStateFlow<List<GoalEntity>>(emptyList())
    val progress = MutableStateFlow(UserProgress())

    // Flow for holding shopping item suggestions as the user types.
    val shoppingItemSuggestions = MutableStateFlow<List<ShoppingItemEntity>>(emptyList())

    init {
        // Observe goal changes and perform daily auto-reset for recurring goals.
        viewModelScope.launch {
            goalDao.getAllGoals().collect { storedGoals ->
                val todayStart = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                storedGoals.filter {
                    it.recurring && it.isCompleted && it.lastReset < todayStart
                }.forEach { goal ->
                    val updatedGoal = goal.copy(
                        isCompleted = false,
                        lastReset = todayStart,
                        xpDeductible = false  // Prevent deduction upon auto-reset.
                    )
                    goalDao.updateGoal(updatedGoal)
                }
                goals.value = storedGoals
            }
        }

        // Observe user progress changes.
        viewModelScope.launch {
            progressDao.getProgress().collect { storedProgress ->
                storedProgress?.let {
                    progress.value = it
                }
            }
        }
    }

    // Existing goal operations:
    fun addGoal(goalText: String, xp: Int, type: String, recurring: Boolean) {
        viewModelScope.launch {
            val newGoal = GoalEntity(
                text = goalText,
                priority = xp,
                type = type,
                recurring = recurring,
                lastReset = System.currentTimeMillis()
            )
            goalDao.insertGoal(newGoal)
        }
    }

    fun getGoalById(goalId: Int): GoalEntity? {
        return goals.value.firstOrNull { it.id == goalId }
    }

    fun updateGoal(updatedGoal: GoalEntity) {
        viewModelScope.launch {
            goalDao.updateGoal(updatedGoal)
        }
    }

    /**
     * Toggle goal completion.
     *
     * When a goal is manually checked, award XP and mark flags accordingly.
     * When a goal is manually unchecked and is still eligible for XP deduction,
     * subtract its XP. Auto-reset recurring goals have xpDeductible = false.
     */
    fun toggleGoalCompletion(goal: GoalEntity) {
        viewModelScope.launch {
            val newState = !goal.isCompleted
            if (newState && !goal.xpAwarded) {
                // Award XP for manual completion.
                val currentProgress = progress.value
                val newTotalXp = currentProgress.xp + goal.priority
                // Update stat based on goal type.
                val updatedProgress = when (goal.type.lowercase()) {
                    "agility" -> currentProgress.copy(
                        xp = newTotalXp,
                        level = newTotalXp / 10 + 1,
                        speed = currentProgress.speed + goal.priority
                    )
                    "intelligence" -> currentProgress.copy(
                        xp = newTotalXp,
                        level = newTotalXp / 10 + 1,
                        intelligence = currentProgress.intelligence + goal.priority
                    )
                    "wisdom" -> currentProgress.copy(
                        xp = newTotalXp,
                        level = newTotalXp / 10 + 1,
                        wisdom = currentProgress.wisdom + goal.priority
                    )
                    "health" -> currentProgress.copy(
                        xp = newTotalXp,
                        level = newTotalXp / 10 + 1,
                        health = currentProgress.health + goal.priority
                    )
                    "strength" -> currentProgress.copy(
                        xp = newTotalXp,
                        level = newTotalXp / 10 + 1,
                        strength = currentProgress.strength + goal.priority
                    )
                    "charisma" -> currentProgress.copy(
                        xp = newTotalXp,
                        level = newTotalXp / 10 + 1,
                        charisma = currentProgress.charisma + goal.priority
                    )
                    else -> currentProgress.copy(
                        xp = newTotalXp,
                        level = newTotalXp / 10 + 1
                    )
                }
                progressDao.updateProgress(updatedProgress)
                // Update the goal to mark XP awarded and eligible for manual deduction.
                val updatedGoal = goal.copy(
                    isCompleted = true,
                    xpAwarded = true,
                    xpDeductible = true
                )
                goalDao.updateGoal(updatedGoal)
            } else if (!newState && goal.xpAwarded) {
                // User manually unchecks a goal – if its XP is still deductible, deduct it.
                if (goal.xpDeductible) {
                    val currentProgress = progress.value
                    val newTotalXp = currentProgress.xp - goal.priority
                    val updatedProgress = when (goal.type.lowercase()) {
                        "agility" -> currentProgress.copy(
                            xp = newTotalXp,
                            level = newTotalXp / 10 + 1,
                            speed = currentProgress.speed - goal.priority
                        )
                        "intelligence" -> currentProgress.copy(
                            xp = newTotalXp,
                            level = newTotalXp / 10 + 1,
                            intelligence = currentProgress.intelligence - goal.priority
                        )
                        "wisdom" -> currentProgress.copy(
                            xp = newTotalXp,
                            level = newTotalXp / 10 + 1,
                            wisdom = currentProgress.wisdom - goal.priority
                        )
                        "health" -> currentProgress.copy(
                            xp = newTotalXp,
                            level = newTotalXp / 10 + 1,
                            health = currentProgress.health - goal.priority
                        )
                        "strength" -> currentProgress.copy(
                            xp = newTotalXp,
                            level = newTotalXp / 10 + 1,
                            strength = currentProgress.strength - goal.priority
                        )
                        "charisma" -> currentProgress.copy(
                            xp = newTotalXp,
                            level = newTotalXp / 10 + 1,
                            charisma = currentProgress.charisma - goal.priority
                        )
                        else -> currentProgress.copy(
                            xp = newTotalXp,
                            level = newTotalXp / 10 + 1
                        )
                    }
                    progressDao.updateProgress(updatedProgress)
                    // Reset flags so XP can be re-awarded later.
                    val updatedGoal = goal.copy(
                        isCompleted = false,
                        xpAwarded = false,
                        xpDeductible = false
                    )
                    goalDao.updateGoal(updatedGoal)
                } else {
                    // For goals auto-reset (xpDeductible = false), simply update state.
                    val updatedGoal = goal.copy(isCompleted = false)
                    goalDao.updateGoal(updatedGoal)
                }
            } else {
                // Fallback for other state changes.
                val updatedGoal = goal.copy(isCompleted = newState)
                goalDao.updateGoal(updatedGoal)
            }
        }
    }

    // -- Shopping Goal Operations --

    /**
     * Inserts a shopping item for a given shopping goal.
     */
    fun insertShoppingItem(goalId: Int, itemName: String, amount: Int, price: Double) {
        viewModelScope.launch {
            val newItem = ShoppingItemEntity(
                goalId = goalId,
                itemName = itemName,
                amount = amount,
                price = price
            )
            shoppingItemDao.insertItem(newItem)
        }
    }

    /**
     * Returns a Flow of shopping items for the specified shopping goal.
     */
    fun getShoppingItemsForGoal(goalId: Int): Flow<List<ShoppingItemEntity>> {
        return shoppingItemDao.getItemsForGoal(goalId)
    }

    /**
     * Searches previously added shopping items based on the input query.
     * This can be used to provide autocomplete suggestions as the user types.
     * For example, if the user types "Mil", and a previous item "Milk" exists with a price,
     * then that price can be automatically applied to the current list.
     */
    fun searchShoppingItems(query: String) {
        viewModelScope.launch {
            // Use SQL wildcards to match partial names.
            shoppingItemDao.searchItems("%${query}%").collect { suggestions ->
                shoppingItemSuggestions.value = suggestions
            }
        }
    }
}
