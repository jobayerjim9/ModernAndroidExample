package com.jobaer.example.utils

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Job

interface LoadDataViewModel {
    val _message: MutableLiveData<String?>
    val _loader: MutableLiveData<Boolean>

    fun loadData(block: suspend () -> Unit): Job
}