package com.tertiaryinfotech.aiexams.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tertiaryinfotech.aiexams.data.ApiException
import com.tertiaryinfotech.aiexams.data.ExamMode
import com.tertiaryinfotech.aiexams.data.LibraryExam
import com.tertiaryinfotech.aiexams.data.LibraryResponse
import com.tertiaryinfotech.aiexams.ui.SessionViewModel

/** Params passed when launching an exam from the library or a teaser. */
data class StartExamArgs(
    val examId: String,
    val title: String,
    val code: String,
    val mode: ExamMode,
    val teaser: Boolean,
)

/**
 * The "My Exams" tab: the user's purchased entitlements grouped by bundle, each
 * with a Practice/Exam mode toggle. Mirrors the iOS LibraryView.
 */
@Composable
fun LibraryScreen(
    session: SessionViewModel,
    onStart: (StartExamArgs) -> Unit,
) {
    var library by remember { mutableStateOf<LibraryResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(session.token) {
        if (session.token == null) return@LaunchedEffect
        isLoading = true
        try {
            library = session.api.library()
            error = null
        } catch (e: ApiException) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    val lib = library
    when {
        session.token == null ->
            AuthRequiredView("Sign in to see purchased practice exams and continue your progress.")

        isLoading && lib == null ->
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }

        lib != null -> {
            if (lib.bundles.isEmpty() && lib.standalone.isEmpty()) {
                MessageState(
                    "No purchased exams",
                    "Your purchased practice exams will appear here after web checkout.",
                )
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    lib.bundles.forEach { bundle ->
                        item(key = bundle.bundleId) {
                            SectionHeader(bundle.bundleTitle)
                        }
                        items(bundle.items, key = { it.entitlementId }) { item ->
                            LibraryExamRow(item, onStart)
                            HorizontalDivider()
                        }
                    }
                    if (lib.standalone.isNotEmpty()) {
                        item { SectionHeader("Standalone") }
                        items(lib.standalone, key = { it.entitlementId }) { item ->
                            LibraryExamRow(item, onStart)
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        else -> MessageState("Could not load exams", error ?: "Try again.")
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun LibraryExamRow(item: LibraryExam, onStart: (StartExamArgs) -> Unit) {
    var selectedMode by remember { mutableStateOf(ExamMode.PRACTICE) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onStart(StartExamArgs(item.examId, item.examTitle, item.examCode, selectedMode, false))
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                item.examCode,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                item.vendorName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(item.examTitle, style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SingleChoiceSegmentedButtonRow {
                ExamMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = selectedMode == mode,
                        onClick = { selectedMode = mode },
                        shape = SegmentedButtonDefaults.itemShape(index, ExamMode.entries.size),
                    ) { Text(mode.title) }
                }
            }
            Text(
                "${item.questionCount} questions",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
