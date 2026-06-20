package com.tertiaryinfotech.aiexams.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.tertiaryinfotech.aiexams.BuildConfig

private const val DEVELOPER = "Tertiary Infotech Academy Pte Ltd"
private const val WEBSITE_LABEL = "tertiaryinfotech.com"
private const val WEBSITE_URL = "https://www.tertiaryinfotech.com"

/**
 * About tab — app description, developer, data source, and version.
 * Styled with the same grouped-card layout as the rest of the app.
 */
@Composable
fun AboutScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        AppCard(Modifier.fillMaxWidth()) {
            Text("AI Exams", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Practice your purchased certification exams on the go. Browse the " +
                    "catalog, try free teasers, and take exams in Practice or Exam mode " +
                    "with explanations and instant scoring.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        SectionLabel("Developer")
        AppCard(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Filled.Apartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(DEVELOPER, style = MaterialTheme.typography.bodyLarge)
            }
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.startActivity(Intent(Intent.ACTION_VIEW, WEBSITE_URL.toUri()))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(Icons.Filled.Public, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(WEBSITE_LABEL, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            }
        }

        SectionLabel("Data")
        AppCard(Modifier.fillMaxWidth()) {
            Text(
                "Exam content and your purchases are managed on the Tertiary Exams " +
                    "website (exams.tertiaryinfotech.com). This app is for mobile practice; " +
                    "payments and vouchers stay on the website.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        AppCard(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Version", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
