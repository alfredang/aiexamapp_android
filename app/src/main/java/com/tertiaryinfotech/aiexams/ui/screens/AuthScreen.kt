package com.tertiaryinfotech.aiexams.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.tertiaryinfotech.aiexams.ui.SessionViewModel
import kotlinx.coroutines.launch

/**
 * Email/password sign-in & registration. Mirrors the iOS AuthView: the same
 * copy, an 8-character minimum password, and a toggle between modes.
 */
@Composable
fun AuthScreen(session: SessionViewModel) {
    val scope = rememberCoroutineScope()
    var isRegistering by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isBusy by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (isRegistering) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )

        session.errorMessage?.let {
            Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                scope.launch {
                    isBusy = true
                    if (isRegistering) session.register(name, email, password)
                    else session.login(email, password)
                    isBusy = false
                }
            },
            enabled = !isBusy && email.isNotEmpty() && password.length >= 8,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isBusy) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(if (isRegistering) "Create Account" else "Sign In")
            }
        }

        TextButton(
            onClick = {
                isRegistering = !isRegistering
                session.errorMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (isRegistering) "I already have an account" else "Create a free account")
        }

        Text(
            "Use the app to practice exams you already own and try available free " +
                "teasers. Purchases and payments are handled on the website.",
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start),
        )
    }
}
