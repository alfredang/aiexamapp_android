package com.tertiaryinfotech.aiexams.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tertiaryinfotech.aiexams.data.CatalogBundle
import com.tertiaryinfotech.aiexams.ui.screens.AboutScreen
import com.tertiaryinfotech.aiexams.ui.screens.AccountScreen
import com.tertiaryinfotech.aiexams.ui.screens.CatalogDetailScreen
import com.tertiaryinfotech.aiexams.ui.screens.CatalogScreen
import com.tertiaryinfotech.aiexams.ui.screens.ExamRunnerScreen
import com.tertiaryinfotech.aiexams.ui.screens.FeedbackScreen
import com.tertiaryinfotech.aiexams.ui.screens.LibraryScreen
import com.tertiaryinfotech.aiexams.ui.screens.StartExamArgs
import com.tertiaryinfotech.aiexams.ui.screens.StartExamScreen

private sealed class Tab(val route: String, val label: String, val icon: ImageVector) {
    data object Library : Tab("library", "My Exams", Icons.AutoMirrored.Filled.ListAlt)
    data object Catalog : Tab("catalog", "Catalog", Icons.Filled.Search)
    data object Account : Tab("account", "Account", Icons.Filled.Person)
    data object Feedback : Tab("feedback", "Feedback", Icons.Filled.Forum)
    data object About : Tab("about", "About", Icons.Filled.Info)
}

private val tabs = listOf(Tab.Library, Tab.Catalog, Tab.Account, Tab.Feedback, Tab.About)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(session: SessionViewModel) {
    val navController = rememberNavController()

    // Transient holders for navigation arguments that aren't plain strings.
    var selectedBundle by remember { mutableStateOf<CatalogBundle?>(null) }
    var startArgs by remember { mutableStateOf<StartExamArgs?>(null) }

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = currentRoute in tabs.map { it.route }
    val title = when (currentRoute) {
        Tab.Library.route -> "My Exams"
        Tab.Catalog.route -> "Catalog"
        Tab.Account.route -> "Account"
        Tab.Feedback.route -> "Feedback"
        Tab.About.route -> "About"
        "catalog_detail" -> selectedBundle?.code ?: "Bundle"
        "start_exam" -> startArgs?.code ?: "Exam"
        else -> "AI Exams"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(title) })
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Tab.Library.route,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            composable(Tab.Library.route) {
                LibraryScreen(session) { args ->
                    startArgs = args
                    navController.navigate("start_exam")
                }
            }
            composable(Tab.Catalog.route) {
                CatalogScreen(session) { bundle ->
                    selectedBundle = bundle
                    navController.navigate("catalog_detail")
                }
            }
            composable(Tab.Account.route) { AccountScreen(session) }
            composable(Tab.Feedback.route) { FeedbackScreen() }
            composable(Tab.About.route) { AboutScreen() }

            composable("catalog_detail") {
                selectedBundle?.let { bundle ->
                    CatalogDetailScreen(bundle) { exam ->
                        startArgs = StartExamArgs(
                            examId = exam.id,
                            title = exam.title,
                            code = exam.code,
                            mode = com.tertiaryinfotech.aiexams.data.ExamMode.PRACTICE,
                            teaser = true,
                        )
                        navController.navigate("start_exam")
                    }
                }
            }

            composable("start_exam") {
                startArgs?.let { args ->
                    StartExamScreen(session, args) { attemptId ->
                        navController.navigate("exam_runner/$attemptId")
                    }
                }
            }

            composable("exam_runner/{attemptId}") { entry ->
                val attemptId = entry.arguments?.getString("attemptId").orEmpty()
                ExamRunnerScreen(session, attemptId)
            }
        }
    }
}
