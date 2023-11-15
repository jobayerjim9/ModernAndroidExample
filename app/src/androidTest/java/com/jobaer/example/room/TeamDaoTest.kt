package com.jobaer.example.room

import androidx.room.Room
import androidx.test.filters.SmallTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.jobaer.example.data.local.AppDatabase
import com.jobaer.example.data.local.TeamDao
import com.jobaer.example.models.Team
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@SmallTest
class TeamDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var teamDao: TeamDao

    @Before
    fun setupDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        teamDao = database.teamDao()
    }

    @Test
    fun insertAndRetrieveTeams() = runBlocking {
        val team1 = Team("1", "Team 1", "Logo 1")
        val team2 = Team("2", "Team 2", "Logo 2")

        // Insert teams
        teamDao.insertAll(listOf(team1, team2))

        // Retrieve teams using Flow
        val teams = teamDao.getAllTeams().first()
        assertThat(teams.size).isEqualTo(2)
        assertThat(teams).contains(team1)
        assertThat(teams).contains(team2)
    }

    @Test
    fun insertAndReplaceTeams() = runBlocking {
        val team1 = Team("1", "Team 1", "Logo 1")
        val team2 = Team("1", "Team 2", "Logo 2") // Same ID as team1

        // Insert the first team
        teamDao.insertAll(listOf(team1))

        // Replace it with the second team
        teamDao.insertAll(listOf(team2))

        // Retrieve teams using Flow
        val teams = teamDao.getAllTeams().first()

        assertThat(teams.size).isEqualTo(1)
        assertThat(teams).contains(team2)
    }


    @After
    fun closeDatabase() {
        database.close()
    }
}