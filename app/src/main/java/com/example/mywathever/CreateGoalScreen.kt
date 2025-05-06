package com.example.mywathever

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel()
) {
    var newGoalText by remember { mutableStateOf("") }
    var xpValue by remember { mutableFloatStateOf(1f) }
    val types = listOf("Agility", "Intelligence", "Wisdom", "Health", "Strength", "Charisma")
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(types.first()) }
    var recurring by remember { mutableStateOf(false) } // New state for recurring flag

    Scaffold(
        topBar = { TopAppBar(title = { Text("Create New Goal") }) },
        containerColor = Color.DarkGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TextField(
                value = newGoalText,
                onValueChange = { newGoalText = it },
                label = { Text("Goal") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "XP: ${xpValue.toInt()}",
                color = Color.White,
                fontSize = 16.sp
            )
            Slider(
                value = xpValue,
                onValueChange = { xpValue = it },
                valueRange = 1f..10f,
                steps = 8,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Dropdown for selecting the goal type.
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Type: $selectedType")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    types.forEach { type ->
                        DropdownMenuItem(
                            onClick = {
                                selectedType = type
                                expanded = false
                            },
                            text = { Text(text = type) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Checkbox to mark the goal as recurring.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = recurring,
                    onCheckedChange = { recurring = it },
                    colors = androidx.compose.material3.CheckboxDefaults.colors(
                        checkedColor = Color.Green,
                        uncheckedColor = Color.White,
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Recurring Goal", color = Color.White, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (newGoalText.isNotBlank()) {
                        // When adding a new goal, set the recurring flag accordingly.
                        viewModel.addGoal(newGoalText, xpValue.toInt(), selectedType, recurring)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create")
            }
        }
    }
}
