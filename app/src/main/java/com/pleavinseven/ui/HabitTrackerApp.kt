package com.pleavinseven.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pleavinseven.MainViewModel
import com.pleavinseven.R

@Composable
fun HabitTrackerApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "HabitsPage") {
        composable("HabitsPage") {
            HabitsPage(viewModel = viewModel, navController)
        }
        composable("CounterPage") {
            CounterPage(viewModel = viewModel)
        }
    }
}

@Composable
fun CounterPage(viewModel: MainViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            modifier = Modifier.size(500.dp),
            onClick = {
                viewModel.onCountButtonClicked()
            },
        ) {
            Text(
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge,
                text = viewModel.count.toString()
            )
        }
    }
}

@Composable
fun HabitsPage(viewModel: MainViewModel, navController: NavController) {
    var showPopupWindow by remember {
        mutableStateOf((false))
    }
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        if (showPopupWindow) {
            AddHabitPopUp(viewModel) { showPopupWindow = false }
        }
        LazyVerticalGrid(columns = GridCells.Fixed(2), content = {
            items(viewModel.habitList.size) { item ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .aspectRatio(1f)
                        .fillMaxSize()
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center,
                ) {
                    OutlinedCard(
                        elevation = CardDefaults.cardElevation(6.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .clickable { navController.navigate("CounterPage") },
                        shape = RoundedCornerShape(35.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(4.dp, Color.Black),
                    ) {
                        Text(
                            text = viewModel.habitList[item].habitName,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.displayLarge,
                        )
                    }
                }
            }
        })
        FloatingActionButton(
            onClick = {
                showPopupWindow = !showPopupWindow
            },
            Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .clip(RoundedCornerShape(45.dp))
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircle,
                contentDescription = stringResource(id = R.string.add_habit_description)
            )
        }
    }
}

@Composable
fun AddHabitPopUp(viewModel: MainViewModel, onDismiss: () -> Unit) {
    Popup(alignment = Alignment.Center, properties = PopupProperties(
        focusable = true,
        dismissOnClickOutside = true,
        dismissOnBackPress = true,
    ), onDismissRequest = { onDismiss() }) {
        var habitName by remember {
            mutableStateOf("")
        }
        OutlinedCard(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier
                .size(250.dp)
                .background(Color.Transparent),
            shape = RoundedCornerShape(35.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.add_habit_name),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                    OutlinedTextField(
                        value = habitName,
                        onValueChange = { habitName = it.take(14) },
                        textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp),
                        maxLines = 1,
                        modifier = Modifier.padding(8.dp)
                    )
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(1f)
                    ) {
                        Button(onClick = { viewModel.createHabitClicked(habitName) }) {
                            Text(text = stringResource(id = R.string.create_habit))
                        }
                    }
                }
            }
        }
    }
}
