package com.pleavinseven

import android.app.Application
import com.pleavinseven.model.database.Repository
import com.pleavinseven.model.entities.Habit
import com.pleavinseven.model.entities.TimeLogModel
import com.pleavinseven.viewmodels.MainViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class MainViewModelTest {

    private val applicationMock = mockk<Application> {
        every { applicationContext } returns mockk()
    }
    private lateinit var mockRepository: Repository
    private lateinit var viewModel: MainViewModel
    private val testHabitList = mutableListOf(Habit("Habit 1", 0), Habit("Habit 2", 1))
    private val testHabit = Habit("testHabit", 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() = runTest {
        Dispatchers.setMain(Dispatchers.Unconfined)
        mockRepository = mockk()
        coEvery { mockRepository.getHabits() } returns flowOf(testHabitList)
        coEvery { mockRepository.addCount(any()) } returns Unit
        coEvery { mockRepository.addTimeLog(any()) } returns Unit
        coEvery { mockRepository.getHabitWithTimeLogs(any()) } returns flowOf()
        coEvery { mockRepository.addHabit(any()) } returns Unit
        launch { viewModel = MainViewModel(mockRepository, applicationMock) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAddHabitToDB() {
        viewModel.createHabitClicked(testHabit.habitName)
        coVerify {
            mockRepository.addHabit(match {
                it.habitName == testHabit.habitName
                        && it.count == 0
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
            mockRepository.addCount(match
            {
                it.habitName == testHabit.habitName &&
                        it.count == 1
            }
            )
        }
    }

    @Test
    fun testLogTimeStampInDatabase() {
        val currentTime = LocalDateTime.now()
        val timeLogModel = TimeLogModel(
            logId = 0,
            year = currentTime.year,
            month = currentTime.monthValue,
            day = currentTime.dayOfMonth,
            hour = currentTime.hour,
            min = currentTime.minute,
            seconds = currentTime.second,
            habitName = testHabit.habitName
        )
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