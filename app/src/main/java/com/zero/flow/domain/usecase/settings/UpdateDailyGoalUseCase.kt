package com.zero.flow.domain.usecase.settings

import com.zero.flow.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for updating daily goal
 */
class UpdateDailyGoalUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(sessions: Int): Result<Unit> {
        return try {
            if (sessions < 1 || sessions > 20) {
                return Result.failure(
                    IllegalArgumentException("Daily goal must be between 1 and 20 sessions")
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}