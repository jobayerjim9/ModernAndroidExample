package com.jobaer.example.ui

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.jobaer.example.data.TeamRepository
import com.jobaer.example.ui.fragments.TeamsFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import com.jobaer.example.R
import com.jobaer.example.fakes.FakeTeamRepository
import com.jobaer.example.launchFragmentInHiltContainer
import com.jobaer.example.models.Team
import com.jobaer.example.utils.TestUtils.atPosition
import com.jobaer.example.utils.TestUtils.hasDrawable
import org.hamcrest.Matchers.allOf

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class TeamsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: TeamRepository

    @Before
    fun init() {
        // Populate @Inject fields in test class
        hiltRule.inject()
    }


    @Test
    fun testTeamsRecyclerViewDisplay() {
        launchFragmentInHiltContainer <TeamsFragment>()
        // Use Espresso to perform assertions on the fragment's view
        onView(withId(R.id.teamsRecycler)).check(matches(isDisplayed()))
    }

    @Test
    fun displayTeams() {
        launchFragmentInHiltContainer <TeamsFragment>()
        val teams= listOf(Team("1","Real Madrid","xcdc"),Team("2","Barcelona","http://google.com"),Team("3","PSG","http://google.com"))
        (repository as FakeTeamRepository).addTeams(teams)
        onView(withText("Real Madrid")).check(matches(isDisplayed()))
        onView(withText("Barcelona")).check(matches(isDisplayed()))
        onView(withText("PSG")).check(matches(isDisplayed()))
    }
    @Test
    fun checkTeamLogoDisplayed() {
        launchFragmentInHiltContainer <TeamsFragment>()
        val teams= listOf(Team("1","Real Madrid","xcdc"),Team("2","Barcelona","http://google.com"),Team("3","PSG","http://google.com"))
        (repository as FakeTeamRepository).addTeams(teams)
        for (i in teams.indices) {
            onView(withId(R.id.teamsRecycler))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(i))
                .check(matches(atPosition(i, hasDescendant(allOf(withId(R.id.logo), hasDrawable())))))
        }
    }

    @Test
    fun checkScrollingAndLastItemDisplayed() {
        launchFragmentInHiltContainer <TeamsFragment>()
        val teams = mutableListOf<Team>()
        repeat(50) { i->
            teams.add(Team("$i","Team $i","Logo"))
        }

        val positionToCheck = teams.size-1
        (repository as FakeTeamRepository).addTeams(teams)
        onView(withId(R.id.teamsRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(positionToCheck))
            .check(matches(atPosition(positionToCheck, hasDescendant(allOf(withId(R.id.title), withText(teams[positionToCheck].name))))))
    }


    @Test
    fun clickTeamNavigateToMatchesFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<TeamsFragment>(Bundle(), R.style.Theme_KickoffByZujuInterview) {
            navController.setGraph(R.navigation.main_nav)
            navController.setCurrentDestination(R.id.teamsFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
        (repository as FakeTeamRepository).addTeams(listOf(Team("1","Real Madrid","http://google.com")))
        onView(withId(R.id.teamsRecycler)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.matchesFragment)
    }

}