package com.edu.run.domain

import kotlin.time.Duration

interface SyncScheduler {
    fun scheduleSync(interval: Duration)
    fun cancelSync()
}