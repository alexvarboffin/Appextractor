package com.walhalla.compose.phone

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.ui.mobile.AppDetailScreen
import com.walhalla.compose.ui.mobile.ExtractorScreen
import com.walhalla.compose.ui.mobile.LogScreen
import com.walhalla.compose.ui.mobile.ManifestScreen
import com.walhalla.compose.ui.mobile.SettingsScreen
import com.walhalla.compose.ui.theme.AppextractorTheme
import com.walhalla.ui.observer.RateAppModule
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

/** Phone / tablet entry. Screens live in [com.walhalla.compose.ui.mobile]. */
class MainActivity : ComponentActivity() {

    private var rateAppModule: RateAppModule? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rateAppModule = RateAppModule(context)
        lifecycle.addObserver(rateAppModule!!)
        val testDevices = listOf(
            AdRequest.DEVICE_ID_EMULATOR,
            "B9F431FA79D446517D20E015CC3D013E"
        )
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(testDevices).build()
        )
        MobileAds.initialize(this)
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

    override fun onSaveInstanceState(outState: Bundle) {
        rateAppModule?.appReloadedHandler()
        super.onSaveInstanceState(outState)
    }
}

@Composable
private fun PhoneRoot() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var exitArmed by remember { mutableStateOf(false) }
    val items = listOf(
        Screen.Extractor,
        Screen.Logs,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val atRoot = currentRoute == Screen.Extractor.route ||
        currentRoute == Screen.Logs.route ||
        currentRoute == Screen.Settings.route
    BackHandler(enabled = atRoot) {
        if (exitArmed) {
            (context as? AppCompatActivity)?.finish()
        } else {
            exitArmed = true
            Toast.makeText(
                context,
                context.getString(com.walhalla.shared.R.string.press_again_to_exit)
                    .uppercase(Locale.getDefault()),
                Toast.LENGTH_LONG
            ).show()
            scope.launch {
                delay(1100)
                exitArmed = false
            }
        }
    }
    val isNavBarVisible =
        currentRoute != null && !currentRoute.startsWith("app_detail")

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val layoutType = if (isNavBarVisible) {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
    } else {
        NavigationSuiteType.None
    }

    Column(Modifier.fillMaxSize()) {
    NavigationSuiteScaffold(
        modifier = Modifier.weight(1f),
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
        PhoneBannerAd(Modifier.fillMaxWidth())
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
