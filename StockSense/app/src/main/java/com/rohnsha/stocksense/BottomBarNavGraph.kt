package com.rohnsha.stocksense

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rohnsha.stocksense.screens.Details
import com.rohnsha.stocksense.screens.HomeScreen
import com.rohnsha.stocksense.screens.SearchScrn
import com.rohnsha.stocksense.screens.WatchlistScreen

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ){
        composable(route = BottomBarScreen.Home.route){
            HomeScreen()
        }
        composable(route = BottomBarScreen.Search.route){
            SearchScrn()
        }
        composable(route = BottomBarScreen.Watchlist.route){
            WatchlistScreen()
        }
        composable(route = BottomBarScreen.More.route){
            WatchlistScreen()
        }
        composable(route = BottomBarScreen.StockDetails.route){
            Details(stockSymbol = "lala", ltp = "0", change = "0", {})
        }
    }
}