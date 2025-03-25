package com.example.mywathever

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val goalsList by viewModel.goals.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newGoalText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.DarkGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            // Existing button to create a new goal
            Button(onClick = { showDialog = true }) {
                Text("New Goal")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display the list of goals
            LazyColumn {
                items(goalsList) { goal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = goal.isCompleted,
                            onCheckedChange = { viewModel.toggleGoalCompletion(goal) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.Gray,      // Color when the box is checked
                                uncheckedColor = Color.White,    // Color for the unchecked border
                                checkmarkColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = goal.text,
                            color = if (goal.isCompleted) Color.LightGray else Color.White,
                            style = if (goal.isCompleted) {
                                LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough)
                            } else {
                                LocalTextStyle.current
                            }
                        )
                    }
                }
            }
        }
    }

    // Existing dialog code for adding a new goal
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New Goal") },
            text = {
                TextField(
                    value = newGoalText,
                    onValueChange = { newGoalText = it },
                    placeholder = { Text("Enter a new goal") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newGoalText.isNotBlank()) {
                        viewModel.addGoal(newGoalText)
                        newGoalText = ""
                    }
                    showDialog = false
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
