package com.pleavinseven

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainViewModel {

    var count by mutableStateOf(0)

    fun onCountButtonClicked(){
        count += 1
    }
}