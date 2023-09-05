package com.pleavinseven

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pleavinseven.model.Repository
import com.pleavinseven.model.TimeLogModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainViewModel(
    private val repository: Repository, application: Application
) : AndroidViewModel(application) {

    var count by mutableStateOf(0)


    fun onCountButtonClicked() {
        addCount()
        logTimeStampInDatabase()
    }

    private fun addCount() {
        count++
    }

    private fun logTimeStampInDatabase() {
        val currentTime = LocalDateTime.now()
        val timeLogModel = TimeLogModel(
            logId = 0,
            year = currentTime.year,
            month = currentTime.monthValue,
            day = currentTime.dayOfMonth,
            hour = currentTime.hour,
            min = currentTime.minute,
            seconds = currentTime.second
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTimeLog(timeLogModel)
        }
    }

    private fun formatReadableTime(currentTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        return currentTime.format(formatter)
    }
}