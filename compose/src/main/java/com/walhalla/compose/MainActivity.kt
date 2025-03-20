package com.walhalla.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import androidx.navigation.navArgument
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.screens.LogScreen
import com.walhalla.compose.screens.ManifestScreen
import com.walhalla.compose.screens.SettingsScreen
import com.walhalla.compose.screens.AppDetailScreen
import com.walhalla.compose.screens.ExtractorScreen
import com.walhalla.compose.screens.SearchScreen
import com.walhalla.compose.ui.theme.AppextractorTheme
import com.walhalla.compose.viewmodel.ExtractorViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppextractorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val items = listOf(
                        Screen.Extractor,
                        Screen.Logs,
                        Screen.Settings
                    )

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val showBottomBar =
                        currentRoute != null && !currentRoute.startsWith("app_detail")

                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) {
                                NavigationBar {
                                    val currentDestination = navBackStackEntry?.destination

                                    items.forEach { screen ->
                                        NavigationBarItem(
                                            icon = { Icon(screen.icon, contentDescription = null) },
                                            label = { Text(screen.label) },
                                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                            onClick = {
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Extractor.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Extractor.route) {
                                ExtractorScreen(
                                    onNavigateToAppDetail = { packageName ->
                                        navController.navigate("app_detail/$packageName")
                                    },
                                    viewManifest = {
                                        println("{}{}{}" + it)
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            "app",
                                            it
                                        )
                                        navController.navigate("manifest")
                                    }
                                )


                            }
                            composable(Screen.Logs.route) { LogScreen() }
                            composable(Screen.Settings.route) { SettingsScreen() }
                            composable(Screen.Manifest.route) { backStackEntry ->
                                val app = navController.previousBackStackEntry?.savedStateHandle?.get<PackageMeta>("app")
                                println("DEBUG: Manifest screen app: $app")
                                if (app != null) {
                                    ManifestScreen(
                                        app = app,
                                        onBackClick = { navController.navigateUp() }
                                    )
                                } else {
                                    println("DEBUG: App is null in Manifest screen")
                                    navController.navigateUp()
                                }
                            }
                            composable(
                                route = Screen.AppDetail.route,
                                arguments = listOf(
                                    navArgument("packageName") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val packageName = backStackEntry.arguments?.getString("packageName")
                                val viewModel: ExtractorViewModel = viewModel()
                                val app = viewModel.getAppByPackageName(packageName)
                                if (app != null) {
                                    AppDetailScreen(
                                        app = app,
                                        onBackClick = { navController.navigateUp() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Extractor : Screen("extractor", "Extractor", Icons.Default.Storage)
    object Logs : Screen("logs", "Logs", Icons.AutoMirrored.Filled.List)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    //object Manifest : Screen("manifest/{packageName}", "Manifest", Icons.Default.Description)
    object Manifest : Screen("manifest", "Manifest", Icons.Default.Description)

    object AppDetail : Screen("app_detail/{packageName}", "App Detail", Icons.Default.Info)
}