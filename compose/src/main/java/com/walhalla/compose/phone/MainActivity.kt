package com.walhalla.compose.phone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.screens.AppDetailScreen
import com.walhalla.compose.screens.ExtractorScreen
import com.walhalla.compose.screens.LogScreen
import com.walhalla.compose.screens.ManifestScreen
import com.walhalla.compose.screens.SettingsScreen
import com.walhalla.compose.ui.theme.AppextractorTheme

/**
 * Phone / tablet entry. Screens still live under [com.walhalla.compose.screens]
 * until moved to [com.walhalla.compose.ui.mobile].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppextractorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhoneRoot()
                }
            }
        }
    }
}

@Composable
private fun PhoneRoot() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Extractor,
        Screen.Logs,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isNavBarVisible =
        currentRoute != null && !currentRoute.startsWith("app_detail")

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val layoutType = if (isNavBarVisible) {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
    } else {
        NavigationSuiteType.None
    }

    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteItems = {
            val currentDestination = navBackStackEntry?.destination
            items.forEach { screen ->
                item(
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(screen.icon, contentDescription = null) },
                    label = { Text(screen.label) }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Extractor.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Extractor.route) {
                ExtractorScreen(
                    onNavigateToAppDetail = { app ->
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "detail_app",
                            app
                        )
                        navController.navigate("app_detail/${app.packageName}")
                    },
                    viewManifest = {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "app",
                            it
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "xml_file",
                            null as String?
                        )
                        navController.navigate("manifest")
                    }
                )
            }
            composable(Screen.Logs.route) { LogScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.Manifest.route) {
                val app = navController.previousBackStackEntry?.savedStateHandle?.get<PackageMeta>("app")
                val xmlFileName = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<String>("xml_file")
                println("DEBUG: Manifest screen app: $app xml=$xmlFileName")
                if (app != null) {
                    ManifestScreen(
                        app = app,
                        xmlFileName = xmlFileName,
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
            ) {
                val app = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<PackageMeta>("detail_app")
                if (app != null) {
                    AppDetailScreen(
                        app = app,
                        onBackClick = { navController.navigateUp() },
                        onViewManifest = { meta, xml ->
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "app",
                                meta
                            )
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "xml_file",
                                xml
                            )
                            navController.navigate("manifest")
                        }
                    )
                } else {
                    LaunchedEffect(Unit) { navController.navigateUp() }
                }
            }
        }
    }
}

private sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Extractor : Screen("extractor", "Extractor", Icons.Default.Storage)
    object Logs : Screen("logs", "Logs", Icons.AutoMirrored.Filled.List)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Manifest : Screen("manifest", "Manifest", Icons.Default.Description)
    object AppDetail : Screen("app_detail/{packageName}", "App Detail", Icons.Default.Info)
}
