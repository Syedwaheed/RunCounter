package com.edu.core.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object Timer {

    fun timeAndEmit(): Flow<Duration> {
        return flow {
            var lastEmitTime = System.currentTimeMillis()

            while(true){
                delay(200L)
                val currentTime = System.currentTimeMillis()
                val elapseTime = currentTime - lastEmitTime
                emit(elapseTime.milliseconds)
                lastEmitTime = currentTime
            }
        }
    }
}