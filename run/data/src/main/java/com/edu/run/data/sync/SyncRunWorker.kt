package com.edu.run.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edu.core.domain.run.RunRepository
import com.edu.core.domain.util.DataError

class SyncRunWorker(
    context: Context,
    params: WorkerParameters,
    private val runRepository: RunRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= 5) {
            return Result.failure()
        }

        runRepository.syncPendingRuns()

        return when (val result = runRepository.fetchRuns()) {
            is com.edu.core.domain.util.Result.Success -> Result.success()
            is com.edu.core.domain.util.Result.Error -> handleError(result.error)
        }
    }

    private fun handleError(error: DataError): Result {
        return when (error) {
            DataError.Network.UNAUTHORIZED,
            DataError.Network.CONFLICT,
            DataError.Network.PAYLOAD_TOO_LARGE,
            DataError.Network.SERIALIZATION,
            DataError.Local.DISKFULL -> Result.failure()

            DataError.Network.REQUEST_TIMEOUT,
            DataError.Network.TOO_MANY_REQUESTS,
            DataError.Network.NO_INTERNET,
            DataError.Network.SERVER_ERROR,
            DataError.Network.UNKNOWN -> Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "sync_run_worker"
    }
}