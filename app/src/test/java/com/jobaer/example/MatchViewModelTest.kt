package com.jobaer.example

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import com.google.common.truth.Truth.assertThat
import com.jobaer.example.fakes.FakeMatchRepository
import com.jobaer.example.models.Match
import com.jobaer.example.ui.viewmodels.MatchViewModel
import com.jobaer.example.utils.LiveDataTestUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class MatchViewModelTest {
    private lateinit var viewModel: MatchViewModel
    private lateinit var fakeRepository: FakeMatchRepository
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        fakeRepository = FakeMatchRepository()
        viewModel = MatchViewModel(fakeRepository)
    }

    @Test
    fun `getMatches should load data into previous and upcoming LiveData`() = runTest {
        // Arrange
        val fakeUpcomingMatches = listOf(Match(), Match())
        val fakePreviousMatches = listOf(Match(), Match())
        fakeRepository.addUpcomingMatches(fakeUpcomingMatches)
        fakeRepository.addPreviousMatches(fakePreviousMatches)

        // Act
        viewModel.getMatches()

        // Assert
        val previous = LiveDataTestUtil.getValue(viewModel.previousMatches)
        assertThat(previous).isEqualTo(fakeRepository.previousMatches.first())

        val upcoming = LiveDataTestUtil.getValue(viewModel.upcomingMatches)
        assertThat(upcoming).isEqualTo(fakeRepository.upcomingMatches.first())
    }

    @Test
    fun `getMatches() should load upcoming matches into LiveData`() = runTest {

        // Arrange
        val fakeUpcomingMatches = listOf(Match(), Match())
        fakeRepository.addUpcomingMatches(fakeUpcomingMatches)

        // Act
        viewModel.getMatches()

        // Assert
        val upcomingMatches = LiveDataTestUtil.getValue(viewModel.upcomingMatches)
        assertThat(upcomingMatches).isEqualTo(fakeUpcomingMatches)
    }

    @Test
    fun `getMatches() should load previous matches into LiveData`() = runTest {
        // Arrange
        val fakePreviousMatches = listOf(Match(), Match())
        fakeRepository.addPreviousMatches(fakePreviousMatches)

        // Act
        viewModel.getMatches()

        // Assert
        val previousMatches = LiveDataTestUtil.getValue(viewModel.previousMatches)
        assertThat(previousMatches).isEqualTo(fakePreviousMatches)
    }

    @Test
    fun `getMatches() should handle HTTP error gracefully`() = runTest {
        // Arrange
        fakeRepository.setShouldThrowNetworkError(true)

        // Act
        viewModel.getMatches()

        // Assert
        val message = LiveDataTestUtil.getValue(viewModel.message)
        val loader = LiveDataTestUtil.getValue(viewModel.loader)

        assertThat(message).isEqualTo("HTTP error: 400")
        assertThat(loader).isFalse()
    }

    @Test
    fun `getMatches() should handle other errors gracefully`() = runTest {
        // Arrange
        fakeRepository.setShouldThrowError(true)

        // Act
        viewModel.getMatches()

        // Assert
        val message = LiveDataTestUtil.getValue(viewModel.message)
        val loader = LiveDataTestUtil.getValue(viewModel.loader)

        assertThat(message).isEqualTo("Custom error message")
        assertThat(loader).isFalse()
    }

    @Test
    fun `getUpcomingMatchByTeam() should load previous matches by team into LiveData`() =
        runTest {
            Dispatchers.setMain(UnconfinedTestDispatcher())
            // Arrange
            val teamName = "Team B"
            val fakeUpcomingMatches = listOf(Match(), Match(), Match(home = teamName))
            fakeRepository.addUpcomingMatches(fakeUpcomingMatches)

            // Act
            viewModel.setCurrentSelectTeam(teamName)

            // Assert
            val upcomingMatches = LiveDataTestUtil.getValue(viewModel.upcomingMatches)
            assertThat(upcomingMatches).isEqualTo(fakeUpcomingMatches.filter { it.home == teamName })
        }



    @Test
    fun `getPreviousMatchByTeam() should load previous matches by team into LiveData`() =
        runTest {
            Dispatchers.setMain(UnconfinedTestDispatcher())
            // Arrange
            val teamName = "Team B"
            val fakePreviousMatches = listOf(Match(), Match(), Match(home = teamName))
            fakeRepository.addPreviousMatches(fakePreviousMatches)


            // Act
            viewModel.setCurrentSelectTeam(teamName)

            // Assert
            val previousMatches = LiveDataTestUtil.getValue(viewModel.previousMatches)
            assertThat(previousMatches).isEqualTo(fakePreviousMatches.filter { it.home == teamName })
        }


    @Test
    fun `setCurrentSelectTeam() should update selectedTeamName LiveData`() {
        // Arrange
        val teamName = "Team C"

        // Act
        viewModel.setCurrentSelectTeam(teamName)

        // Assert
        val selectedTeam = LiveDataTestUtil.getValue(viewModel.selectedTeamName.asLiveData())
        assertThat(selectedTeam).isEqualTo(teamName)
    }

    @Test
    fun `clearCurrentSelectTeam() should clear selectedTeamName LiveData`() {
        // Arrange
        viewModel.setCurrentSelectTeam("Team D")

        // Act
        viewModel.clearCurrentSelectTeam()

        // Assert
        val selectedTeam = LiveDataTestUtil.getValue(viewModel.selectedTeamName.asLiveData())
        assertThat(selectedTeam).isEmpty()
    }

    @Test
    fun `updateMatch should set isNotificationSet true`() = runTest {
        val match = Match()
        assertThat(match.isNotificationSet).isFalse()
        fakeRepository.addUpcomingMatches(listOf(match))
        viewModel.updateNotificationOfMatch(match)
        val updatedMatch = fakeRepository.upcomingMatches.first().find { it.id==match.id }
        assertThat(updatedMatch?.isNotificationSet).isTrue()
    }




}
