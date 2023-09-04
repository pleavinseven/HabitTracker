package com.pleavinseven.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pleavinseven.MainViewModel


@Composable
fun HabitTrackerApp(viewModel: MainViewModel) {
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

