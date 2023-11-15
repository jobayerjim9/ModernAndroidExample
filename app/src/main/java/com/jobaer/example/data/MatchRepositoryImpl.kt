package com.jobaer.example.data

import com.jobaer.example.data.local.MatchDao
import com.jobaer.example.data.remote.ApiService
import com.jobaer.example.models.AppConfig.LOCAL_DATABASE_CLEANUP_INTERVAL_DAYS
import com.jobaer.example.models.Match
import com.jobaer.example.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val matchDao: MatchDao, private val apiService: ApiService,
) : MatchRepository {
    override val previousMatches: Flow<List<Match>>
        get() = matchDao.getPreviousMatches()

    override val upcomingMatches: Flow<List<Match>>
        get() = matchDao.getUpcomingMatches()


    override suspend fun getMatches() {
        withContext(Dispatchers.IO) {
            val match = apiService.getMatches().matches
            matchDao.insertAllMatch(match.upcoming)
            matchDao.insertAllMatch(match.previous)
        }
    }

    override suspend fun getAllMatchByTeam(id: String) {
        withContext(Dispatchers.IO) {
            val match = apiService.getMatchByTeam(id).matches
            matchDao.insertAllMatch(match.upcoming)
            matchDao.insertAllMatch(match.previous)
        }
    }


    override suspend fun getUpcomingMatchByTeam(name: String): Flow<List<Match>> {
        return withContext(Dispatchers.IO) {
            matchDao.getUpcomingMatchByTeam(name)
        }
    }

    override suspend fun getPreviousMatchByTeam(name: String): Flow<List<Match>> {
        return withContext(Dispatchers.IO) {
            matchDao.getPreviousMatchByTeam(name)
        }
    }

    override suspend fun cleanupOldMatches() {
        withContext(Dispatchers.IO) {
            matchDao.deleteOldMatches(
                Utils.formatDateToISOString(
                    Utils.calculateXDaysAgo(
                        LOCAL_DATABASE_CLEANUP_INTERVAL_DAYS
                    )
                )
            )
        }
    }

    override suspend fun updateMatch(id: Long) {
        withContext(Dispatchers.IO) {
            matchDao.updateMatch(id)
        }

    }
}