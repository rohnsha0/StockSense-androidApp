package com.rohnsha.stocksense.navigation.bottombar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rohnsha.stocksense.ui.screen.AnalysisInsightsScreen
import com.rohnsha.stocksense.ui.screen.ChatbotScreen
import com.rohnsha.stocksense.ui.screen.PortfolioWatchlistScreen
import com.rohnsha.stocksense.ui.screen.ProfileSettingsScreen
import com.rohnsha.stocksense.ui.screen.StockDetailScreen
import com.rohnsha.stocksense.ui.screen.StockListScreen
import com.rohnsha.stocksense.ui.screen.StockSenseHomePage

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    padding: PaddingValues
) {

    NavHost(
        navController = navController,
        startDestination = bottomNavItems.HomePage.route,
    ) {

        composable(bottomNavItems.HomePage.route) {
            StockSenseHomePage(padding = padding, navController= navController)
        }

        composable(bottomNavItems.Watchlist.route) {
            PortfolioWatchlistScreen(padding = padding, navController= navController)
        }

        composable(bottomNavItems.Chatbot.route) {
            ChatbotScreen()
        }

        composable(bottomNavItems.Profile.route) {
            ProfileSettingsScreen()
        }

        composable(bottomNavItems.AnalysisInsigth.route){
            AnalysisInsightsScreen(padding = padding, navController= navController)
        }

        composable(bottomNavItems.Details.route){
            StockDetailScreen()
        }

        composable(bottomNavItems.StockList.route){
            StockListScreen(
                padding = padding,
                navController= navController
            )
        }


    }


}