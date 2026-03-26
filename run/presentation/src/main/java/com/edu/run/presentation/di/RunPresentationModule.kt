package com.edu.run.presentation.di

import com.edu.run.domain.RunningTracker
import com.edu.run.presentation.active_run.ActiveRunViewModel
import com.edu.run.presentation.analytics.AnalyticsViewModel
import com.edu.run.presentation.charts.ChartsScreenViewModel
import com.edu.run.presentation.run_detail.RunDetailViewModel
import com.edu.run.presentation.run_overview.RunOverviewViewModel
import com.edu.run.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val runPresentationModule = module{
    singleOf(::RunningTracker)
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
    viewModelOf(::AnalyticsViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ChartsScreenViewModel)

    // ViewModel with parameter - runId is passed via parametersOf()
    viewModelOf(::RunDetailViewModel)
}