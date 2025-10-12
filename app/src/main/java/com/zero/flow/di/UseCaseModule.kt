package com.zero.flow.di

import com.zero.flow.domain.repository.SessionRepository
import com.zero.flow.domain.repository.SettingsRepository
import com.zero.flow.domain.repository.TaskRepository
import com.zero.flow.domain.usecase.session.*
import com.zero.flow.domain.usecase.settings.*
import com.zero.flow.domain.usecase.task.*
import com.zero.flow.domain.usecase.timer.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // Task Use Cases
    @Provides
    @Singleton
    fun provideGetTasksUseCase(repository: TaskRepository) = GetTasksUseCase(repository)

    @Provides
    @Singleton
    fun provideCreateTaskUseCase(repository: TaskRepository) = CreateTaskUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateTaskUseCase(repository: TaskRepository) = UpdateTaskUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteTaskUseCase(repository: TaskRepository) = DeleteTaskUseCase(repository)

    @Provides
    @Singleton
    fun provideGetTaskByIdUseCase(repository: TaskRepository) = GetTaskByIdUseCase(repository)

    @Provides
    @Singleton
    fun provideCompleteTaskUseCase(repository: TaskRepository) = CompleteTaskUseCase(repository)

    @Provides
    @Singleton
    fun provideUncompleteTaskUseCase(repository: TaskRepository) = UncompleteTaskUseCase(repository)

    @Provides
    @Singleton
    fun provideGetActiveTasksUseCase(repository: TaskRepository) = GetActiveTasksUseCase(repository)

    @Provides
    @Singleton
    fun provideAutoCompleteTaskUseCase(repository: TaskRepository) = AutoCompleteTaskUseCase(repository,
        CheckTaskCompletionUseCase(repository))

    @Provides
    @Singleton
    fun provideCheckTaskCompletionUseCase(repository: TaskRepository) = CheckTaskCompletionUseCase(repository)

    @Provides
    @Singleton
    fun provideIncrementTaskPomodorosUseCase(repository: TaskRepository) = IncrementTaskPomodorosUseCase(repository)

    // Session Use Cases
    @Provides
    @Singleton
    fun provideGetSessionUseCase(repository: SessionRepository) = GetSessionsUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteSessionUseCase(repository: SessionRepository) = DeleteSessionUseCase(repository)

    @Provides
    @Singleton
    fun provideGetSessionStatsUseCase(repository: SessionRepository) = GetSessionStatsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetDailyStatisticsUseCase(repository: SessionRepository) = GetDailyStatisticsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetSessionsInRangeUseCase(repository: SessionRepository) = GetSessionsInRangeUseCase(repository)

    @Provides
    @Singleton
    fun provideGetTodaySessionCountUseCase(repository: SessionRepository) = GetTodaySessionCountUseCase(repository)

    // Settings Use Cases

    @Provides
    @Singleton
    fun provideGetSettingsUseCase(repository: SettingsRepository) = GetSettingsUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateThemeUseCase(repository: SettingsRepository) = UpdateThemeUseCase(repository)

    @Provides
    @Singleton
    fun provideResetSettingsUseCase(repository: SettingsRepository) = ResetSettingsUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateSettingsUseCase(repository: SettingsRepository) = UpdateSettingsUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateDailyGoalUseCase(repository: SettingsRepository) = UpdateDailyGoalUseCase(repository)

    @Provides
    @Singleton
    fun provideValidateSettingsUseCase(repository: SettingsRepository) = ValidateSettingsUseCase()

    @Provides
    @Singleton
    fun provideUpdateAmbientSoundUseCase(repository: SettingsRepository) = UpdateAmbientSoundUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateBreakDurationUseCase(repository: SettingsRepository) = UpdateBreakDurationUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateFocusDurationUseCase(repository: SettingsRepository) = UpdateFocusDurationUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateAutomaticSettingsUseCase(repository: SettingsRepository) = UpdateAutomaticSettingsUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateNotificationSettingsUseCase(repository: SettingsRepository) = UpdateNotificationSettingsUseCase(repository)

    // Timer Use Cases
    @Provides
    @Singleton
    fun provideStartTimerUseCase() = StartTimerUseCase()

    @Provides
    @Singleton
    fun providePauseTimerUseCase() = PauseTimerUseCase()

    @Provides
    @Singleton
    fun provideResetTimerUseCase() = ResetTimerUseCase()

    @Provides
    @Singleton
    fun provideSkipSessionUseCase() = SkipSessionUseCase()

    @Provides
    @Singleton
    fun provideCompleteSessionUseCase(repository: SessionRepository) = CompleteSessionUseCase(repository)
}
