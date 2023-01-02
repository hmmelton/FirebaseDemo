package com.hmmelton.firebasedemo

import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FirebaseDemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val database = Firebase.database
        // Persist data locally
        database.setPersistenceEnabled(true)
        // According to documentation, 1MB is the min cache size
        // https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase#setPersistenceCacheSizeBytes(long)
        database.setPersistenceCacheSizeBytes(1024 * 1024)

        if (BuildConfig.DEBUG) {
            database.useEmulator("10.0.2.2", 9000)
        }
    }
}