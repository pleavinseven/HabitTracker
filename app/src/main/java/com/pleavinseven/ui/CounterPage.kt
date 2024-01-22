package com.pleavinseven.ui

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.pleavinseven.R
import com.pleavinseven.model.entities.Habit
import com.pleavinseven.utils.Utils
import com.pleavinseven.viewmodels.HabitViewModel
import com.pleavinseven.viewmodels.TimeLogViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterPage(
    habitViewModel: HabitViewModel,
    timeLogViewModel: TimeLogViewModel,
    navController: NavController
) {
    val habit by habitViewModel.habitState.collectAsState()
    habitViewModel.setGoalCompletedColor(habit)
    val goalColor by habitViewModel.goalColorState.collectAsState()
    var count by remember { mutableIntStateOf(habit.count) }
    var showPopupWindow by remember { mutableStateOf(false) }
    val showGoal by remember { mutableStateOf(habit.goal != null) }
    val context = LocalContext.current
    val fontSize = if (habit.name.length < 10) 36.sp else 32.sp

    timeLogViewModel.getTimeLogs(habit.id)
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding(),
        topBar = {
            TopAppBar(title = {})
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showPopupWindow = !showPopupWindow
                }, Modifier.clip(RoundedCornerShape(45.dp))
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(id = R.string.edit_habit_description),
                    modifier = Modifier.size(40.dp)
                )
            }
        },
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { innerPadding ->
        if (showPopupWindow) {
            EditHabitDialog(habitViewModel, context, habit) { showPopupWindow = false }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = habit.name.uppercase(),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = fontSize),
            )
            // Counter
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp, 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        habitViewModel.onDecreaseButtonClicked(habit)
                        timeLogViewModel.removeLastTimeLog(habit)
                        Utils.vibrate(context, Utils.VIBE_EFFECT_CLICK)
                        count = habit.count
                        habitViewModel.setGoalCompletedColor(habit)
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .weight(0.5f),
                ) {
                    Icon(
                        modifier = Modifier.size(160.dp),
                        imageVector = Icons.Filled.Remove,
                        contentDescription = stringResource(id = R.string.count_minus_one)
                    )
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    shape = CircleShape,
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AnimatedContent(
                            targetState = count, transitionSpec = {
                                if (targetState > initialState) {
                                    // If the target number is larger, it slides up and fades in
                                    // while the initial number slides up and fades out.
                                    // else reverse
                                    (slideInVertically { height -> height } + fadeIn()).togetherWith(
                                        slideOutVertically { height -> -height } + fadeOut())
                                } else {
                                    (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                                        slideOutVertically { height -> height } + fadeOut())
                                }.using(
                                    SizeTransform(clip = false)
                                )
                            }, label = ""
                        ) { targetCount ->
                            val countFontSize = when (targetCount) {
                                in 0..9 -> 130.sp
                                in 10..99 -> 100.sp
                                in 100..999 -> 70.sp
                                else -> 50.sp
                            }
                            Text(text = targetCount.toString(), fontSize = countFontSize)
                        }
                    }
                }
                IconButton(
                    onClick = {
                        habitViewModel.onCountButtonClicked(habit)
                        timeLogViewModel.logTimeStampInDatabase(habit.id)
                        Utils.vibrate(context, Utils.VIBE_EFFECT_CLICK)
                        count = habit.count
                        habitViewModel.setGoalCompletedColor(habit)
                    }, modifier = Modifier
                        .weight(0.5f)
                        .size(60.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(160.dp),
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.count_plus_one)
                    )
                }
            }
            if (showGoal) {
                GoalCard(goalColor, habit.goal)
            }
        }
    }
}

@Composable
fun GoalCard(goalColor: Int, goal: Int?) {
    Card(
        modifier = Modifier
            .height(48.dp)
            .padding(start = 30.dp, end = 34.dp, bottom = 8.dp ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = goalColor))
                    .wrapContentHeight(),
                text = "Goal $goal",
                color = Color.Black,
                textAlign = TextAlign.Center,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
            )
        }
    }
}

@Composable
fun EditHabitDialog(
    habitViewModel: HabitViewModel,
    context: Context,
    habit: Habit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var editHabitName by remember {
        mutableStateOf(habit.name)
    }
    var editHabitGoal by remember {
        if (habit.goal == null) {
            mutableStateOf("0")
        } else {
            mutableStateOf(habit.goal.toString())
        }
    }
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card {
            Column {
                OutlinedTextField(
                    value = editHabitName,
                    label = { Text("Habit Name") },
                    onValueChange = { editHabitName = it.take(14) },
                    textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp),
                    maxLines = 1,
                    modifier = Modifier.padding(8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                )
                OutlinedTextField(
                    value = editHabitGoal,
                    label = { Text("Daily Goal") },
                    onValueChange = { editHabitGoal = it.take(4) },
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
                            val habitGoalInt = if (editHabitGoal == "") {
                                null
                            } else {
                                editHabitGoal.toInt()
                            }
                            // checks if habit is updatable and either updates or returns toast
                            if (!habitViewModel.updateHabitClicked(
                                    habit, editHabitName, habitGoalInt
                                )
                            ) {
                                scope.launch {
                                    Utils.showToastShort(context, R.string.habit_already_exists)
                                }
                            } else {
                                Utils.vibrate(context, Utils.VIBE_EFFECT_DOUBLE_CLICK)
                                onDismiss()
                            }
                        }, modifier = Modifier.padding(8.dp)
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