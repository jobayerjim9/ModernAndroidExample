package com.jobaer.example.data

import com.jobaer.example.data.local.TeamDao
import com.jobaer.example.data.remote.ApiService
import com.jobaer.example.models.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val teamDao: TeamDao, private val apiService: ApiService,
) : TeamRepository {
    override val teams: Flow<List<Team>>
        get() = teamDao.getAllTeams()


    override suspend fun getTeams() {
        withContext(Dispatchers.IO) {
            val teams = apiService.getTeams().teams
            teamDao.insertAll(teams)
        }
    }

}