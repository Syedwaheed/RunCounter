package com.edu.core.database.di

import androidx.room.Room
import com.edu.core.database.RoomLocalRunDataSource
import com.edu.core.database.RunDatabase
import com.edu.core.database.migration.Migration3To4
import com.edu.core.database.migration.Migration4To5
import com.edu.core.domain.run.LocalRunDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            RunDatabase::class.java,
            "run.db"
        )
            .addMigrations(Migration3To4, Migration4To5)
            .build()
    }
    single { get<RunDatabase>().runDao }
    single { get<RunDatabase>().runPendingSyncDao }
    single { get<RunDatabase>().deletedRunSyncDao }
    single {get<RunDatabase>().goalDao}
    singleOf(::RoomLocalRunDataSource).bind<LocalRunDataSource>()
}