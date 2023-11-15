package com.jobaer.example.ui

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.work.Configuration
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.jobaer.example.R
import com.jobaer.example.data.MatchRepository
import com.jobaer.example.data.TeamRepository
import com.jobaer.example.fakes.FakeTeamRepository
import com.jobaer.example.models.Team
import com.jobaer.example.utils.DataBindingIdlingResource
import com.jobaer.example.utils.EspressoIdlingResource
import com.jobaer.example.utils.TestUtils
import com.jobaer.example.utils.monitorActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class AppNavigationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS,android.Manifest.permission.SCHEDULE_EXACT_ALARM)

    @Inject
    lateinit var teamRepository: TeamRepository

    @Inject
    lateinit var matchRepository: MatchRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        // Populate @Inject fields in test class
        hiltRule.inject()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

    }

    @Before
    fun registerIdlingResource() {

        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun navigateToTeamsFragment() {
        onView(allOf(
            withText(R.string.teams),
            withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click())

        onView(withId(R.id.teamsLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToTeamsFragmentDisplayToolbarTittle() {
        onView(allOf(
            withText(R.string.teams),
            withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click())

        onView(withText(R.string.all_teams)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToMatchesFragment() {
        onView(allOf(
            withText(R.string.matches),
            withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click())

        onView(withId(R.id.matchesLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToMatchesFragmentDisplayToolbarTittle() {
        onView(allOf(
            withText(R.string.matches),
            withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click())

        onView(withText(R.string.all_matches)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnTeamNavigateToMatchesFragment() {
        val name ="Real madrid"
        (teamRepository as FakeTeamRepository).addTeams(listOf(Team("1",name,"abvd")))
        onView(withId(R.id.teamsRecycler))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    TestUtils.clickChildViewWithId(R.id.teamItem)
                )
            )
        onView(withText(R.string.previous_matches)).check(matches(isDisplayed()))
    }


}