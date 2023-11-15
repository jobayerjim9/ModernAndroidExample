package com.jobaer.example.utils

import androidx.lifecycle.LiveData
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object LiveDataTestUtil {
    /**
     * Get the value from a LiveData object by blocking the current thread.
     *
     * @param liveData The LiveData to observe.
     * @return The value observed.
     */
    @Throws(InterruptedException::class)
    fun <T> getValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)

        liveData.observeForever { t ->
            data[0] = t
            latch.countDown()
        }

        // Wait for the value to be set
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data[0] as T
    }
}
