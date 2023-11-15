package com.jobaer.example.ui.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.jobaer.example.data.MatchRepository
import com.jobaer.example.models.Match
import com.jobaer.example.utils.CleanupWorker
import com.jobaer.example.utils.LoadDataViewModel
import com.jobaer.example.utils.MatchNotificationService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.concurrent.TimeUnit


@HiltViewModel
class MatchViewModel @Inject constructor(private val repository: MatchRepository) : ViewModel(),
    LoadDataViewModel {
    val selectedTeamName = MutableStateFlow("")

    //Data selection will be depend on selected team if no selected team it will retrieve all upcoming matches
    @ExperimentalCoroutinesApi
    val upcomingMatches: LiveData<List<Match>> = selectedTeamName.flatMapLatest { name ->
        if (name == "") {
            repository.upcomingMatches
        } else {
            repository.getUpcomingMatchByTeam(name)
        }
    }.distinctUntilChanged().asLiveData()

    //Data selection will be depend on selected team if no selected team it will retrieve all previous matches
    @ExperimentalCoroutinesApi
    val previousMatches: LiveData<List<Match>> = selectedTeamName.flatMapLatest { name ->
        if (name == "") {
            repository.previousMatches
        } else {
            repository.getPreviousMatchByTeam(name)
        }
    }.distinctUntilChanged().asLiveData()


    val message: LiveData<String?>
        get() = _message


    val loader: LiveData<Boolean>
        get() = _loader


    fun getMatches() {
        loadData {
            repository.getMatches()
        }
    }

    fun getMatchByTeam(id: String) {
        loadData {
            repository.getAllMatchByTeam(id)
        }
    }

    fun setCurrentSelectTeam(name: String) {
        selectedTeamName.value = name
    }

    fun clearCurrentSelectTeam() {
        selectedTeamName.value = ""
    }

    //This will schedule work manager to cleanup room database
    fun scheduleCleanupWork(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<CleanupWorker>(30, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "database_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    //Schedule a local notification for notifying user about match
    fun scheduleMatchNotification(context: Context, match: Match) {
        viewModelScope.launch {
            try {
                val result = MatchNotificationService(context).scheduleNotification(match)
                if (result) {
                    match.isNotificationSet = true
                    updateNotificationOfMatch(match)
                }
            } catch (e: Exception) {
                _message.value = e.localizedMessage
            }

        }
    }

    //Update isNotificationSet to true on Room database to avoid duplicate scheduling
    suspend fun updateNotificationOfMatch(match: Match) {
        repository.updateMatch(match.id)
        _message.value = "Notification set for ${match.description}}"
    }

    fun askNotificationPermission(
        context: Context,
        requestPermissionLauncher: ActivityResultLauncher<String>
    ) {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    }

    override val _message: MutableLiveData<String?> = MutableLiveData()
    override val _loader: MutableLiveData<Boolean> = MutableLiveData(false)


    override fun loadData(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _loader.value = true
                block()
            } catch (error: Throwable) {
                when (error) {
                    is HttpException -> {
                        // Handle HTTP-specific errors here

                        // Handle the error or update UI accordingly
                        _message.value = "HTTP error: ${error.code()}"
                    }

                    else -> {
                        // Handle other types of errors
                        _message.value = error.message
                    }
                }
            } finally {
                _loader.value = false
            }
        }

    }


}