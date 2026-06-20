package com.tertiaryinfotech.aiexams.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tertiaryinfotech.aiexams.data.ApiClient
import com.tertiaryinfotech.aiexams.data.ApiException
import com.tertiaryinfotech.aiexams.data.SessionStore
import com.tertiaryinfotech.aiexams.data.User
import kotlinx.coroutines.launch

/**
 * Holds auth state as Compose state and exposes the token-bound [ApiClient] to
 * all screens. Mirrors the iOS SessionStore's observable behaviour.
 */
class SessionViewModel(app: Application) : AndroidViewModel(app) {

    private val store = SessionStore(app)

    var token by mutableStateOf<String?>(null)
        private set
    var user by mutableStateOf<User?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
    var isReady by mutableStateOf(false)
        private set

    val api: ApiClient get() = store.api

    init {
        viewModelScope.launch {
            store.restore()
            token = store.token
            user = store.user
            isReady = true
        }
    }

    /** Returns true on success. On failure sets [errorMessage]. */
    suspend fun login(email: String, password: String): Boolean =
        authenticate { store.login(email, password) }

    suspend fun register(name: String, email: String, password: String): Boolean =
        authenticate { store.register(name, email, password) }

    private suspend fun authenticate(action: suspend () -> Unit): Boolean = try {
        action()
        token = store.token
        user = store.user
        errorMessage = null
        true
    } catch (e: ApiException) {
        errorMessage = e.message
        false
    }

    fun signOut() {
        viewModelScope.launch {
            store.signOut()
            token = null
            user = null
        }
    }
}
