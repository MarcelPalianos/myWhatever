package com.example.mywathever

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                        // Check mark
                        Box(modifier = Modifier.scale(2f)) {
                            Checkbox(
                                checked = goal.isCompleted,
                                onCheckedChange = { viewModel.toggleGoalCompletion(goal) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.Gray,
                                    uncheckedColor = Color.White,
                                    checkmarkColor = Color.White
                                ),
                                modifier = Modifier.size(32.dp) // Optional: Adjust size
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))

                        // Column for goal name and XP value
                        Column {
                            Text(
                                text = goal.text,
                                fontSize = 18.sp,
                                color = if (goal.isCompleted) Color.LightGray else Color.White,
                                style = if (goal.isCompleted) {
                                    LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough)
                                } else {
                                    LocalTextStyle.current
                                }
                            )
                            Text(
                                text = "${goal.priority} XP",
                                fontSize = 12.sp,
                                color = Color.Green
                            )
                        }
                    }
                }
            }

        }
    }

    if (showDialog) {
        var expanded by remember { mutableStateOf(false) }
        var selectedValue by remember { mutableStateOf("1") } // default selection
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New Goal") },
            text = {
                Column {
                    TextField(
                        value = newGoalText,
                        onValueChange = { newGoalText = it },
                        placeholder = { Text("Enter a new goal") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Priority: $selectedValue")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            (1..10).forEach { value ->
                                DropdownMenuItem(onClick = {
                                    selectedValue = value.toString()
                                    expanded = false
                                },
                                    text = { Text(text = value.toString()) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newGoalText.isNotBlank()) {
                            // Pass the priority as an integer to the ViewModel
                            viewModel.addGoal(newGoalText, selectedValue.toInt())
                            newGoalText = ""
                        }
                        showDialog = false
                    }
                ) {
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
