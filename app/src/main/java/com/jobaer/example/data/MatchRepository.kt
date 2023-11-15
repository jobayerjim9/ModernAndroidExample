package com.jobaer.example.data

import com.jobaer.example.models.Match
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    val previousMatches: Flow<List<Match>>
    val upcomingMatches: Flow<List<Match>>

    suspend fun getMatches()
    suspend fun getAllMatchByTeam(id:String)
    suspend fun getUpcomingMatchByTeam(name:String) : Flow<List<Match>>
    suspend fun getPreviousMatchByTeam(name:String) : Flow<List<Match>>
    suspend fun cleanupOldMatches()
    suspend fun updateMatch(id: Long)
}