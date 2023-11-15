package com.jobaer.example.data

import com.jobaer.example.models.Team
import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    val teams: Flow<List<Team>>

    suspend fun getTeams()

}