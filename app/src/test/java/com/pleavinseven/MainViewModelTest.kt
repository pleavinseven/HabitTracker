package com.pleavinseven

import android.app.Application
import com.pleavinseven.model.database.Repository
import com.pleavinseven.model.entities.Habit
import com.pleavinseven.model.entities.TimeLogModel
import com.pleavinseven.viewmodels.MainViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class MainViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val applicationMock = mockk<Application> {
        every { applicationContext } returns mockk()
    }
    private lateinit var mockRepository: Repository
    private lateinit var viewModel: MainViewModel
    private val testHabitList = mutableListOf(
        Habit(0, "Habit 1", 0, null), Habit(0, "Habit 2", 1, null)
    )
    private val testHabit = Habit(0, "testHabit", 0, null)
    private val mockTimeLogList = listOf(mockk<TimeLogModel>())
    private val currentTime: LocalDateTime = LocalDateTime.now()
    private val timeLogModel = TimeLogModel(
        logId = 0,
        year = currentTime.year,
        month = currentTime.monthValue,
        day = currentTime.dayOfMonth,
        hour = currentTime.hour,
        min = currentTime.minute,
        seconds = currentTime.second,
        habitName = testHabit.name
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() = runTest {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        coEvery { mockRepository.getHabits() } returns flowOf(testHabitList)
        coEvery { mockRepository.updateCount(any()) } returns Unit
        coEvery { mockRepository.addTimeLog(any()) } returns Unit
        coEvery { mockRepository.getHabitWithTimeLogs(any()) } returns flowOf()
        coEvery { mockRepository.addHabit(any()) } returns Unit
        coEvery { mockRepository.removeLastTimeLog(any()) } returns Unit
        launch { viewModel = MainViewModel(mockRepository, applicationMock) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun testAddHabitToDB() {
        viewModel.createHabitClicked(testHabit.name, 3)
        coVerify {
            mockRepository.addHabit(match {
                it.name == testHabit.name && it.count == 0
            })
        }
    }

    @Test
    fun testGetHabits() {
        runBlocking { assertEquals(testHabitList, viewModel.habitList) }
    }

    @Test
    fun testAddCount() {
        viewModel.onCountButtonClicked(testHabit)
        assertEquals(1, testHabit.count)
        coVerify {
            mockRepository.updateCount(match {
                it.name == testHabit.name && it.count == 1
            })
        }
    }

    @Test
    fun testDecreaseCount_WhenCountIsZero() {
        viewModel.timeLogList = emptyList()
        viewModel.onDecreaseButtonClicked(testHabit)
        assertEquals(0, testHabit.count)
        coVerify(exactly = 0) {
            mockRepository.updateCount(any())
        }
    }

    @Test
    fun testDecreaseCount_WhenCountIsGreaterThanZero() {
        testHabit.count = 1
        viewModel.timeLogList = mockTimeLogList
        viewModel.onDecreaseButtonClicked(testHabit)
        assertEquals(0, testHabit.count)
        coVerify {
            mockRepository.updateCount(match {
                it.name == testHabit.name && it.count == 0
            })
        }
    }

    @Test
    fun testRemoveLastTimeLog_WhenCountIsGreaterThanZero() = runTest(testDispatcher) {
        testHabit.count = 1
        viewModel.timeLogList = mockTimeLogList.toMutableList()
        viewModel.onDecreaseButtonClicked(testHabit)
        coVerify { mockRepository.removeLastTimeLog(mockTimeLogList.last()) }
    }

    @Test
    fun testLogTimeStampInDatabase() {
        viewModel.onCountButtonClicked(testHabit)
        coVerify {
            mockRepository.addTimeLog(match {
                it.year == timeLogModel.year &&
                        it.month == timeLogModel.month &&
                        it.day == timeLogModel.day &&
                        it.hour == timeLogModel.hour &&
                        it.min == timeLogModel.min &&
                        it.seconds == timeLogModel.seconds &&
                        it.habitName == timeLogModel.habitName
            })
        }
    }
}