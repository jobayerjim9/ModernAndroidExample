package com.jobaer.example.data.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private lateinit var retrofit: Retrofit
    private const val BASE_URL = "https://jmde6xvjr4.execute-api.us-east-1.amazonaws.com/"

    fun getClient(): Retrofit {
        if (::retrofit.isInitialized) {
            return retrofit
        }
        val gson = GsonBuilder()
            .setLenient()
            .create()
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit
    }
}