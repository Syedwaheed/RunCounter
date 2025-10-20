package com.edu.run.network.di

import com.edu.core.domain.run.RemoteRunDataSource
import com.edu.run.network.KtorRemoteRunDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val network = module {
    singleOf(::KtorRemoteRunDataSource).bind<RemoteRunDataSource>()
}