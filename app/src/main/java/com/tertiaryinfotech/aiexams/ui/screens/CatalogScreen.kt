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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.tertiaryinfotech.aiexams.data.ApiException
import com.tertiaryinfotech.aiexams.data.CatalogBundle
import com.tertiaryinfotech.aiexams.data.CatalogResponse
import com.tertiaryinfotech.aiexams.ui.SessionViewModel
import kotlinx.coroutines.launch

/**
 * Browse the public exam catalog with search. No checkout/payment, mirroring
 * the iOS CatalogView.
 */
@Composable
fun CatalogScreen(
    session: SessionViewModel,
    onOpenBundle: (CatalogBundle) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var catalog by remember { mutableStateOf<CatalogResponse?>(null) }
    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun load() {
        isLoading = true
        try {
            catalog = session.api.catalog(searchText)
            error = null
        } catch (e: ApiException) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { load() }

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search certifications") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = { scope.launch { load() } }),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        )

        val current = catalog
        when {
            current != null -> LazyColumn(Modifier.fillMaxSize()) {
                item {
                    Text(
                        "Browse all practice exam bundles. Buying and payment are not available in this app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                    )
                }
                items(current.bundles, key = { it.id }) { bundle ->
                    BundleRow(bundle) { onOpenBundle(bundle) }
                    HorizontalDivider()
                }
            }

            isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }

            else -> MessageState("Catalog unavailable", error ?: "Try again.")
        }
    }
}

@Composable
private fun BundleRow(bundle: CatalogBundle, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            bundle.vendor?.let {
                Text(
                    it.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                bundle.code,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(bundle.title, style = MaterialTheme.typography.titleMedium)
        Text(
            "${bundle.totalQuestions} questions across ${bundle.practiceExamCount} practice exams",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
