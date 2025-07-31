package com.edu.runcounter.utils

import android.content.Context

/*
object TinkUtils {
    private const val KEYSET_NAME = "tink_keyset.json"
    private const val PREF_FILE_NAME = "tink_keystore_prefs"
    private const val MASTER_KEY_URI = "android-keystore://tink_master_key"

    fun getAead(context: Context): Aead {
        TinkConfig.register() // Registers Tink configurations

        return AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)

    }
}
*/
