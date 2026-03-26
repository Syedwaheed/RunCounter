package com.edu.core.data.networking

import android.util.Log
import com.edu.core.data.BuildConfig
import com.edu.core.domain.AuthInfo
import com.edu.core.domain.SessionStorage
import com.edu.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber

class HttpClientFactory(
    private val sessionStorage: SessionStorage
) {

    fun build(): HttpClient {
        return HttpClient(CIO) {
            install(HttpTimeout){
                requestTimeoutMillis = 60_000   // total time for the whole request
                connectTimeoutMillis = 30_000   // time to establish TCP/TLS
                socketTimeoutMillis = 60_000
            }
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
                level = LogLevel.ALL
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                header("x-api-key", BuildConfig.API_KEY)
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val info = sessionStorage.get()
                        Timber.d("loadTokens: userId=${info?.userId}, hasToken=${info?.accessToken?.isNotEmpty()}")
                        BearerTokens(
                            accessToken = info?.accessToken ?: "",
                            refreshToken = info?.refreshToken ?: "",
                        )
                    }
                    refreshTokens {
                        val info = sessionStorage.get()
                        Log.d("info", "$info")
                        val response = client.post<AccessTokenRequest, AccessTokenResponse>(
                            route = "/accessToken",
                            body = AccessTokenRequest(
                                refreshToken = info?.refreshToken ?: "",
                                userId = info?.userId ?: ""
                            )
                        )
                        if (response is Result.Success) {
                            val newAuthInfo = AuthInfo(
                                accessToken = response.data.accessToken,
                                refreshToken = info?.refreshToken ?: "",
                                userId = info?.userId ?: ""
                            )
                            sessionStorage.set(newAuthInfo)
                            BearerTokens(
                                accessToken = newAuthInfo.accessToken,
                                refreshToken = newAuthInfo.refreshToken
                            )
                        }
                        else{
                            BearerTokens(
                                accessToken = "",
                                refreshToken = ""
                            )
                        }
                    }

                }
            }
        }.also { client ->
            // Intercept every request to add fresh token from SessionStorage
            // Use Transform phase which runs AFTER Auth plugin's State phase
            // This ensures we override the cached token with the current user's token
            client.requestPipeline.intercept(HttpRequestPipeline.Transform) {
                val info = sessionStorage.get()
                Timber.d("RequestPipeline: userId=${info?.userId}, hasToken=${info?.accessToken?.isNotEmpty()}")
                if (!info?.accessToken.isNullOrEmpty()) {
                    context.headers.remove(HttpHeaders.Authorization)
                    context.headers.append(HttpHeaders.Authorization, "Bearer ${info.accessToken}")
                }
            }
        }
    }
}