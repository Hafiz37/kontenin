package com.example.kontenin.ui

import com.example.kontenin.AppContainer
import com.example.kontenin.R
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kontenin.ui.components.MainBottomBar
import com.example.kontenin.ui.navigation.KonteninNavHost
import com.example.kontenin.ui.navigation.Routes

/**
 * Composable host utama aplikasi Kontenin (FE-05).
 *
 * Tanggung jawab:
 * - Memegang [NavController] dan [SnackbarHostState] di level teratas.
 * - Menampilkan [MainBottomBar] hanya saat di destinasi utama
 *   (disembunyikan di Form dan Detail sesuai DESIGN §5.2).
 * - Menempatkan [SnackbarHost] di atas bottom navigation agar tidak tertutup.
 * - Menangani system insets melalui [Scaffold] (edge-to-edge sudah aktif
 *   di MainActivity via enableEdgeToEdge()).
 */
@Composable
fun KonteninApp(appContainer: AppContainer) {
    val navController       = rememberNavController()
    val snackbarHostState   = remember { SnackbarHostState() }

    // Amati back-stack untuk menentukan route aktif
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = !Routes.shouldHideBottomBar(currentRoute)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Text(
                text = stringResource(R.string.demo_data_notice),
                modifier = Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
            )
        },
        bottomBar = {
            if (showBottomBar) {
                MainBottomBar(
                    currentRoute = currentRoute,
                    onNavigate   = { route ->
                        navController.navigate(route) {
                            // Hindari back stack yang membesar saat berpindah tab
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Hindari salinan identik di atas back stack
                            launchSingleTop = true
                            // Pulihkan state tab yang sebelumnya dipilih
                            restoreState = true
                        }
                    },
                )
            }
        },
        snackbarHost = {
            // Snackbar muncul di atas bottom navigation (Scaffold mengelola posisi)
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        KonteninNavHost(
            appContainer      = appContainer,
            navController     = navController,
            snackbarHostState = snackbarHostState,
            modifier          = Modifier.padding(innerPadding),
        )
    }
}
