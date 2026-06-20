package com.tertiaryinfotech.aiexams.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

// WhatsApp business line for feature requests and bug reports.
private const val WHATSAPP_NUMBER = "6588666375"
private const val WHATSAPP_DISPLAY = "+65 8866 6375"

/**
 * Feedback tab — opens WhatsApp (with a prefilled message) so users can send
 * new-feature ideas and bug reports directly to the team.
 */
@Composable
fun FeedbackScreen() {
    val context = LocalContext.current

    fun openWhatsApp() {
        val text = Uri.encode(
            "Hi AI Exams team, I'd like to share feedback:\n\n" +
                "• New feature I'd like:\n• Bug I found:\n"
        )
        val uri = "https://wa.me/$WHATSAPP_NUMBER?text=$text".toUri()
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open WhatsApp.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Filled.Forum,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(56.dp),
        )
        Text(
            "Send us feedback",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            "Tell us about new features you'd like to see or report a bug. " +
                "We read every message on WhatsApp.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        Button(
            onClick = ::openWhatsApp,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        ) {
            Icon(Icons.Filled.Forum, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("  Message us on WhatsApp")
        }
        Text(
            WHATSAPP_DISPLAY,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}
