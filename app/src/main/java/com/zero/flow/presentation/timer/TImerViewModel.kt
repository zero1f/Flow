package com.zero.flow.presentation.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zero.flow.domain.model.SessionType
import com.zero.flow.domain.model.Task
import com.zero.flow.domain.model.TimerState
import com.zero.flow.domain.repository.SessionRepository
import com.zero.flow.domain.repository.SettingsRepository
import com.zero.flow.service.TimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

sealed class TimerEvent {
    data class StartTimer(val context: Context) : TimerEvent()
    data class PauseTimer(val context: Context) : TimerEvent()
    data class ResetTimer(val context: Context) : TimerEvent()
    data class SkipTimer(val context: Context) : TimerEvent()
    data class SelectSessionType(val sessionType: SessionType) : TimerEvent()
    data class SelectTask(val task: Task?) : TimerEvent()
}

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerService: TimerService? = null
    private var isServiceBound = false
    private var serviceStateJob: Job? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            isServiceBound = true
            observeServiceState()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
            isServiceBound = false
            serviceStateJob?.cancel()
        }
    }

    init {
        observeSettings()
        loadTodaySessions()
    }

    fun bindService(context: Context) {
        if (!isServiceBound) {
            val intent = TimerService.newIntent(context)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindService(context: Context) {
        if (isServiceBound) {
            context.unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    private fun observeServiceState() {
        serviceStateJob?.cancel()
        serviceStateJob = viewModelScope.launch {
            timerService?.timerState?.collect { timerState ->
                _uiState.update { it.copy(timerState = timerState) }
                if (timerState is TimerState.Completed) {
                    onSessionCompleted(timerState.sessionType)
                }
            }
        }
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
            is TimerEvent.StartTimer -> startTimer(event.context)
            is TimerEvent.PauseTimer -> sendCommandToService(event.context, TimerService.ACTION_PAUSE_TIMER)
            is TimerEvent.ResetTimer -> {
                sendCommandToService(event.context, TimerService.ACTION_STOP_TIMER)
                // Manually reset cycle state on explicit reset
                _uiState.update { it.copy(focusSessionsInCycle = 0, selectedSessionType = SessionType.FOCUS) }
            }
            is TimerEvent.SkipTimer -> skipTimer(event.context)
            is TimerEvent.SelectSessionType -> {
                if (_uiState.value.timerState is TimerState.Idle) {
                    _uiState.update { it.copy(selectedSessionType = event.sessionType) }
                }
            }
            is TimerEvent.SelectTask -> {
                _uiState.update { it.copy(currentTask = event.task) }
            }
        }
    }

    private fun startTimer(context: Context) {
        val sessionType = _uiState.value.selectedSessionType
        val intent = TimerService.newIntent(context).apply {
            action = TimerService.ACTION_START_TIMER
            putExtra(TimerService.EXTRA_SESSION_TYPE, sessionType)
            putExtra(TimerService.EXTRA_TOTAL_TIME, getSessionDuration(sessionType))
        }
        context.startService(intent)
    }

    private fun skipTimer(context: Context) {
        sendCommandToService(context, TimerService.ACTION_STOP_TIMER)
        // Treat skip as a completed session for cycle purposes
        onSessionCompleted(_uiState.value.selectedSessionType, autoStartNext = true)
    }

    private fun onSessionCompleted(completedType: SessionType, autoStartNext: Boolean = true) {
        viewModelScope.launch {
            var currentCycleCount = _uiState.value.focusSessionsInCycle
            if (completedType == SessionType.FOCUS) {
                currentCycleCount++
            }

            val nextSessionType = if (completedType == SessionType.FOCUS && currentCycleCount >= _uiState.value.settings.longBreakInterval) {
                _uiState.update { it.copy(focusSessionsInCycle = 0) }
                SessionType.LONG_BREAK
            } else if (completedType == SessionType.FOCUS) {
                _uiState.update { it.copy(focusSessionsInCycle = currentCycleCount) }
                SessionType.SHORT_BREAK
            } else {
                SessionType.FOCUS
            }

            _uiState.update { it.copy(selectedSessionType = nextSessionType) }

            if (autoStartNext && _uiState.value.settings.autoStartNextSession) {
                // Use the application context from the service to avoid issues with Activity context
                timerService?.let { service ->
                    startTimer(service.applicationContext)
                }
            }
        }
    }

    private fun sendCommandToService(context: Context, action: String) {
        val intent = TimerService.newIntent(context).apply { this.action = action }
        context.startService(intent)
    }

    private fun getSessionDuration(sessionType: SessionType): Long {
        val settings = _uiState.value.settings
        val minutes = when (sessionType) {
            SessionType.FOCUS -> settings.focusDuration
            SessionType.SHORT_BREAK -> settings.shortBreakDuration
            SessionType.LONG_BREAK -> settings.longBreakDuration
        }
        return minutes * 60 * 1000L
    }

    override fun onCleared() {
        if (isServiceBound) {
            timerService?.let {
                it.applicationContext.unbindService(serviceConnection)
                isServiceBound = false
            }
        }
        super.onCleared()
    }
}
