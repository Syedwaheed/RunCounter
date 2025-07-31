package com.edu.runcounter.utils

import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map


/*class EncryptedPreferenceManager(
    private val dataStore: DataStore<Preferences>,
    private val aead: Aead
) {
    private val tokenKey = stringPreferencesKey("secure_token")

    suspend fun saveToken(token: String) {
        val encryptedBytes = aead.encrypt(token.toByteArray(), null)
        val encoded = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        dataStore.edit { it[tokenKey] = encoded }
    }

    suspend fun getToken(): String? {
        val encoded = dataStore.data
            .map { it[tokenKey] }
            .firstOrNull()
        return encoded?.let {
            val decryptedBytes = aead.decrypt(Base64.decode(it, Base64.NO_WRAP), null)
            String(decryptedBytes)
        }
    }

    suspend fun clearToken() {
        dataStore.edit { it.remove(tokenKey) }
    }
}*/
