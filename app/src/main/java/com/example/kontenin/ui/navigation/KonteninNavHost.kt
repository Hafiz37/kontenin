package com.example.kontenin.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
 */
@Composable
fun KonteninNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route,
        modifier = modifier,
    ) {

        // ── Destinasi utama ───────────────────────────────────────────────────

        composable(route = Routes.Home.route) {
            HomeScreen(
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
            JobDetailScreen(
                jobId = jobId,
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
            JobFormScreen(
                mode  = mode,
                jobId = jobId,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
