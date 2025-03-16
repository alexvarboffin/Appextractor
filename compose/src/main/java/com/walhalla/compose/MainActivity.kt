package com.walhalla.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.walhalla.compose.ui.navigation.Screen
import com.walhalla.compose.ui.screens.appdetail.AppDetailScreen
import com.walhalla.compose.ui.screens.main.MainScreen
import com.walhalla.compose.ui.screens.manifest.ManifestScreen
import com.walhalla.compose.ui.screens.resources.ResourcesScreen
import com.walhalla.compose.ui.theme.AppextractorTheme

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

                    NavHost(navController = navController, startDestination = Screen.Home.route) {
                        composable(Screen.Home.route) {
                            MainScreen(
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
            }
        }
    }
}