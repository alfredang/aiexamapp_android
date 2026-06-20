package com.tertiaryinfotech.aiexams.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.tertiaryinfotech.aiexams.data.ApiException
import com.tertiaryinfotech.aiexams.ui.SessionViewModel
import kotlinx.coroutines.launch

private const val WEBSITE = "https://exams.tertiaryinfotech.com/"

/**
 * Account tab: shows the signed-in user, a website link, and destructive
 * actions (delete account, sign out). When signed out it shows the auth form.
 * Mirrors the iOS AccountView.
 */
@Composable
fun AccountScreen(session: SessionViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    if (session.token == null) {
        AuthScreen(session)
        return
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(session.user?.email ?: "Signed in", style = MaterialTheme.typography.titleMedium)
        }
        session.user?.name?.takeIf { it.isNotEmpty() }?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 36.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    context.startActivity(Intent(Intent.ACTION_VIEW, WEBSITE.toUri()))
                }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Filled.OpenInBrowser, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text("Open Tertiary Exams Website")
        }
        Text(
            "Purchases, payments, invoices, and vouchers stay on the website. This app is for mobile practice.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isDeleting) { showDeleteConfirm = true }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Text(
                if (isDeleting) "Deleting..." else "Delete Account",
                color = MaterialTheme.colorScheme.error,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { session.signOut() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Text("Sign Out", color = MaterialTheme.colorScheme.error)
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete your account?") },
            text = {
                Text(
                    "Your account will be anonymized and disabled. Purchase records may " +
                        "be retained for legal and accounting purposes."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    scope.launch {
                        isDeleting = true
                        try {
                            session.api.deleteAccount()
                            session.signOut()
                        } catch (e: ApiException) {
                            error = e.message
                        } finally {
                            isDeleting = false
                        }
                    }
                }) { Text("Delete Account", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
        )
    }
}
