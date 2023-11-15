package com.jobaer.example.room

import androidx.room.Room
import androidx.test.filters.SmallTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth
import com.jobaer.example.data.local.AppDatabase
import com.jobaer.example.data.local.MatchDao
import com.jobaer.example.models.Match
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@SmallTest
class MatchDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var matchDao: MatchDao

    @Before
    fun setupDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        matchDao = database.matchDao()
    }
    @Test
    fun insertBothAndRetrievePreviousMatches() = runBlocking {
        val previousMatch = Match(1, "2022-04-23T18:00:00.000Z", "Team Cool Eagles vs. Team Red Dragons","Team Cool Eagles","Team Red Dragons","Team Red Dragons","https://tstzj.s3.amazonaws.com/highlights.mp4")
        val upcomingMatch = Match(2, "2022-14-23T18:00:00.000Z", "Team Cool Eagles vs. Team Red Dragons","Team Cool Eagles","Team Red Dragons")

        // Insert teams
        matchDao.insertAllMatch(listOf(previousMatch, upcomingMatch))

        // Retrieve teams using Flow
        val matches = matchDao.getPreviousMatches().first()
        Truth.assertThat(matches.size).isEqualTo(1)
        Truth.assertThat(matches).contains(previousMatch)
        Truth.assertThat(matches).doesNotContain(upcomingMatch)
    }

    @Test
    fun insertBothAndRetrieveUpcomingMatches() = runBlocking {
        val previousMatch = Match(1, "2022-04-23T18:00:00.000Z", "Team Cool Eagles vs. Team Red Dragons","Team Cool Eagles","Team Red Dragons","Team Red Dragons","https://tstzj.s3.amazonaws.com/highlights.mp4")
        val upcomingMatch = Match(2, "2022-14-23T18:00:00.000Z", "Team Cool Eagles vs. Team Red Dragons","Team Cool Eagles","Team Red Dragons")

        // Insert teams
        matchDao.insertAllMatch(listOf(previousMatch, upcomingMatch))

        // Retrieve teams using Flow
        val matches = matchDao.getUpcomingMatches().first()
        Truth.assertThat(matches.size).isEqualTo(1)
        Truth.assertThat(matches).contains(upcomingMatch)
        Truth.assertThat(matches).doesNotContain(previousMatch)
    }

    @Test
    fun insertBothAndRetrieveUpcomingMatchesForTeam() = runBlocking {
        val previousMatch = Match(1, "2022-04-23T18:00:00.000Z", "Team Cool Eagles vs. Team Red Dragons","Team Cool Eagles","Team Red Dragons","Team Red Dragons","https://tstzj.s3.amazonaws.com/highlights.mp4")
        val upcomingMatch = Match(2, "2022-14-23T18:00:00.000Z", "Team Cool Eagles vs. Team Red Dragons","Team Cool Eagles","Team Red Dragons")

        // Insert teams
        matchDao.insertAllMatch(listOf(previousMatch, upcomingMatch))

        // Retrieve teams using Flow
        val matches = matchDao.getPreviousMatchByTeam("Team Cool Eagles").first()
        Truth.assertThat(matches.size).isEqualTo(1)
        Truth.assertThat(matches).contains(previousMatch)
        Truth.assertThat(matches).doesNotContain(upcomingMatch)
    }

    @Test
    fun insertBothAndRetrievePreviousMatchesForTeam() = runBlocking {
        val previousMatch = Match(1, "2022-04-23T18:00:00.000Z", "Team Cool Eagles vs. Team Red Dragons","Team Cool Eagles","Team Red Dragons","Team Red Dragons","https://tstzj.s3.amazonaws.com/highlights.mp4")
        val upcomingMatch = Match(2, "2022-14-23T18:00:00.000Z", "Team Cool Eagles vs. Team Red Dragons","Team Cool Eagles","Team Red Dragons")

        // Insert teams
        matchDao.insertAllMatch(listOf(previousMatch, upcomingMatch))

        // Retrieve teams using Flow
        val matches = matchDao.getPreviousMatchByTeam("Team Red Dragons").first()
        Truth.assertThat(matches.size).isEqualTo(1)
        Truth.assertThat(matches).contains(previousMatch)
        Truth.assertThat(matches).doesNotContain(upcomingMatch)
    }

    @Test
    fun insertBothAndCleanupDatabase() = runBlocking {
        val previousMatch = Match(1, "2022-04-23T18:00:00.000Z", "Team Cool Eagles vs. Team Red Dragons","Team Cool Eagles","Team Red Dragons","Team Red Dragons","https://tstzj.s3.amazonaws.com/highlights.mp4")
        val upcomingMatch = Match(2, "2022-14-23T18:00:00.000Z", "Team Cool Eagles vs. Team Red Dragons","Team Cool Eagles","Team Red Dragons")

        // Insert teams
        matchDao.insertAllMatch(listOf(previousMatch, upcomingMatch))

        //Cleanup
        matchDao.deleteOldMatches("2022-13-23T18:00:00.000Z")

        // Retrieve teams using Flow
        val matches = matchDao.getPreviousMatches().first()
        Truth.assertThat(matches.size).isEqualTo(0)
    }

    @After
    fun closeDatabase() {
        database.close()
    }
}