package com.jobaer.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jobaer.example.models.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllMatch(upcomingMatches: List<Match>)

    @Query("SELECT * FROM matches WHERE winner == '' OR winner is NULL")
    fun getUpcomingMatches(): Flow<List<Match>>

    @Query("SELECT * FROM matches WHERE winner IS NOT NULL AND winner != ''")
    fun getPreviousMatches(): Flow<List<Match>>

    @Query("SELECT * FROM matches WHERE (home == :name OR away == :name) AND (winner == '' OR winner is NULL)")
    fun getUpcomingMatchByTeam(name: String): Flow<List<Match>>

    @Query("SELECT * FROM matches WHERE (home == :name OR away == :name) AND (winner IS NOT NULL AND winner != '')")
    fun getPreviousMatchByTeam(name: String): Flow<List<Match>>

    @Query("DELETE FROM matches WHERE date < :formattedThresholdDate")
    suspend fun deleteOldMatches(formattedThresholdDate: String)

    @Query("UPDATE matches SET isNotificationSet = 1 WHERE id = :id")
    suspend fun updateMatch(id: Long)
}