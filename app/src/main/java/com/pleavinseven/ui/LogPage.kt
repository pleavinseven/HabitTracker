package com.pleavinseven.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.ExtraStore
import com.patrykandpatrick.vico.core.model.columnSeries
import com.pleavinseven.composables.MyTheme
import com.pleavinseven.model.entities.DailyCount
import com.pleavinseven.viewmodels.LogPageViewModel

@Composable
fun LogPage(
    navController: NavController,
    viewModel: LogPageViewModel,
) {
    viewModel.getDataForGraphs()
    viewModel.timeLogList
    MyTheme(navController = navController) {
        LazyCalendar(viewModel)
        DailyCountBarChart(viewModel.countList, viewModel)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2)
        ) {
            // TODO add other graphs/ logs
        }
    }
}

@Composable
fun LazyCalendar(viewModel: LogPageViewModel) {

    var dateTitle by remember(viewModel) { mutableStateOf(viewModel.formattedDateString) }
    var monthLength by remember(viewModel) { mutableStateOf(viewModel.getMonthLength()) }
    val lazyListState = rememberLazyListState()

    // scroll to last index so calendar starts at most recent/ last day of month
    LaunchedEffect(viewModel.scrollIndex) {
        lazyListState.scrollToItem(index = viewModel.scrollIndex)
    }

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
                        monthLength.lastIndex
                    }
                    .weight(1f),
                text = dateTitle
            )
        }
        LazyRow(
            state = lazyListState,
            modifier = Modifier.padding(
                top = 10.dp,
            )
        ) {
            items(monthLength) { item ->
                CalendarCard(
                    day = item,
                    onClick = {
                        viewModel.scrollIndex = monthLength.indexOf(item)
                    }
                )
            }
        }
    }
}

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

@Composable
fun DailyCountBarChart(countList: List<DailyCount>, viewModel: LogPageViewModel) {
    Card(
        modifier = Modifier.padding(12.dp),
        border = BorderStroke(2.dp, Color.White)
    ) {
        var counts by remember { mutableStateOf(countList.map { it.count }) }
        var dates by remember { mutableStateOf(countList.map { it.day }) }
        var monthLength by remember(viewModel) { mutableStateOf(viewModel.getMonthLength()) }
        val modelProducer = remember { CartesianChartModelProducer.build() }
        monthLength = viewModel.getMonthLength()
        val labelListKey = ExtraStore.Key<Array<String>>()
        LaunchedEffect(Unit) {
            modelProducer.tryRunTransaction {
                counts = countList.map { it.count }
                dates = countList.map { it.day }
                columnSeries {
//                    for each series x will be the dates that it occurred and y the amount
//                    if x is not given then it starts from 0
//                    series(y = listOf(6, 1, 9, 3))
//                    series(x = listOf(1, 2, 3, 4), y = listOf(2, 5, 3, 4))
                    series(x = dates, y = counts)
                }
                updateExtras {
                    it[labelListKey] = monthLength
                }
            }
        }
        CartesianChartHost(
            rememberCartesianChart(
                rememberColumnCartesianLayer(),
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
            ),
            modelProducer,
        )
    }
}