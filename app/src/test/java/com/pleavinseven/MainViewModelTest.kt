package com.pleavinseven

import android.app.Application
import com.pleavinseven.model.database.HabitDao
import com.pleavinseven.model.database.Repository
import com.pleavinseven.model.database.TimeLogDao
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private val applicationMock = mockk<Application> {
        every { applicationContext } returns mockk()
    }
    private val timeLogDao = mockk<TimeLogDao>(relaxed = true)
    private val habitDao = mockk<HabitDao>(relaxed = true)
    private lateinit var repository: Repository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        repository = Repository(timeLogDao, habitDao)
        viewModel = MainViewModel(repository, applicationMock)
    }

    @Test
    fun countStartsAtZero() {
        assertEquals(0, viewModel.count)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun onCountButtonClicked_CountPlusOne() = runTest {
        Dispatchers.setMain(Dispatchers.Unconfined)
        launch { viewModel.onCountButtonClicked() }
        advanceUntilIdle()
        assertEquals(1, viewModel.count)
    }

    @Test
    fun beforeCallingOnClick_TimeLogsAreEmpty() {
        assert(viewModel.timeLogsList.isEmpty())
    }
}