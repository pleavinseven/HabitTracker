package com.pleavinseven

import com.pleavinseven.model.database.Repository
import com.pleavinseven.model.entities.Habit
import com.pleavinseven.viewmodels.HabitViewModel
import com.pleavinseven.workers.ResetWorkManagerScheduler
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HabitViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var mockRepository: Repository
    private lateinit var mockResetWorkManagerScheduler: ResetWorkManagerScheduler

    private lateinit var viewModel: HabitViewModel
    private val testHabitList = mutableListOf(
        Habit(0, "Habit 1", 0, null, 1), Habit(0, "Habit 2", 1, null, 1)
    )
    private val testHabit = Habit(0, "testHabit", 0, null, 1)
    private val goal = 3

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() = runTest {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        mockResetWorkManagerScheduler = mockk()

        coEvery { mockRepository.getHabits() } returns flowOf(testHabitList)
        coEvery { mockRepository.updateHabit(any()) } returns Unit
        coEvery { mockRepository.getHabitWithTimeLogs(any()) } returns flowOf()
        coEvery { mockRepository.addHabit(any()) } returns Unit
        coEvery { mockRepository.deleteHabit(any()) } returns Unit
        every { mockResetWorkManagerScheduler.scheduleLogAndReset(any(), any()) } returns Unit
        every { mockResetWorkManagerScheduler.cancel(any()) } returns Unit
        launch {
            viewModel = HabitViewModel(mockRepository, mockResetWorkManagerScheduler)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun testCreateHabitClicked_createHabitAndAddToDatabase() {
        viewModel.createHabitClicked(testHabit.name, goal, 1)
        coVerify {
            mockRepository.addHabit(match {
                it.name == testHabit.name && it.count == 0 && it.goal == goal
            })
        }
    }

    @Test
    fun testGetHabits_fromRepository() {
        val result = viewModel.habitList
        runBlocking { assertEquals(testHabitList, result) }
    }

    @Test
    fun testOnCountButtonClicked_addCountToHabitAndUpdateDatabase() {
        val initialCount = testHabit.count
        viewModel.onCountButtonClicked(testHabit)
        assertEquals(initialCount + 1, testHabit.count)
        coVerify {
            mockRepository.updateHabit(match {
                it.name == testHabit.name && it.count == initialCount + 1
            })
        }
    }

    @Test
    fun testOnDecreaseButtonClicked_WhenCountIsZero_DoesNotDecreaseOrUpdateDatabase() {
        viewModel.onDecreaseButtonClicked(testHabit)
        assertEquals(0, testHabit.count)
        coVerify(exactly = 0) {
            mockRepository.updateHabit(testHabit)
        }
    }

    @Test
    fun testOnDecreaseButtonClicked_WhenCountIsGreaterThanZero_DecreasesAndUpdatesDatabase() {
        testHabit.count = 1
        viewModel.onDecreaseButtonClicked(testHabit)
        assertEquals(0, testHabit.count)
        coVerify {
            mockRepository.updateHabit(match {
                it.name == testHabit.name && it.count == 0
            })
        }
    }

    @Test
    fun testOnHabitConfirmDeleteClick_habitRemovedFromDatabase() {
        viewModel.onHabitConfirmDeleteClick(testHabit)
        coVerify {
            mockRepository.deleteHabit(match {
                it.name == testHabit.name && it.count == testHabit.count
            })
        }
    }

    @Test
    fun testUpdateHabitClicked_withNoName_ReturnsFalse() {
        val emptyName = ""
        val newGoal = 5
        val resultEmpty = viewModel.updateHabitClicked(testHabit, emptyName, newGoal)
        assertFalse(resultEmpty)
        coVerify(exactly = 0) {
            mockRepository.updateHabit(any())
        }
    }

    @Test
    fun testUpdateHabitClicked_withSameName_UpdatesGoalOnly() {
        val sameName = testHabit.name
        val newGoal = 5
        val resultSame = viewModel.updateHabitClicked(testHabit, sameName, newGoal)
        assertTrue(resultSame)
        coVerify {
            mockRepository.updateHabit(match {
                it.name == sameName && it.count == testHabit.count && it.goal == newGoal
            })
        }
    }

    @Test
    fun testUpdateHabitClicked_withNewName_UpdatesNameAndGoal() {
        val newName = "new name"
        val newGoal = 5
        val resultNew = viewModel.updateHabitClicked(testHabit, newName, newGoal)
        assertTrue(resultNew)
        coVerify {
            mockRepository.updateHabit(match {
                it.name == newName && it.count == testHabit.count && it.goal == newGoal
            })
        }
    }

}