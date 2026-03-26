package com.edu.core.domain.run

import com.edu.core.domain.util.DataError
import com.edu.core.domain.util.Result

interface RemoteImageStorage {
    suspend fun uploadImage(
        runId: String,
        imageBytes: ByteArray
    ): Result<String, DataError.Network>

    suspend fun deleteImage(runId: String): Result<Unit, DataError.Network>
}