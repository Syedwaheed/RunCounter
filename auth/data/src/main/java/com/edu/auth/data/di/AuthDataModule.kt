package com.edu.auth.data.di

import com.edu.auth.data.AuthRepositoryImpl
import com.edu.auth.data.EmailPatternValidator
import com.edu.auth.domain.AuthRepository
import com.edu.auth.domain.PatternValidator
import com.edu.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator>{
        EmailPatternValidator
    }
    singleOf(::UserDataValidator)
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}