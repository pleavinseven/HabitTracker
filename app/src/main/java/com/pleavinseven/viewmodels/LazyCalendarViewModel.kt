package com.pleavinseven.viewmodels

import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

class LazyCalendarViewModel : ViewModel() {

    private val currentDate: LocalDate = LocalDate.now()
    var date: LocalDate = LocalDate.now()
    var formattedDateString: String = date.format(DateTimeFormatter.ofPattern("LLL yyyy"))

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