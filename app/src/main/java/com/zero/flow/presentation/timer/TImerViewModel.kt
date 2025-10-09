package com.zero.flow.presentation.timer


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zero.flow.domain.model.*
import com.zero.flow.domain.repository.SessionRepository
import com.zero.flow.domain.repository.SettingsRepository
import com.zero.flow.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject



sealed class TimerEvent {
    data object StartTimer : TimerEvent()
    data object PauseTimer : TimerEvent()
    data object ResetTimer : TimerEvent()
    data object SkipSession : TimerEvent()
    data class SelectSessionType(val sessionType: SessionType) : TimerEvent()
    data class SelectTask(val task: Task?) : TimerEvent()
}

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val settingsRepository: SettingsRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var sessionStartTime: LocalDateTime? = null
    private var selectedSessionType = SessionType.FOCUS

    init {
        observeSettings()
        loadTodaySessions()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }

    private fun loadTodaySessions() {
        viewModelScope.launch {
            val today = LocalDateTime.now()
            val count = sessionRepository.getSessionsCountForDate(today)
            _uiState.update { it.copy(completedSessionsCount = count) }
        }
    }

    fun onEvent(event: TimerEvent) {
        when (event) {
            is TimerEvent.StartTimer -> startTimer()
            is TimerEvent.PauseTimer -> pauseTimer()
            is TimerEvent.ResetTimer -> resetTimer()
            is TimerEvent.SkipSession -> skipSession()
            is TimerEvent.SelectSessionType -> {
                selectedSessionType = event.sessionType
                resetTimer()
            }
            is TimerEvent.SelectTask -> {
                _uiState.update { it.copy(currentTask = event.task) }
            }
        }
    }

    private fun startTimer() {
        val currentState = _uiState.value.timerState

        when (currentState) {
            is TimerState.Idle -> {
                sessionStartTime = LocalDateTime.now()
                val duration = getSessionDuration(selectedSessionType)
                startCountdown(selectedSessionType, duration, _uiState.value.currentTask)
            }
            is TimerState.Paused -> {
                startCountdown(
                    currentState.sessionType,
                    currentState.remainingTimeMs,
                    currentState.currentTask
                )
            }
            else -> {}
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        val currentState = _uiState.value.timerState

        if (currentState is TimerState.Running) {
            _uiState.update {
                it.copy(
                    timerState = TimerState.Paused(
                        sessionType = currentState.sessionType,
                        remainingTimeMs = currentState.remainingTimeMs,
                        totalTimeMs = currentState.totalTimeMs,
                        currentTask = currentState.currentTask
                    )
                )
            }
        }
    }

    private fun resetTimer() {
        timerJob?.cancel()
        sessionStartTime = null
        _uiState.update { it.copy(timerState = TimerState.Idle) }
    }

    private fun skipSession() {
        timerJob?.cancel()
        completeSession(false)
        resetTimer()
    }

    private fun startCountdown(
        sessionType: SessionType,
        durationMs: Long,
        task: Task?
    ) {
        val totalDuration = if (durationMs == getSessionDuration(sessionType)) {
            durationMs
        } else {
            // For resumed sessions, use the original total duration
            (_uiState.value.timerState as? TimerState.Paused)?.totalTimeMs
                ?: getSessionDuration(sessionType)
        }

        _uiState.update {
            it.copy(
                timerState = TimerState.Running(
                    sessionType = sessionType,
                    remainingTimeMs = durationMs,
                    totalTimeMs = totalDuration,
                    currentTask = task
                )
            )
        }

        timerJob = viewModelScope.launch {
            var remaining = durationMs

            while (remaining > 0) {
                delay(1000)
                remaining -= 1000

                _uiState.update {
                    it.copy(
                        timerState = TimerState.Running(
                            sessionType = sessionType,
                            remainingTimeMs = remaining,
                            totalTimeMs = totalDuration,
                            currentTask = task
                        )
                    )
                }
            }

            completeSession(true)
            _uiState.update {
                it.copy(timerState = TimerState.Completed(sessionType))
            }

            delay(3000) // Show completed state for 3 seconds
            handleSessionCompletion(sessionType)
        }
    }

    private fun completeSession(completed: Boolean) {
        val currentState = _uiState.value.timerState

        if (currentState is TimerState.Running && completed) {
            viewModelScope.launch {
                val session = Session(
                    sessionType = currentState.sessionType,
                    durationMs = currentState.totalTimeMs,
                    startTime = sessionStartTime ?: LocalDateTime.now(),
                    endTime = LocalDateTime.now(),
                    completed = true,
                    taskId = currentState.currentTask?.id
                )

                sessionRepository.insertSession(session)

                // Increment task pomodoros if it's a focus session
                if (currentState.sessionType == SessionType.FOCUS) {
                    currentState.currentTask?.let { task ->
                        taskRepository.incrementTaskPomodoros(task.id)
                    }
                    loadTodaySessions()
                }
            }
        }
    }

    private fun handleSessionCompletion(completedType: SessionType) {
        val settings = _uiState.value.settings

        val nextType = when (completedType) {
            SessionType.FOCUS -> {
                val shouldLongBreak = (_uiState.value.completedSessionsCount + 1) %
                        settings.sessionsUntilLongBreak == 0
                if (shouldLongBreak) SessionType.LONG_BREAK else SessionType.SHORT_BREAK
            }
            SessionType.SHORT_BREAK, SessionType.LONG_BREAK -> SessionType.FOCUS
        }

        selectedSessionType = nextType

        val autoStart = when (nextType) {
            SessionType.FOCUS -> settings.autoStartPomodoros
            else -> settings.autoStartBreaks
        }

        if (autoStart) {
            startTimer()
        } else {
            resetTimer()
        }
    }

    private fun getSessionDuration(sessionType: SessionType): Long {
        val settings = _uiState.value.settings
        val minutes = when (sessionType) {
//            SessionType.FOCUS -> settings.focusDurationMinutes
            SessionType.FOCUS -> settings.focusDuration
//            SessionType.SHORT_BREAK -> settings.shortBreakDurationMinutes
            SessionType.SHORT_BREAK -> settings.shortBreakDuration
//            SessionType.LONG_BREAK -> settings.longBreakDurationMinutes
            SessionType.LONG_BREAK -> settings.longBreakDuration
        }
        return minutes * 60 * 1000L
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}