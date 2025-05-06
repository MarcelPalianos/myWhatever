package com.example.mywathever

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel()
) {
    val goalsList by viewModel.goals.collectAsState()
    val progress by viewModel.progress.collectAsState()

    Scaffold(
        containerColor = Color.DarkGray,
        topBar = { TopAppBar(title = { Text("My Goals") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            // Header: Level on left, two columns for stats on right.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Level ${progress.level}",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    modifier = Modifier.weight(2f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Agility: ${progress.speed}", color = Color.White, fontSize = 16.sp)
                        Text(text = "Intelligence: ${progress.intelligence}", color = Color.White, fontSize = 16.sp)
                        Text(text = "Wisdom: ${progress.wisdom}", color = Color.White, fontSize = 16.sp)
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Health: ${progress.health}", color = Color.White, fontSize = 16.sp)
                        Text(text = "Strength: ${progress.strength}", color = Color.White, fontSize = 16.sp)
                        Text(text = "Charisma: ${progress.charisma}", color = Color.White, fontSize = 16.sp)
                    }
                }
            }

            // XP progress indicator.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { (progress.xp % 10) / 10f },
                    modifier = Modifier
                        .width(200.dp)
                        .height(8.dp),
                    color = Color.Green,
                    trackColor = Color.Gray,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${progress.xp} XP",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // "New Goal" button.
            Button(onClick = { navController.navigate("createGoal") }) {
                Text("New Goal")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of goals.
            LazyColumn {
                items(goalsList) { goal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("goalSettings/${goal.id}") }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.scale(2f)) {
                            Checkbox(
                                checked = goal.isCompleted,
                                onCheckedChange = { viewModel.toggleGoalCompletion(goal) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.Gray,
                                    uncheckedColor = Color.White,
                                    checkmarkColor = Color.White
                                ),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = goal.text,
                                fontSize = 18.sp,
                                color = if (goal.isCompleted) Color.LightGray else Color.White,
                                style = if (goal.isCompleted)
                                    LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough)
                                else LocalTextStyle.current
                            )
                            Text(
                                text = "${goal.priority} XP - ${goal.type}",
                                fontSize = 12.sp,
                                color = Color.Green
                            )
                        }
                    }
                }
            }

        }
    }
}
