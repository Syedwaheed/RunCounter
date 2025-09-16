package com.edu.auth.presentation.di

import com.edu.auth.presentation.login.LoginViewModel
import com.edu.auth.presentation.register.RegisterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authViewModelModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::LoginViewModel)
}