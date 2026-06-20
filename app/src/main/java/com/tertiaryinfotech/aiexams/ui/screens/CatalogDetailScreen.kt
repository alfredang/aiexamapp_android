package com.tertiaryinfotech.aiexams.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tertiaryinfotech.aiexams.data.CatalogBundle
import com.tertiaryinfotech.aiexams.data.CatalogExam

/** Bundle detail with free-teaser entries. Mirrors iOS CatalogDetailView. */
@Composable
fun CatalogDetailScreen(
    bundle: CatalogBundle,
    onStartTeaser: (CatalogExam) -> Unit,
) {
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Column(
                Modifier.padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(bundle.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    bundle.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "${bundle.totalQuestions} questions · ${bundle.practiceExamCount} practice exams",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            HorizontalDivider()
            Text(
                "Practice exams",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
            )
        }

        items(bundle.exams, key = { it.id }) { exam ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStartTeaser(exam) }
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text("Free teaser: ${exam.code}", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${exam.durationMinutes} min exam mode · pass ${exam.passingScore}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            HorizontalDivider()
        }

        item {
            Text(
                "Full practice access requires an existing web purchase. No checkout " +
                    "or payment flow is included in this app.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        }
    }
}
