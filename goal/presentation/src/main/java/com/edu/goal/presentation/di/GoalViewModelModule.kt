package com.edu.goal.presentation.di

import com.edu.goal.domain.DateFormatter
import com.edu.goal.domain.DefaultGoalValidator
import com.edu.goal.domain.GoalValidator
import com.edu.goal.presentation.GoalViewModel
import com.edu.goal.presentation.util.DefaultDateFormatter
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val goalViewModelModule = module {
    single<DateFormatter>{ DefaultDateFormatter }
    single<GoalValidator>{ DefaultGoalValidator() }
    viewModelOf(::GoalViewModel)
}