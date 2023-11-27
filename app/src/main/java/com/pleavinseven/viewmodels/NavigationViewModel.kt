package com.pleavinseven.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationViewModel {
    private val _mutableNavBarPosition = MutableStateFlow(0)
    val navBarPosition: StateFlow<Int> = _mutableNavBarPosition

    fun setNavBarPosition(pos: Int) {
        _mutableNavBarPosition.value = pos
    }
}