package com.edu.runcounter.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.edu.runcounter.MainViewModel
import com.edu.runcounter.RunCounter
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
val appModule = module {
    /*single<Aead> {
        TinkUtils.getAead(get())
    }
    // DataStore instance
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = {
                File(androidContext().filesDir, "secure_prefs.preferences_pb")
            }
        )
    }
    // Your encrypted token manager
    single {
        EncryptedPreferenceManager(get(), get())
    }
*/
    single<SharedPreferences>{

        EncryptedSharedPreferences(
            androidApplication(),
            "auth_pref",
            MasterKey(androidApplication()),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM

        )
    }
    single<CoroutineScope>{
        (androidApplication() as RunCounter).applicationScope
    }
    viewModelOf(::MainViewModel)
}