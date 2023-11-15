package com.jobaer.example

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jobaer.example.ui.viewmodels.TeamViewModel
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.google.common.truth.Truth.assertThat
import com.jobaer.example.fakes.FakeTeamRepository
import com.jobaer.example.utils.LiveDataTestUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class TeamViewModelTest {

    private lateinit var viewModel: TeamViewModel
    private lateinit var fakeRepository: FakeTeamRepository

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        fakeRepository = FakeTeamRepository()
        viewModel = TeamViewModel(fakeRepository)
    }

    @Test
    fun `getTeams() should load data into teams LiveData`() = runTest {
        // Act
        viewModel.getTeams()

        // Assert
        val teams = LiveDataTestUtil.getValue(viewModel.teams)
        assertThat(teams).isEqualTo(fakeRepository.teams.first())
    }

    @Test
    fun `getTeams() should handle HTTP error gracefully`() = runTest {
        // Arrange
        fakeRepository.setShouldThrowNetworkError(true)

        // Act
        viewModel.getTeams()

        // Assert
        val message = LiveDataTestUtil.getValue(viewModel.message)
        val loader = LiveDataTestUtil.getValue(viewModel.loader)

        assertThat(message).isEqualTo("HTTP error: 400")
        assertThat(loader).isFalse()
    }

    @Test
    fun `getTeams() should handle other errors gracefully`() = runTest {
        // Arrange
        fakeRepository.setShouldThrowError(true)

        // Act
        viewModel.getTeams()

        // Assert
        val message = LiveDataTestUtil.getValue(viewModel.message)
        val loader = LiveDataTestUtil.getValue(viewModel.loader)

        assertThat(message).isEqualTo("Custom error message")
        assertThat(loader).isFalse()
    }
}

