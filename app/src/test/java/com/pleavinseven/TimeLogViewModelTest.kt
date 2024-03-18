package com.pleavinseven

import com.pleavinseven.model.database.Repository
import com.pleavinseven.model.entities.Habit
import com.pleavinseven.model.entities.TimeLogModel
import com.pleavinseven.viewmodels.TimeLogViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class TimeLogViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var mockRepository: Repository
    private lateinit var viewModel: TimeLogViewModel

    private val testHabit = Habit(0, "testHabit", 0, null, 1)
    private val mockTimeLogList = listOf(mockk<TimeLogModel>())
    private val currentTime: LocalDateTime = LocalDateTime.now()
    private val timeLogModel = TimeLogModel(
        logId = 0,
        year = currentTime.year,
        month = currentTime.monthValue,
        day = currentTime.dayOfMonth,
        hour = currentTime.hour,
        min = currentTime.minute,
        seconds = null,
        habitId = testHabit.id
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() = runTest {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()

        coEvery { mockRepository.addTimeLog(any()) } returns Unit
        coEvery { mockRepository.getMonthlyHabitDataList(any(), any()) } returns listOf()
        coEvery { mockRepository.removeLastTimeLog(any()) } returns Unit
        launch {
            viewModel = TimeLogViewModel(mockRepository)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun testOnDecreaseButtonClicked_WhenCountIsZero_DoesNotRemoveTimeLog() {
        testHabit.count = 0
        viewModel.timeLogList = emptyList()
    }

    @Test
    fun testRemoveLastTimeLog_WhenCountIsGreaterThanZero() = runTest(testDispatcher) {
        testHabit.count = 1
        viewModel.timeLogList = mockTimeLogList.toMutableList()
        viewModel.removeLastTimeLog(testHabit)
        coVerify { mockRepository.removeLastTimeLog(mockTimeLogList.last()) }
    }

    @Test
    fun testLogTimeStampInDatabase() {
        viewModel.logTimeStampInDatabase(testHabit.id)
        coVerify {
            mockRepository.addTimeLog(match {
                it.year == timeLogModel.year && it.month == timeLogModel.month && it.day == timeLogModel.day && it.hour == timeLogModel.hour && it.min == timeLogModel.min && it.habitId == timeLogModel.habitId
            })
        }
    }
}