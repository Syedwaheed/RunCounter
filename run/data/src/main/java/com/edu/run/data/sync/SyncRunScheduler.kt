package com.edu.run.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.edu.run.domain.SyncScheduler
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncRunScheduler(
    private val context: Context
): SyncScheduler {
    private val workManager = WorkManager.getInstance(context)

    override fun scheduleSync(interval: Duration) {
        val workRequest = PeriodicWorkRequestBuilder<SyncRunWorker>(
            repeatInterval = interval.toJavaDuration()
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            SyncRunWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun cancelSync() {
        workManager.cancelUniqueWork(SyncRunWorker.WORK_NAME)
    }
}