package com.edu.core.data.di

import com.edu.core.data.auth.EncryptedSessionStorage
import com.edu.core.data.networking.HttpClientFactory
import com.edu.core.domain.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module{
    single {
        HttpClientFactory(get()).build()
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
}