package com.tertiaryinfotech.aiexams.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "ai_exams_session")

/**
 * Persists the auth token + user, exposes an [ApiClient] bound to the current
 * token, and holds the in-memory auth state. Mirrors the iOS SessionStore.
 */
class SessionStore(private val appContext: Context) {

    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }
    private val tokenKey = stringPreferencesKey("ai-exams-token")
    private val userKey = stringPreferencesKey("ai-exams-user")

    @Volatile var token: String? = null
        private set
    @Volatile var user: User? = null
        private set

    val api: ApiClient get() = ApiClient(tokenProvider = { token })

    /** Loads any persisted session into memory. Call once on app start. */
    suspend fun restore() {
        val prefs = appContext.dataStore.data.first()
        token = prefs[tokenKey]
        user = prefs[userKey]?.let { runCatching { json.decodeFromString<User>(it) }.getOrNull() }
    }

    suspend fun login(email: String, password: String) {
        persist(api.login(email, password))
    }

    suspend fun register(name: String, email: String, password: String) {
        persist(api.register(name, email, password))
    }

    private suspend fun persist(response: AuthResponse) {
        token = response.token
        user = response.user
        appContext.dataStore.edit { prefs ->
            prefs[tokenKey] = response.token
            prefs[userKey] = json.encodeToString(User.serializer(), response.user)
        }
    }

    suspend fun signOut() {
        token = null
        user = null
        appContext.dataStore.edit { it.clear() }
    }
}
