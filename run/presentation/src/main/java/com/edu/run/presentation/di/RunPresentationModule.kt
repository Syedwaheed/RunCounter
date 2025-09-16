package com.edu.run.presentation.di

import com.edu.run.domain.RunningTracker
import com.edu.run.presentation.active_run.ActiveRunViewModel
import com.edu.run.presentation.run_overview.RunOverviewViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val runPresentationModule = module{
    singleOf(::RunningTracker)
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}