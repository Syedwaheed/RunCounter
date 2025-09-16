package com.edu.auth.data

import com.edu.auth.domain.AuthRepository
import com.edu.core.data.networking.post
import com.edu.core.domain.AuthInfo
import com.edu.core.domain.SessionStorage
import com.edu.core.domain.util.DataError
import com.edu.core.domain.util.EmptyResult
import com.edu.core.domain.util.Result
import com.edu.core.domain.util.asEmptyDataResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage
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

    override suspend fun login(
        email: String,
        password: String
    ): EmptyResult<DataError.Network> {
        val result = httpClient.post<LoginRequest,LoginResponse>(
            route = "/login",
            body = LoginRequest(email,password)
        )
        if(result is Result.Success){
            sessionStorage.set(
                AuthInfo(
                    refreshToken = result.data.refreshToken,
                    accessToken = result.data.accessToken,
                    userId = result.data.userId,
                )
            )
        }
       return result.asEmptyDataResult()
    }
}