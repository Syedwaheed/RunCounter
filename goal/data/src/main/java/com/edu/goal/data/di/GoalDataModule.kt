package com.edu.goal.data.di

import com.edu.core.database.RunDatabase
import com.edu.goal.data.LocalGoalRepository
import com.edu.goal.domain.GoalRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val goalDataModule = module{
    singleOf(::LocalGoalRepository).bind<GoalRepository>()
}