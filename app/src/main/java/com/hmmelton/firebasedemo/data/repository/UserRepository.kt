package com.hmmelton.firebasedemo.data.repository

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.hmmelton.firebasedemo.analytics.AnalyticsClient
import com.hmmelton.firebasedemo.analytics.events.UserDatabaseQueryFailureEvent
import com.hmmelton.firebasedemo.binding.Users
import com.hmmelton.firebasedemo.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "UserRepository"

/**
 * Repository for [User] objects. This is used to interact with the current user both locally and
 * remotely.
 */
class UserRepository @Inject constructor(
    @Users private val database: DatabaseReference,
    private val analytics: AnalyticsClient
) {

    suspend fun get(id: String): User? {
        val snapshot = try {
            database.child(id).get().await()
        } catch (e: Exception) {
            Log.e(TAG, "error fetching User", e)
            analytics.logEvent(UserDatabaseQueryFailureEvent(id, e))
            return null
        }

        // Return null if user could not be found
        if (!snapshot.exists()) {
            Log.e(TAG, "could not find User with ID: $id")
            return null
        }

        // Convert Snapshot value to User object
        val user = try {
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "error converting fetched value to User object", e)
            null
        }

        return user
    }
}