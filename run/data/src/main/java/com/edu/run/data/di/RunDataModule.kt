package com.edu.run.data.di

import com.edu.run.data.sync.SyncRunScheduler
import com.edu.run.data.sync.SyncRunWorker
import com.edu.run.domain.SyncScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::SyncRunWorker)
    singleOf(::SyncRunScheduler).bind<SyncScheduler>()
}