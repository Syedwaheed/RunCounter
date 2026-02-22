package com.edu.runcounter

import android.app.Application
import androidx.work.Configuration
import com.edu.auth.data.di.authDataModule
import com.edu.auth.presentation.di.authViewModelModule
import com.edu.core.data.di.coreDataModule
import com.edu.core.database.di.databaseModule
import com.edu.goal.data.di.goalDataModule
import com.edu.goal.presentation.di.goalViewModelModule
import com.edu.run.data.di.runDataModule
import com.edu.run.location.di.locationModule
import com.edu.run.network.di.networkModule
import com.edu.run.presentation.di.runPresentationModule
import com.edu.runcounter.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class RunCounter: Application(), Configuration.Provider {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RunCounter)
            workManagerFactory()
            modules(
                authDataModule,
                authViewModelModule,
                appModule,
                coreDataModule,
                runPresentationModule,
                runDataModule,
                locationModule,
                databaseModule,
                networkModule,
                goalDataModule,
                goalViewModelModule
            )
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
}