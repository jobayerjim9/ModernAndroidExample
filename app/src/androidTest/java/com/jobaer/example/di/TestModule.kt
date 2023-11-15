package com.jobaer.example.di

import com.jobaer.example.data.MatchRepository
import com.jobaer.example.data.TeamRepository
import com.jobaer.example.fakes.FakeMatchRepository
import com.jobaer.example.fakes.FakeTeamRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestModule {
    @Singleton
    @Provides
    fun provideMatchRepository() : MatchRepository {
        return FakeMatchRepository()
    }

    @Singleton
    @Provides
    fun provideTeamRepository() : TeamRepository {
        return FakeTeamRepository()
    }
}