package com.pleavinseven.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pleavinseven.R
import com.pleavinseven.viewmodels.MainViewModel

@Composable
fun CounterPage(viewModel: MainViewModel, habitName: String) {
    val habit = viewModel.getHabitFromId(habitName)
    val startCount = habit.count.toString()
    var count by remember {
        mutableStateOf(startCount)
    }
    viewModel.getTimeLogs(habitName)
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = habitName.uppercase(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge,
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = {
                        viewModel.onDecreaseButtonClicked(habit)
                        count = habit.count.toString()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .weight(0.3f),
                    contentPadding = PaddingValues(0.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = stringResource(id = R.string.add_habit_description)
                    )
                }
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 150.sp,
                    text = count
                )
                FilledTonalButton(
                    onClick = {
                        viewModel.onCountButtonClicked(habit)
                        count = habit.count.toString()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .weight(0.3f),
                    contentPadding = PaddingValues(0.dp),
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add_habit_description)
                    )
                }
            }
        }
    }
}