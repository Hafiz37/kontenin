package com.example.kontenin.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kontenin.AppContainer
import com.example.kontenin.MainActivity
import com.example.kontenin.ui.screens.home.HomeScreen
import com.example.kontenin.ui.screens.jobdetail.JobDetailScreen
import com.example.kontenin.ui.screens.jobform.JobFormScreen
import com.example.kontenin.ui.screens.recap.RecapScreen
import com.example.kontenin.ui.screens.settings.SettingsScreen

/**
 * NavHost Kontenin — mendaftarkan seluruh destinasi dan memetakan
 * argument navigasi ke masing-masing layar (FE-05).
 *
 * Alur navigasi:
 *   Home ──► JobDetail ──► JobForm (edit / duplicate)
 *   Home ──► JobForm   (add)
 *   Bottom bar: Home ↔ Recap ↔ Settings
 *   
 * Diupdate di FE-07 untuk menyediakan HomeViewModel dari AppContainer.
 */
@Composable
fun KonteninNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    // Ambil AppContainer dari MainActivity
    val context = LocalContext.current
    val appContainer = remember {
        (context as? MainActivity)?.appContainer 
            ?: throw IllegalStateException("AppContainer tidak ditemukan di MainActivity")
    }
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route,
        modifier = modifier,
    ) {

        // ── Destinasi utama ───────────────────────────────────────────────────

        composable(route = Routes.Home.route) {
            // FE-07: Create HomeViewModel per navigation entry
            val homeViewModel = remember { appContainer.createHomeViewModel() }
            
            HomeScreen(
                viewModel = homeViewModel,
                snackbarHostState = snackbarHostState,
                onNavigateToJobDetail = { jobId ->
                    navController.navigate(Routes.JobDetail.createRoute(jobId))
                },
                onNavigateToAddJob = {
                    navController.navigate(
                        Routes.JobForm.createRoute(Routes.MODE_ADD)
                    )
                },
            )
        }

        composable(route = Routes.Recap.route) {
            RecapScreen(snackbarHostState = snackbarHostState)
        }

        composable(route = Routes.Settings.route) {
            SettingsScreen(snackbarHostState = snackbarHostState)
        }

        // ── Destinasi turunan ─────────────────────────────────────────────────

        composable(
            route = Routes.JobDetail.route,
            arguments = listOf(
                navArgument(Routes.ARG_JOB_ID) { type = NavType.LongType },
            ),
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getLong(Routes.ARG_JOB_ID) ?: -1L
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.example.kontenin.presentation.jobdetail.JobDetailViewModel>(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return appContainer.createJobDetailViewModel(jobId = jobId) as T
                    }
                }
            )
            JobDetailScreen(
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Routes.JobForm.createRoute(Routes.MODE_EDIT, id))
                },
                onNavigateToDuplicate = { id ->
                    navController.navigate(Routes.JobForm.createRoute(Routes.MODE_DUPLICATE, id))
                },
            )
        }

        composable(
            route = Routes.JobForm.route,
            arguments = listOf(
                navArgument(Routes.ARG_MODE) {
                    type = NavType.StringType
                    defaultValue = Routes.MODE_ADD
                },
                navArgument(Routes.ARG_JOB_ID) {
                    type = NavType.LongType
                    defaultValue = Routes.DEFAULT_JOB_ID
                },
            ),
        ) { backStackEntry ->
            val mode  = backStackEntry.arguments?.getString(Routes.ARG_MODE)  ?: Routes.MODE_ADD
            val jobId = backStackEntry.arguments?.getLong(Routes.ARG_JOB_ID)  ?: Routes.DEFAULT_JOB_ID
            
            // FE-08: Create JobFormViewModel dengan SavedStateHandle
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.example.kontenin.presentation.jobform.JobFormViewModel>(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return appContainer.createJobFormViewModel(
                            savedStateHandle = backStackEntry.savedStateHandle,
                            jobId = jobId,
                            mode = mode
                        ) as T
                    }
                }
            )
            
            JobFormScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
