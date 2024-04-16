package com.pleavinseven.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pleavinseven.model.database.Repository
import com.pleavinseven.model.entities.DailyCount
import com.pleavinseven.model.entities.TimeLogModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

class LogPageViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _countList = mutableStateOf(emptyList<DailyCount>())
    private val _timeLogList = mutableStateOf(emptyList<TimeLogModel>())

    val countList: List<DailyCount> get() = _countList.value
    val timeLogList: List<TimeLogModel> get() = _timeLogList.value

    private val currentDate: LocalDate = LocalDate.now()
    private var date: LocalDate = LocalDate.now()
    var formattedDateString: String = date.format(DateTimeFormatter.ofPattern("LLL yyyy"))
    var scrollIndex by mutableIntStateOf(getMonthLength().size)

    fun getDataForGraphs() {
        viewModelScope.launch {
            val habitWithTimeLogs = repository.getMonthlyHabitDataList(
                date.monthValue,
                date.year
            )
            _countList.value =
                habitWithTimeLogs.map { it.dailyCount }.flatten() // Separate dailyCounts
            _timeLogList.value =
                habitWithTimeLogs.map { it.timeLogs }.flatten() // Separate timeLogs
        }
    }

    fun monthClick(): Month {
        date = date.minusMonths(1)
        formattedDateString = date.format(DateTimeFormatter.ofPattern("LLL yyyy"))
        return date.month
    }

    fun getMonthLength(): Array<String> {
        val monthLength = if (date.month == currentDate.month && date.year == currentDate.year) {
            currentDate.dayOfMonth
        } else {
            date.lengthOfMonth()
        }
        var daysArray = arrayOf<String>()
        for (day in 1..monthLength) {
            daysArray += LocalDate.of(date.year, date.month, day)
                .format(DateTimeFormatter.ofPattern("EE \n dd"))
        }
        return daysArray
    }
}
