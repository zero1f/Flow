package com.zero.flow.presentation.statistics

sealed class StatisticsEvent {
    data class SelectPeriod(val period: TimePeriod) : StatisticsEvent()
    data object RefreshData : StatisticsEvent()
}