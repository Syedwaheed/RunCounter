package com.edu.auth.data

import com.edu.auth.domain.AuthRepository
import com.edu.core.data.networking.post
import com.edu.core.domain.util.DataError
import com.edu.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient
): AuthRepository {

    override suspend fun register(
        email: String,
        password: String
    ): EmptyResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(email,password)
        )
    }
}