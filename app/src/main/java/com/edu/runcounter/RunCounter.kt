package com.edu.runcounter

import android.app.Application
import com.edu.auth.data.di.authDataModule
import com.edu.auth.presentation.di.authViewModelModule
import com.edu.core.data.di.coreDataModule
import com.edu.runcounter.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RunCounter: Application() {
    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RunCounter)
            modules(
                authDataModule,
                authViewModelModule,
                appModule,
                coreDataModule
            )
        }
    }
}