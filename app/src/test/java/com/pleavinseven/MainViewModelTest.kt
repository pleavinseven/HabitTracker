package com.pleavinseven

import org.junit.Assert.assertEquals
import org.junit.Test

class MainViewModelTest {

    private val viewModel = MainViewModel()

    @Test
    fun countStartsAtZero() {
        assertEquals(0, viewModel.count)
    }

    @Test
    fun onCountButtonClicked_CountPlusOne() {
        viewModel.onCountButtonClicked()
        assertEquals(1, viewModel.count)
        viewModel.onCountButtonClicked()
        assertEquals(2, viewModel.count)
    }
}