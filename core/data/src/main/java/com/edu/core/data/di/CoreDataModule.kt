package com.edu.core.data.di

import com.edu.core.data.auth.EncryptedSessionStorage
import com.edu.core.data.networking.HttpClientFactory
import com.edu.core.data.run.FirebaseStorageImageUploader
import com.edu.core.data.run.OfflineFirstRunRepository
import com.edu.core.domain.SessionStorage
import com.edu.core.domain.run.RemoteImageStorage
import com.edu.core.domain.run.RunRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(get()).build()
    }
    single { FirebaseAuth.getInstance() }
    single { FirebaseStorage.getInstance() }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
    singleOf(::FirebaseStorageImageUploader).bind<RemoteImageStorage>()
    singleOf(::OfflineFirstRunRepository).bind<RunRepository>()
}