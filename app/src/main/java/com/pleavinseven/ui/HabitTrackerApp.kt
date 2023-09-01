package com.pleavinseven.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.pleavinseven.MainViewModel

val viewModel = MainViewModel()

@Composable
fun HabitTrackerApp() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = { viewModel.onCountButtonClicked() },
        ) {
            Text(
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge,
                text = viewModel.count.toString()
            )
        }
    }
}

