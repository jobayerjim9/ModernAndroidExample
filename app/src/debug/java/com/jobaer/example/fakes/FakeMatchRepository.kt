package com.jobaer.example.fakes

import com.jobaer.example.data.MatchRepository
import com.jobaer.example.models.Match
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

class FakeMatchRepository : MatchRepository {
    private val fakePreviousMatch = mutableListOf<Match>()
    //        Match(1, "2022-04-23T18:00:00.000Z", "Team Cool Eagles vs. Team Red Dragons","Team Cool Eagles","Team Red Dragons","Team Red Dragons","https://tstzj.s3.amazonaws.com/highlights.mp4"),
    //        Match(2, "2022-05-23T18:00:00.000Z", "ABC vs. DEF","ABC","DEF","DEF","https://tstzj.s3.amazonaws.com/highlights.mp4"),
    private val fakeUpcomingMatch = mutableListOf<Match>()
//    Match(1, "2022-14-23T18:00:00.000Z", "Team Cool Eagles vs. Team Red Dragons","Team Cool Eagles","Team Red Dragons"),
//    Match(2, "2022-15-23T18:00:00.000Z", "ABC vs. DEF","ABC","DEF"),
    private val shouldThrowNetworkErrorFlow = MutableStateFlow(false)
    private val shouldThrowErrorFlow = MutableStateFlow(false)


    override val previousMatches: Flow<List<Match>>
        get() = flowOf(fakePreviousMatch)
    override val upcomingMatches: Flow<List<Match>>
        get() = flowOf(fakeUpcomingMatch)


    override suspend fun getMatches() {
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

    override suspend fun getAllMatchByTeam(id: String) {
        if (shouldThrowNetworkErrorFlow.value) {
            throw HttpException(Response.error<Any>(400, "Network error".toResponseBody("plain/text".toMediaTypeOrNull())))
        } else if (shouldThrowErrorFlow.value ) {
            throw RuntimeException("Custom error message")
        } else {
            // Simulate fetching matches by team from the API and populating the repository
            // In a real implementation, you would update the data lists with actual API data.
        }
    }

    override suspend fun getUpcomingMatchByTeam(name: String): Flow<List<Match>> {
        if (shouldThrowNetworkErrorFlow.value) {
            throw HttpException(Response.error<Any>(400, "Network error".toResponseBody("plain/text".toMediaTypeOrNull())))
        } else if (shouldThrowErrorFlow.value ) {
            throw RuntimeException("Custom error message")
        } else {
            // Return filtered data for previous matches by team
            val filteredMatches = fakeUpcomingMatch.filter { it.home == name || it.away == name }
            return flowOf(filteredMatches)
        }
    }

    override suspend fun getPreviousMatchByTeam(name: String): Flow<List<Match>> {
        if (shouldThrowNetworkErrorFlow.value) {
            throw HttpException(Response.error<Any>(400, "Network error".toResponseBody("plain/text".toMediaTypeOrNull())))
        } else if (shouldThrowErrorFlow.value ) {
            throw RuntimeException("Custom error message")
        } else {
            // Return filtered data for previous matches by team
            val filteredMatches = fakePreviousMatch.filter { it.home == name || it.away == name }
            return flowOf(filteredMatches)
        }
    }

    override suspend fun cleanupOldMatches() {
        val filtered = fakePreviousMatch.filter { it.date!! <  "2022-04-23T18:00:00.000Z"}
        fakePreviousMatch.clear()
        fakePreviousMatch.addAll(filtered)
    }

    override suspend fun updateMatch(id: Long) {
        val match = fakeUpcomingMatch.find { match -> match.id == id }
        match?.isNotificationSet =true
    }

    fun addUpcomingMatches(matches: List<Match>) {
        fakeUpcomingMatch.addAll(matches)
    }

    fun addPreviousMatches(matches: List<Match>) {
        fakePreviousMatch.addAll(matches)
    }
}
