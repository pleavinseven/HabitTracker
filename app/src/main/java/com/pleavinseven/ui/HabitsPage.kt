package com.pleavinseven.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.pleavinseven.R
import com.pleavinseven.utils.Utils
import com.pleavinseven.viewmodels.MainViewModel
import kotlinx.coroutines.launch

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
        HabitLazyGrid(viewModel = viewModel, navController = navController)
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
                contentDescription = stringResource(id = R.string.confirm)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitLazyGrid(viewModel: MainViewModel, navController: NavController) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), content = {
        items(viewModel.habitList.size) { item ->
            val currentHabit = viewModel.habitList[item]
            val habit = viewModel.getHabitFromId(currentHabit.id)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp, 12.dp, 8.dp, 0.dp)
                        .aspectRatio(1f)
                        .combinedClickable(onClick = {
                            navController.navigate(
                                "CounterPage/${currentHabit.id}"
                            )
                            viewModel.getTimeLogs(currentHabit.name)
                        }, onLongClick = {
                            viewModel.onHabitLongClick(habit)
                        }),
                    shape = CircleShape,
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currentHabit.count.toString(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.displayLarge,
                        )
                    }
                }
                Text(
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 4.dp),
                    text = currentHabit.name,
                    textAlign = TextAlign.Center,
                    style = if (currentHabit.name.length < 10) {
                        MaterialTheme.typography.displaySmall
                    } else {
                        MaterialTheme.typography.titleLarge
                    },
                )
            }
        }
    })
}


@Composable
fun AddHabitPopUp(viewModel: MainViewModel, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    var habitName by remember {
        mutableStateOf("")
    }
    var habitGoal by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    Popup(alignment = Alignment.Center, properties = PopupProperties(
        focusable = true,
        dismissOnClickOutside = true,
        dismissOnBackPress = true,
    ), onDismissRequest = { onDismiss() }) {
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
                        modifier = Modifier.padding(8.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    )
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(1f)
                    ) {
                        Button(onClick = {
                            if (!viewModel.createHabitClicked(habitName)) {
                                scope.launch {
                                    Utils.showToastShort(context, R.string.habit_already_exists)
                                }
                            } else {
                                onDismiss()
                            }
                        }) {
                            Text(text = stringResource(id = R.string.create_habit))
                        }
                    }
                }
            }
        }
    }
}