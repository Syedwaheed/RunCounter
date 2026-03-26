package com.edu.core.data.run

import com.edu.core.data.networking.firebaseSafeCall
import com.edu.core.domain.run.RemoteImageStorage
import com.edu.core.domain.util.DataError
import com.edu.core.domain.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseStorageImageUploader(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage
) : RemoteImageStorage {

    private suspend fun trySignInAnonymously() {
        if (firebaseAuth.currentUser != null) return
        try {
            firebaseAuth.signInAnonymously().await()
            Timber.d("Signed in to Firebase anonymously")
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Timber.w("Anonymous sign-in failed, proceeding without auth: ${e.message}")
        }
    }

    override suspend fun uploadImage(
        runId: String,
        imageBytes: ByteArray
    ): Result<String, DataError.Network> {
        trySignInAnonymously()
        return firebaseSafeCall {
            val imageRef = firebaseStorage.reference
                .child("run_images")
                .child("$runId.jpg")

            imageRef.putBytes(imageBytes).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()
            Timber.d("Image uploaded successfully: $downloadUrl")
            downloadUrl
        }
    }

    override suspend fun deleteImage(runId: String): Result<Unit, DataError.Network> {
        trySignInAnonymously()
        return firebaseSafeCall {
            val imageRef = firebaseStorage.reference
                .child("run_images")
                .child("$runId.jpg")

            imageRef.delete().await()
            Timber.d("Image deleted successfully for run: $runId")
        }
    }
}