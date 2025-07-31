package com.edu.auth.domain

import com.edu.core.domain.util.DataError
import com.edu.core.domain.util.EmptyResult

interface AuthRepository{
    suspend fun register(email: String, password: String): EmptyResult<DataError.Network>
}