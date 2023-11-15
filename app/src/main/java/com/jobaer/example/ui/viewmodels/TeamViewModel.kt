package com.jobaer.example.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.jobaer.example.data.TeamRepository
import com.jobaer.example.models.Team
import com.jobaer.example.utils.LoadDataViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import retrofit2.HttpException

@HiltViewModel
class TeamViewModel @Inject constructor(private val repository: TeamRepository) : ViewModel(),LoadDataViewModel {
    val teams: LiveData<List<Team>> = repository.teams.distinctUntilChanged().asLiveData()

    override val _message = MutableLiveData<String?>()

    val message: LiveData<String?>
        get() = _message

    override val _loader = MutableLiveData(false)

    val loader: LiveData<Boolean>
        get() = _loader

    fun getTeams() {
        loadData {
            repository.getTeams()
        }
    }

    override fun loadData(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _loader.value = true
                block()
            } catch (error: Exception) {
                println(error.message)
                when (error) {
                    is HttpException -> {
                        // Handle HTTP-specific errors here // Error message from the server
                       // Handle the error or update UI accordingly
                        _message.postValue("HTTP error: ${error.code()}")
                    }
                    else -> {
                        // Handle other types of errors
                        _message.postValue(error.message)
                    }
                }
            } finally {
                _loader.value = false
            }
        }

    }
}