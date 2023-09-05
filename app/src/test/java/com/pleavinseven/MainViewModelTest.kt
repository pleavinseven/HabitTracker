package com.pleavinseven

import android.app.Application
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.pleavinseven.model.Repository
import com.pleavinseven.model.TestDatabase
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class MainViewModelTest {

    private val applicationMock = mockk<Application>() {
        every { applicationContext } returns mockk()
    }

    private val database = Room.inMemoryDatabaseBuilder(
        InstrumentationRegistry.getInstrumentation().context, TestDatabase::class.java
    ).build()
    private val timeLogDao = database.timeLogDao()
    private val repository = Repository(timeLogDao)
    private val viewModel = MainViewModel(repository, applicationMock)

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