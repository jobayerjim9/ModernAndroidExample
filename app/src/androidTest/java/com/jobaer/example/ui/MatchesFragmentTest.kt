package com.jobaer.example.ui

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.jobaer.example.R
import com.jobaer.example.data.MatchRepository
import com.jobaer.example.fakes.FakeMatchRepository
import com.jobaer.example.launchFragmentInHiltContainer
import com.jobaer.example.models.Match
import com.jobaer.example.models.Team
import com.jobaer.example.ui.fragments.MatchesFragment
import com.jobaer.example.ui.fragments.MatchesFragmentArgs
import com.jobaer.example.utils.TestUtils.atPosition
import com.jobaer.example.utils.TestUtils.clickChildViewWithId
import com.jobaer.example.utils.Utils
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class MatchesFragmentTest {
    private val sampleUpcomingMatch = Match(
        1,
        "2024-08-13T20:00:00.000Z",
        "Real Madrid Vs. Barcelona",
        "Real Madrid",
        "Barcelona"
    )

    private val samplePreviousMatch = Match(
        1,
        "2022-08-13T20:00:00.000Z",
        "Real Madrid Vs. Barcelona",
        "Real Madrid",
        "Barcelona",
        "Real Madrid",
        "https://tstzj.s3.amazonaws.com/highlights.mp4"
    )

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: MatchRepository

    @Before
    fun init() {
        // Populate @Inject fields in test class
        hiltRule.inject()
    }

    @Test
    fun checkPreviousMatchesRecyclerViewDisplayForAllMatches() {
        launchMatchesFragmentForAllMatch()

        (repository as FakeMatchRepository).addPreviousMatches(listOf(Match()))

        // Use Espresso to perform assertions on the fragment's view
        onView(withId(R.id.prevousRecycler))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkUpcomingMatchesRecyclerViewDisplayForAllMatches() {
        launchMatchesFragmentForAllMatch()

        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))

        // Use Espresso to perform assertions on the fragment's view
        onView(withId(R.id.upcomingRecycler))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkBothTypeMatchesRecyclerViewDisplayForAllMatches() {
        launchMatchesFragmentForAllMatch()

        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))

        // Use Espresso to perform assertions on the fragment's view
        onView(withId(R.id.upcomingRecycler))
            .check(matches(isDisplayed()))

        onView(withId(R.id.prevousRecycler))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkBothLabelsAreDisplayed() {
        launchMatchesFragmentForAllMatch()

        onView(withText(R.string.previous_matches)).check(matches(isDisplayed()))
        onView(withText(R.string.upcoming_matches)).check(matches(isDisplayed()))
    }

    @Test
    fun checkAllLabelsAndRecyclerViewsDisplayed() {
        launchMatchesFragmentForAllMatch()

        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))
        onView(withText(R.string.previous_matches)).check(matches(isDisplayed()))

        onView(withText(R.string.upcoming_matches)).check(matches(isDisplayed()))

        onView(withId(R.id.upcomingRecycler))
            .check(matches(isDisplayed()))

        onView(withId(R.id.prevousRecycler))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkPreviousMatchNoItemPlaceholderDisplayed() {
        launchMatchesFragmentForAllMatch()

        onView(withText(R.string.previous_matches)).check(matches(isDisplayed()))
        onView(withText(R.string.no_previous_match)).check(matches(isDisplayed()))
        onView(withId(R.id.prevousRecycler)).check(matches(not(isDisplayed())))

    }

    @Test
    fun checkUpcomingMatchNoItemPlaceholderDisplayed() {
        launchMatchesFragmentForAllMatch()

        onView(withText(R.string.upcoming_matches)).check(matches(isDisplayed()))
        onView(withText(R.string.no_upcoming_match)).check(matches(isDisplayed()))
        onView(withId(R.id.upcomingRecycler)).check(matches(not(isDisplayed())))
    }

    @Test
    fun checkBothNoItemPlaceholderNotDisplayed() {
        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))
        launchMatchesFragmentForAllMatch()
        onView(withText(R.string.no_upcoming_match)).check(matches(not(isDisplayed())))
        onView(withText(R.string.no_previous_match)).check(matches(not(isDisplayed())))
    }

    @Test
    fun checkBothMatchNoItemPlaceholderDisplayed() {
        launchMatchesFragmentForAllMatch()

        onView(withText(R.string.no_upcoming_match)).check(matches(isDisplayed()))
        onView(withText(R.string.no_previous_match)).check(matches(isDisplayed()))
    }

    @Test
    fun checkOnlyPreviousMatchNoItemPlaceholderDisplayed() {
        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        launchMatchesFragmentForAllMatch()

        onView(withText(R.string.no_upcoming_match)).check(matches(not(isDisplayed())))
        onView(withText(R.string.no_previous_match)).check(matches(isDisplayed()))
    }

    @Test
    fun checkOnlyUpcomingMatchNoItemPlaceholderDisplayed() {
        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))

        launchMatchesFragmentForAllMatch()

        onView(withText(R.string.no_previous_match)).check(matches(not(isDisplayed())))
        onView(withText(R.string.no_upcoming_match)).check(matches(isDisplayed()))
    }

    @Test
    fun checkUpcomingItemsDateIsDisplayedCorrectly() {
        launchMatchesFragmentForAllMatch()

        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        onView(withId(R.id.upcomingRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(
                            allOf(
                                withId(R.id.date),
                                withText(Utils.convertDate(sampleUpcomingMatch.date))
                            )
                        )
                    )
                )
            )

    }

    @Test
    fun checkUpcomingItemsHomeIsDisplayedCorrectly() {
        launchMatchesFragmentForAllMatch()
        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        onView(withId(R.id.upcomingRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(allOf(withId(R.id.home), withText(sampleUpcomingMatch.home)))
                    )
                )
            )
    }


    @Test
    fun checkUpcomingItemsAwayIsDisplayedCorrectly() {
        launchMatchesFragmentForAllMatch()

        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        onView(withId(R.id.upcomingRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(allOf(withId(R.id.away), withText(sampleUpcomingMatch.away)))
                    )
                )
            )

    }

    @Test
    fun checkUpcomingItemsNotifyMeIsDisplayedCorrectly() {
        launchMatchesFragmentForAllMatch()
        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        onView(withId(R.id.upcomingRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(allOf(withId(R.id.notifyMe), withText(R.string.notify_me)))
                    )
                )
            )
    }

    @Test
    fun checkUpcomingItemsNotDisplayShowHighlightsButton() {
        launchMatchesFragmentForAllMatch()

        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        onView(withId(R.id.upcomingRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(allOf(withId(R.id.viewHighlight), not(isDisplayed())))
                    )
                )
            )
    }

    @Test
    fun checkUpcomingItemsNotDisplayWinnerTitle() {
        launchMatchesFragmentForAllMatch()
        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        onView(withId(R.id.upcomingRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(allOf(withId(R.id.winnerTitle), not(isDisplayed())))
                    )
                )
            )
    }

    @Test
    fun checkUpcomingItemsNotDisplayWinner() {
        launchMatchesFragmentForAllMatch()
        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        onView(withId(R.id.upcomingRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(allOf(withId(R.id.winnerName), not(isDisplayed())))
                    )
                )
            )
    }


    @Test
    fun checkPreviousMatchItemsDateIsDisplayedCorrectly() {
        launchMatchesFragmentForAllMatch()

        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))
        onView(withId(R.id.prevousRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(
                            allOf(
                                withId(R.id.date),
                                withText(Utils.convertDate(samplePreviousMatch.date))
                            )
                        )
                    )
                )
            )

    }

    @Test
    fun checkPreviousMatchItemsHomeIsDisplayedCorrectly() {
        launchMatchesFragmentForAllMatch()
        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))
        onView(withId(R.id.prevousRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(allOf(withId(R.id.home), withText(samplePreviousMatch.home)))
                    )
                )
            )
    }


    @Test
    fun checkPreviousMatchItemsAwayIsDisplayedCorrectly() {
        launchMatchesFragmentForAllMatch()

        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))
        onView(withId(R.id.prevousRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(allOf(withId(R.id.away), withText(samplePreviousMatch.away)))
                    )
                )
            )

    }

    @Test
    fun checkPreviousMatchItemsNotifyMeNotDisplayed() {
        launchMatchesFragmentForAllMatch()
        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))
        onView(withId(R.id.prevousRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(allOf(withId(R.id.notifyMe), not(isDisplayed())))
                    )
                )
            )
    }

    @Test
    fun checkPreviousMatchItemsShowHighlightsButtonDisplayed() {
        launchMatchesFragmentForAllMatch()

        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))
        onView(withId(R.id.prevousRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(
                            allOf(
                                withId(R.id.viewHighlight),
                                withText(R.string.watch_highlights)
                            )
                        )
                    )
                )
            )
    }

    @Test
    fun checkPreviousMatchItemsWinnerTitleDisplayed() {
        launchMatchesFragmentForAllMatch()
        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))
        onView(withId(R.id.prevousRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(allOf(withId(R.id.winnerTitle), withText(R.string.winner)))
                    )
                )
            )
    }

    @Test
    fun checkPreviousMatchItemsWinnerDisplayed() {
        launchMatchesFragmentForAllMatch()
        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))
        onView(withId(R.id.prevousRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(
                matches(
                    atPosition(
                        0,
                        hasDescendant(
                            allOf(
                                withId(R.id.winnerName),
                                withText(samplePreviousMatch.winner)
                            )
                        )
                    )
                )
            )
    }

    @Test
    fun checkPreviousMatchesLastItemDisplayed() {
        launchMatchesFragmentForAllMatch()
        val matches = mutableListOf<Match>()
        repeat(50) {
            matches.add(samplePreviousMatch)
        }
        (repository as FakeMatchRepository).addPreviousMatches(matches)

        val positionToCheck = matches.size - 1
        onView(withId(R.id.prevousRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(positionToCheck))
            .check(
                matches(
                    atPosition(
                        positionToCheck,
                        hasDescendant(allOf(withId(R.id.winnerName), isDisplayed()))
                    )
                )
            )
    }

    //Please turn off animations from developer option to pass this test
    @Test
    fun checkUpcomingMatchesLastItemDisplayed() {
        launchMatchesFragmentForAllMatch()
        val matches = mutableListOf<Match>()
        repeat(50) {
            matches.add(sampleUpcomingMatch)
        }
        (repository as FakeMatchRepository).addUpcomingMatches(matches)

        val positionToCheck = matches.size - 1
        onView(withId(R.id.upcomingRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(positionToCheck))
            .check(
                matches(
                    atPosition(
                        positionToCheck,
                        hasDescendant(allOf(withId(R.id.home), isDisplayed()))
                    )
                )
            )
    }


    @Test
    fun checkPreviousMatchesRecyclerViewDisplayedForTeam() {
        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))
        launchMatchesFragmentForTeam()

        // Use Espresso to perform assertions on the fragment's view
        onView(withId(R.id.prevousRecycler))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkUpcomingMatchesRecyclerViewDisplayedForTeam() {
        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        launchMatchesFragmentForTeam()

        // Use Espresso to perform assertions on the fragment's view
        onView(withId(R.id.upcomingRecycler))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkBothRecyclerViewDisplayedForTeam() {
        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))
        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        launchMatchesFragmentForTeam()
        onView(withId(R.id.upcomingRecycler))
            .check(matches(isDisplayed()))
        onView(withId(R.id.prevousRecycler))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkAllLabelsAndRecyclerViewsDisplayedForTem() {
        (repository as FakeMatchRepository).addUpcomingMatches(listOf(sampleUpcomingMatch))
        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))
        launchMatchesFragmentForTeam()

        onView(withText(R.string.previous_matches)).check(matches(isDisplayed()))

        onView(withText(R.string.upcoming_matches)).check(matches(isDisplayed()))

        onView(withId(R.id.upcomingRecycler))
            .check(matches(isDisplayed()))

        onView(withId(R.id.prevousRecycler))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkWatchHighlightsButtonClick() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<MatchesFragment>(
            Bundle(),
            R.style.Theme_KickoffByZujuInterview
        ) {
            navController.setGraph(R.navigation.main_nav)
            navController.setCurrentDestination(R.id.matchesFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
        (repository as FakeMatchRepository).addPreviousMatches(listOf(samplePreviousMatch))
        onView(withId(R.id.prevousRecycler))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    clickChildViewWithId(R.id.viewHighlight)
                )
            )
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.videoViewerFragment)
    }

    private fun launchMatchesFragmentForAllMatch() {
        val bundle = Bundle().apply {
            putParcelable("team", null)
        }
        launchFragmentInHiltContainer<MatchesFragment>(
            MatchesFragmentArgs.fromBundle(bundle).toBundle()
        )
    }

    private fun launchMatchesFragmentForTeam() {
        val team = Team("1", "Real Madrid", "sada")
        val bundle = Bundle().apply {
            putParcelable("team", team)
        }
        launchFragmentInHiltContainer<MatchesFragment>(
            MatchesFragmentArgs.fromBundle(bundle).toBundle()
        )
    }


}