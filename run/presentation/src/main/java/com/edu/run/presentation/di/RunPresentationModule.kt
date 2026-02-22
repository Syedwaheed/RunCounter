package com.edu.run.presentation.di

import androidx.compose.ui.text.font.FontVariation.Setting
import com.edu.run.domain.RunningTracker
import com.edu.run.presentation.active_run.ActiveRunViewModel
import com.edu.run.presentation.analytics.AnalyticsViewModel
import com.edu.run.presentation.charts.ChartsScreenViewModel
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
}