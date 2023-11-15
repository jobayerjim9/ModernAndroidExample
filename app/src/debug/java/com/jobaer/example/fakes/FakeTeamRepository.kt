package com.jobaer.example.fakes

import com.jobaer.example.data.TeamRepository
import com.jobaer.example.models.Team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

class FakeTeamRepository : TeamRepository {
    private val fakeTeams = mutableListOf<Team>()

    private val shouldThrowNetworkErrorFlow = MutableStateFlow<Boolean>(false)
    private val shouldThrowErrorFlow = MutableStateFlow<Boolean>(false)

    override val teams: Flow<List<Team>> = flowOf(fakeTeams)

    override suspend fun getTeams() {
        if (shouldThrowNetworkErrorFlow.value) {
            throw HttpException(Response.error<Any>(400, "Network error".toResponseBody("plain/text".toMediaTypeOrNull())))
        } else if (shouldThrowErrorFlow.value) {
            throw RuntimeException("Custom error message")
        }
    }

    fun setShouldThrowNetworkError(shouldThrow: Boolean) {
        shouldThrowNetworkErrorFlow.value = shouldThrow
    }

    fun setShouldThrowError(shouldThrow: Boolean) {
        shouldThrowErrorFlow.value = shouldThrow
    }
    fun addTeams(team: List<Team>) {
        fakeTeams.addAll(team)
    }
}

