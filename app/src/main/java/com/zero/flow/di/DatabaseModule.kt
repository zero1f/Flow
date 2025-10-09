package com.zero.flow.di

import android.content.Context
import androidx.room.Room
import com.zero.flow.data.local.database.FlowDatabase
import com.zero.flow.data.local.dao.SessionDao
import com.zero.flow.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFlowDatabase(
        @ApplicationContext context: Context
    ): FlowDatabase {
        return Room.databaseBuilder(
            context,
            FlowDatabase::class.java,
            "flow_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideSessionDao(database: FlowDatabase): SessionDao {
        return database.sessionDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: FlowDatabase): TaskDao {
        return database.taskDao()
    }
}

