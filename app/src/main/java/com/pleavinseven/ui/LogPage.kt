package com.pleavinseven.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pleavinseven.composables.MyTheme

@Composable
fun LogPage(
    navController: NavController,
    viewModel: LogPageViewModel,
) {
    viewModel.getDataForGraphs()
    MyTheme(navController = navController) {
        LazyCalendar(viewModel)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2)
        ) {

        }
    }
}


@Composable
fun LazyCalendar(viewModel: LogPageViewModel) {

    var dateTitle by remember(viewModel) { mutableStateOf(viewModel.formattedDateString) }
    var monthLength by remember(viewModel) { mutableStateOf(viewModel.getMonthLength()) }

    Column {
        Row(
            Modifier.padding(
                start = 10.dp,
            )
        ) {
            Text(
                modifier = Modifier
                    .clickable {
                        viewModel.monthClick()
                        monthLength = viewModel.getMonthLength()
                        dateTitle = viewModel.formattedDateString
                    }
                    .weight(1f),
                text = dateTitle
            )
        }
        LazyRow(
            Modifier.padding(
                top = 10.dp,
            )
        ) {
            items(monthLength) { item ->
                CalendarCard(
                    day = item,
                    onClick = {

                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarCard(
    day: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(60.dp)
            .width(60.dp)
            .padding(4.dp, 2.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}