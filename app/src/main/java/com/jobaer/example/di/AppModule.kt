package com.jobaer.example.di

import android.content.Context
import androidx.room.Room
import com.jobaer.example.data.MatchRepository
import com.jobaer.example.data.MatchRepositoryImpl
import com.jobaer.example.data.TeamRepository
import com.jobaer.example.data.TeamRepositoryImpl
import com.jobaer.example.data.local.AppDatabase
import com.jobaer.example.data.local.MatchDao
import com.jobaer.example.data.local.TeamDao
import com.jobaer.example.data.remote.ApiClient
import com.jobaer.example.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        return ApiClient.getClient().create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .build()
    }

    @Singleton
    @Provides
    fun provideTeamDao(appDatabase: AppDatabase): TeamDao {
        return appDatabase.teamDao()
    }

    @Singleton
    @Provides
    fun provideMatchDao(appDatabase: AppDatabase): MatchDao {
        return appDatabase.matchDao()
    }

    @Singleton
    @Provides
    fun provideTeamRepository(teamDao: TeamDao, apiService: ApiService): TeamRepository {
        return TeamRepositoryImpl(teamDao, apiService)
    }

    @Singleton
    @Provides
    fun provideMatchRepository(matchDao: MatchDao, apiService: ApiService): MatchRepository {
        return MatchRepositoryImpl(matchDao, apiService)
    }
}