package com.pleavinseven.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.pleavinseven.R
import com.pleavinseven.model.entities.Habit
import com.pleavinseven.utils.Utils
import com.pleavinseven.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun HabitsPage(viewModel: MainViewModel, navController: NavController) {
    var showPopupWindow by remember {
        mutableStateOf((false))
    }
    Box(
        modifier = Modifier.fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
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
                imageVector = Icons.Rounded.Add,
                contentDescription = stringResource(id = R.string.confirm),
                modifier = Modifier.size(40.dp)
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
            var showDeleteDialog by remember {
                mutableStateOf((false))
            }
            if (showDeleteDialog) {
                DeleteHabitDialog(viewModel, currentHabit) { showDeleteDialog = false }
            }
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
                            showDeleteDialog = true
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
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface {
            Column {
                OutlinedTextField(
                    value = habitName,
                    label = { Text("Habit Name") },
                    onValueChange = { habitName = it.take(14) },
                    textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp),
                    maxLines = 1,
                    modifier = Modifier.padding(8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                )
                OutlinedTextField(
                    value = habitGoal,
                    label = { Text("Daily Goal") },
                    onValueChange = { habitGoal = it.take(4) },
                    textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp),
                    maxLines = 1,
                    modifier = Modifier.padding(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = stringResource(id = R.string.cancel),
                            modifier = Modifier
                                .size(40.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            val habitGoalInt = if (habitGoal == "") {
                                null
                            } else {
                                habitGoal.toInt()
                            }
                            if (!viewModel.createHabitClicked(habitName, habitGoalInt)) {
                                scope.launch {
                                    Utils.showToastShort(context, R.string.habit_already_exists)
                                }
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = stringResource(id = R.string.confirm),
                            modifier = Modifier
                                .size(40.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteHabitDialog(viewModel: MainViewModel, habit: Habit, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            modifier = Modifier.clip(RoundedCornerShape(24.dp)),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = R.string.delete_habit),
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = stringResource(id = R.string.cancel),
                            modifier = Modifier
                                .size(40.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.onHabitConfirmDeleteClick(habit)
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = stringResource(id = R.string.confirm),
                            modifier = Modifier
                                .size(40.dp)
                        )
                    }
                }
            }
        }
    }
}