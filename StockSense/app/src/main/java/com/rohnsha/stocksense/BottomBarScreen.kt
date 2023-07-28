package com.rohnsha.stocksense

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Home: BottomBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home,
    )

    object Search: BottomBarScreen(
        route = "search",
        title = "Search",
        icon = Icons.Default.Search
    )

    object Watchlist: BottomBarScreen(
        route = "watchlist",
        title = "Saved",
        icon = Icons.Default.List
    )

    object More: BottomBarScreen(
        route = "more",
        title = "More",
        icon = Icons.Default.Settings
    )

    object StockDetails: BottomBarScreen(
        route = "details/{symbol}",
        title = "Details",
        icon = Icons.Default.AccountBox
    )
}
