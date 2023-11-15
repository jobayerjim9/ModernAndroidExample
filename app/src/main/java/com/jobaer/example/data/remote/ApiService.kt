package com.jobaer.example.data.remote

import com.jobaer.example.models.MatchesResponse
import com.jobaer.example.models.TeamResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("teams")
    suspend fun getTeams(): TeamResponse

    @GET("teams/matches")
    suspend fun getMatches(): MatchesResponse

    @GET("teams/{id}/matches")
    suspend fun getMatchByTeam(@Path("id") id: String): MatchesResponse
}