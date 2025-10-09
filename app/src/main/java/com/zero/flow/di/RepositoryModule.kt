package com.zero.flow.di

//import com.zero.flow.data.repository.SessionRepositoryImpl
//import com.zero.flow.data.UserPreferencesDataStore
//import com.zero.flow.data.local.FlowDatabase
import com.zero.flow.data.local.dao.SessionDao
import com.zero.flow.data.local.dao.TaskDao
import com.zero.flow.data.preferences.UserPreferencesDataStore
import com.zero.flow.data.repository.*
import com.zero.flow.domain.repository.SessionRepository
import com.zero.flow.domain.repository.SettingsRepository
import com.zero.flow.domain.repository.TaskRepository
import com.zero.flow.data.repository.SessionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSessionRepository(
        sessionDao: SessionDao
    ): SessionRepository {
        return SessionRepositoryImpl(sessionDao)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: TaskDao
    ): TaskRepository {
        return TaskRepositoryImpl(taskDao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: UserPreferencesDataStore
    ): SettingsRepository {
        return SettingsRepositoryImpl(dataStore)
    }
}