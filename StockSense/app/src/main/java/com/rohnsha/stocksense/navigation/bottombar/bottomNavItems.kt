package com.rohnsha.stocksense.navigation.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector

sealed class bottomNavItems(
    val title: String,
    val route: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
){
    object HomePage: bottomNavItems(
        title = "Home",
        route = "home",
        unselectedIcon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )

    object Chatbot: bottomNavItems(
        title = "Chatbot",
        route = "chatbot",
        unselectedIcon = Icons.Outlined.Chat,
        selectedIcon = Icons.Filled.Chat
    )

    object AnalysisInsigth: bottomNavItems(
        title = "Analysis",
        route = "analysis",
        unselectedIcon = Icons.Outlined.Analytics,
        selectedIcon = Icons.Filled.Analytics
    )

    object Profile: bottomNavItems(
        title = "Profile",
        route = "profile",
        unselectedIcon = Icons.Outlined.Chat,
        selectedIcon = Icons.Filled.Chat
    )

    object Details: bottomNavItems(
        title = "Details",
        route = "details",
        unselectedIcon = Icons.Outlined.Chat,
        selectedIcon = Icons.Filled.Chat
    )

    object StockList: bottomNavItems(
        title = "Search",
        route = "search",
        unselectedIcon = Icons.Outlined.Search,
        selectedIcon = Icons.Filled.Search
    )

    object Watchlist: bottomNavItems(
        title = "Watchlist",
        route = "watchlist",
        unselectedIcon = Icons.Outlined.Star,
        selectedIcon = Icons.Filled.Star
    )
}