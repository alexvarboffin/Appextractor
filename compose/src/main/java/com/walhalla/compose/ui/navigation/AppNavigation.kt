package com.walhalla.compose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.walhalla.compose.ui.screens.home.HomeScreen
import com.walhalla.compose.ui.screens.appdetail.AppDetailScreen
import com.walhalla.compose.ui.screens.manifest.ManifestScreen
import com.walhalla.compose.ui.screens.resources.ResourcesScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AppDetail : Screen("app_detail/{packageName}") {
        fun createRoute(packageName: String) = "app_detail/$packageName"
    }
    object Manifest : Screen("manifest/{packageName}") {
        fun createRoute(packageName: String) = "manifest/$packageName"
    }
    object Resources : Screen("resources/{packageName}") {
        fun createRoute(packageName: String) = "resources/$packageName"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAppClick = { packageName ->
                    navController.navigate(Screen.AppDetail.createRoute(packageName))
                }
            )
        }

        composable(Screen.AppDetail.route) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: return@composable
            AppDetailScreen(
                packageName = packageName,
                onManifestClick = {
                    navController.navigate(Screen.Manifest.createRoute(packageName))
                },
                onResourcesClick = {
                    navController.navigate(Screen.Resources.createRoute(packageName))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Manifest.route) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: return@composable
            ManifestScreen(
                packageName = packageName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Resources.route) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: return@composable
            ResourcesScreen(
                packageName = packageName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
} 