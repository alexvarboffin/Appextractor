package com.walhalla.compose.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.ui.tv.detail.AppDetailScreen
import com.walhalla.compose.ui.tv.shell.MainShellScreen
import com.walhalla.compose.ui.tv.theme.AppextractorTvTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvNavHost() {
    val navController = rememberNavController()
    AppextractorTvTheme {
        CompositionLocalProvider(LocalTvNavController provides navController) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = TvRoutes.Shell
                ) {
                    composable(TvRoutes.Shell) {
                        MainShellScreen()
                    }
                    composable(
                        route = TvRoutes.AppDetail,
                        arguments = listOf(
                            navArgument("packageName") { type = NavType.StringType }
                        )
                    ) { entry ->
                        val packageName = entry.arguments?.getString("packageName").orEmpty()
                        val app = navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.get<PackageMeta>("detail_app")
                        AppDetailScreen(
                            packageName = packageName,
                            initialApp = app,
                            onBack = { navController.navigateUp() }
                        )
                    }
                }
            }
        }
    }
}
