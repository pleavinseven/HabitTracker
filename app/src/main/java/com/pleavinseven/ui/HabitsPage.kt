package com.pleavinseven.ui

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.pleavinseven.R
import com.pleavinseven.composables.MyTheme
import com.pleavinseven.model.entities.Habit
import com.pleavinseven.utils.Utils
import com.pleavinseven.viewmodels.HabitViewModel
import kotlinx.coroutines.launch

@Composable
fun HabitsPage(
    habitViewModel: HabitViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val showDelete by habitViewModel.showDeleteIcon.collectAsState()
    var showPopupWindow by remember { mutableStateOf(false) }
    val deleteHabitList = habitViewModel.deleteHabitList

    fun onDeleteIconClick() {
        for (habit in deleteHabitList) {
            habitViewModel.onHabitConfirmDeleteClick(habit)
        }
        habitViewModel.deleteHabitList.clear()
        Utils.vibrate(context, Utils.VIBE_EFFECT_DOUBLE_CLICK)
    }

    MyTheme(
        navController = navController,
        hasTopBar = true,
        topBarActions = {
            if (showDelete) {
                DeleteIconDialog({ onDeleteIconClick() }) {
                    habitViewModel.setShowDelete()
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showPopupWindow = !showPopupWindow
                },
                modifier = Modifier.clip(RoundedCornerShape(45.dp))
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(id = R.string.confirm),
                    modifier = Modifier.size(40.dp)
                )
            }
        },
    ) {
        if (showPopupWindow) {
            AddHabitPopUp(habitViewModel, context) { showPopupWindow = false }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            content = {
                items(habitViewModel.habitList.size) { item ->
                    val currentHabit = habitViewModel.habitList[item]
                    val habitColor = remember { mutableIntStateOf(R.color.white) }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HabitCard(
                            onClick = {
                                habitViewModel.setCurrentHabit(currentHabit)
                                navController.navigate(
                                    "CounterPage"
                                )
                            },
                            onLongClick = {
                                habitViewModel.setHabitDeleteList(currentHabit)
                                habitColor.intValue = habitViewModel.setCardColor(currentHabit)
                                Utils.vibrate(context, Utils.VIBE_EFFECT_DOUBLE_CLICK)
                            },
                            currentHabit = currentHabit,
                            habitColor = colorResource(id = habitColor.intValue)
                        )
                        Text(
                            modifier = Modifier.padding(
                                0.dp,
                                top = if (currentHabit.name.length < 10) 0.dp else 4.dp,
                                0.dp,
                                4.dp
                            ),
                            text = currentHabit.name,
                            textAlign = TextAlign.Center,
                            style = TextStyle(fontSize = if (currentHabit.name.length < 10) 36.sp else 32.sp)
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitCard(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    currentHabit: Habit,
    habitColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 12.dp, 8.dp, 0.dp)
            .aspectRatio(1f)
            .combinedClickable(
                onClick = {
                    onClick()
                }, onLongClick = {
                    onLongClick()
                }
            ),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = habitColor
        )
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
}

@Composable
fun AddHabitPopUp(
    habitViewModel: HabitViewModel,
    context: Context,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var habitName by remember { mutableStateOf("") }
    var habitGoal by remember { mutableStateOf("") }
    val habitRepeat by remember { mutableLongStateOf(1) }
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card {
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
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { onDismiss() }, modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = stringResource(id = R.string.cancel),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            val habitGoalInt = if (habitGoal == "") {
                                null
                            } else {
                                habitGoal.toInt()
                            }
                            if (!habitViewModel.createHabitClicked(
                                    habitName, habitGoalInt, habitRepeat
                                )
                            ) {
                                scope.launch {
                                    Utils.showToastShort(context, R.string.habit_already_exists)
                                }
                            } else {
                                Utils.vibrate(context, Utils.VIBE_EFFECT_DOUBLE_CLICK)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = stringResource(id = R.string.confirm),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteIconDialog(
    onDismiss: () -> Unit,
    onDeleteIconClick: () -> Unit
) {
    IconButton(
        onClick = {
            onDeleteIconClick()
            onDismiss()
        },
        modifier = Modifier
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = "Delete",
            tint = Color.Red
        )
    }
}

@Preview
@Composable
private fun HabitCardPreview() {
    HabitCard(
        onClick = {},
        onLongClick = { },
        currentHabit = Habit(0, "name", 1, null, 0),
        habitColor = Color.White
    )
}