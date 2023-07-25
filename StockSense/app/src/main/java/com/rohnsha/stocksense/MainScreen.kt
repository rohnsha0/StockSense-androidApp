package com.rohnsha.stocksense

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController= rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) {
        BottomNavGraph(navController = navController)
    }
}

@Composable
fun BottomBar(navController: NavHostController,) {
    val screens= listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Search,
        BottomBarScreen.Watchlist,
        BottomBarScreen.More
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination= navBackStackEntry?.destination

    BottomNavigation(
        modifier = Modifier
            .padding(12.dp)
            .height(81.dp)
            .graphicsLayer {
                shape = RoundedCornerShape(size = 20.dp)
                clip = true
            },
        backgroundColor = Color(0xFFF4F5FA),
        elevation = AppBarDefaults.BottomAppBarElevation
    ) {
        screens.forEach {screens ->
            AddItem(screen = screens, currentDestination = currentDestination, navController = navController)
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    BottomNavigationItem(
        alwaysShowLabel = false,
        modifier = Modifier.padding(8.dp),
        label = {
                Text(text = screen.title,
                modifier = Modifier
                    .padding(8.dp),
                fontSize = 12.sp,
                )
        },
        icon = {
               Icon(
                   imageVector = screen.icon,
                   contentDescription = "Nav Icon",
                    modifier = Modifier.padding(8.dp))
        },
        selected = currentDestination?.hierarchy?.any {
                it.route==screen.route
        } == true,
        onClick = { navController.navigate(screen.route) })
}