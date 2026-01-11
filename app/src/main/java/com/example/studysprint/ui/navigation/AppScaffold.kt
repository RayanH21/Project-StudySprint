package com.example.studysprint.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studysprint.R
import com.example.studysprint.ui.screens.PlaceholderScreen

@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    val items = listOf(
        Triple(TopDest.Courses, R.string.nav_courses, Icons.Default.Book),
        Triple(TopDest.Exams, R.string.nav_exams, Icons.Default.Event),
        Triple(TopDest.Timer, R.string.nav_timer, Icons.Default.Timer),
        Triple(TopDest.Stats, R.string.nav_stats, Icons.Default.BarChart),
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { (dest, labelRes, icon) ->
                    NavigationBarItem(
                        selected = currentRoute == dest.route,
                        onClick = {
                            navController.navigate(dest.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(TopDest.Courses.route) { saveState = true }
                            }
                        },
                        icon = { Icon(icon, contentDescription = null) },
                        label = { Text(stringResource(labelRes)) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = TopDest.Courses.route
        ) {
            composable(TopDest.Courses.route) { PlaceholderScreen(titleRes = R.string.nav_courses, padding = padding) }
            composable(TopDest.Exams.route) { PlaceholderScreen(titleRes = R.string.nav_exams, padding = padding) }
            composable(TopDest.Timer.route) { PlaceholderScreen(titleRes = R.string.nav_timer, padding = padding) }
            composable(TopDest.Stats.route) { PlaceholderScreen(titleRes = R.string.nav_stats, padding = padding) }
        }
    }
}