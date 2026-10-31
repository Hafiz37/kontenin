package com.example.kontenin.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kontenin.R
import com.example.kontenin.ui.navigation.Routes
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Bottom navigation bar utama dengan tiga destinasi: Beranda, Rekap, Pengaturan.
 *
 * Spesifikasi DESIGN §5.2:
 * - Tinggi 80 dp
 * - Label selalu terlihat (alwaysShowLabel = true)
 * - Indikator warna: primaryContainer
 * - Ikon + label tidak terpilih: onSurfaceVariant
 * - Terpilih: ikon onPrimaryContainer (di dalam pil indikator), label primary SemiBold
 *
 * @param currentRoute Route yang sedang aktif, dari NavController.
 * @param onNavigate   Callback saat item navigasi dipilih.
 */
@Composable
fun MainBottomBar(
    currentRoute: String?,
    onNavigate: (route: String) -> Unit,
) {
    data class NavItem(
        val route: String,
        val labelRes: Int,
        val cdRes: Int,
        val icon: @Composable () -> Unit,
    )

    val items = listOf(
        NavItem(
            route    = Routes.Home.route,
            labelRes = R.string.nav_home,
            cdRes    = R.string.cd_nav_home,
            icon     = {
                Icon(
                    imageVector        = Icons.Filled.Home,
                    contentDescription = null,
                )
            },
        ),
        NavItem(
            route    = Routes.Recap.route,
            labelRes = R.string.nav_recap,
            cdRes    = R.string.cd_nav_recap,
            icon     = {
                Icon(
                    imageVector        = Icons.Filled.BarChart,
                    contentDescription = null,
                )
            },
        ),
        NavItem(
            route    = Routes.Settings.route,
            labelRes = R.string.nav_settings,
            cdRes    = R.string.cd_nav_settings,
            icon     = {
                Icon(
                    imageVector        = Icons.Filled.Settings,
                    contentDescription = null,
                )
            },
        ),
    )

    val colors = KonteninTheme.colorScheme

    NavigationBar(
        tonalElevation = 0.dp,
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected        = selected,
                onClick         = {
                    if (!selected) {
                        // Hindari penumpukan layar identik; hanya navigasi jika berbeda
                        onNavigate(item.route)
                    }
                },
                icon            = item.icon,
                label           = {
                    Text(
                        text       = stringResource(item.labelRes),
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color      = if (selected) colors.primary else colors.onSurfaceVariant,
                    )
                },
                alwaysShowLabel = true,
                colors          = NavigationBarItemDefaults.colors(
                    selectedIconColor          = colors.onPrimaryContainer,
                    unselectedIconColor        = colors.onSurfaceVariant,
                    selectedTextColor          = colors.primary,
                    unselectedTextColor        = colors.onSurfaceVariant,
                    indicatorColor             = colors.primaryContainer,
                ),
            )
        }
    }
}
