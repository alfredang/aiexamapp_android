package com.tertiaryinfotech.aiexams.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tertiaryinfotech.aiexams.data.ApiException
import com.tertiaryinfotech.aiexams.data.ExamMode
import com.tertiaryinfotech.aiexams.ui.SessionViewModel
import kotlinx.coroutines.launch

/**
 * Pre-flight screen that starts an attempt and hands off the attempt id to the
 * runner. Mirrors the iOS StartExamView.
 */
@Composable
fun StartExamScreen(
    session: SessionViewModel,
    args: StartExamArgs,
    onAttemptStarted: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var isStarting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val subtitle = when {
        args.teaser -> "Free teaser"
        args.mode == ExamMode.PRACTICE -> "Practice mode reveals explanations after each answer."
        else -> "Exam mode is timed and reveals results after submission."
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        AppCard(Modifier.fillMaxWidth()) {
            Text(
                args.code,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                args.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 6.dp),
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = {
                scope.launch {
                    isStarting = true
                    try {
                        val response = session.api.startAttempt(args.examId, args.mode, args.teaser)
                        onAttemptStarted(response.attemptId)
                    } catch (e: ApiException) {
                        error = e.message
                    } finally {
                        isStarting = false
                    }
                }
            },
            enabled = !isStarting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isStarting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(
                    if (args.teaser) "Start Free Teaser"
                    else "Start ${args.mode.title} Mode"
                )
            }
        }
    }
}
