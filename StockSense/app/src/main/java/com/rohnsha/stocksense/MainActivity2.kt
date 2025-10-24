package com.rohnsha.stocksense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rohnsha.stocksense.navigation.bottombar.BottomNavGraph
import com.rohnsha.stocksense.navigation.bottombar.bottomNavItems
import com.rohnsha.stocksense.ui.screen.AnalysisInsightsScreen
import com.rohnsha.stocksense.ui.screen.ChatbotScreen
import com.rohnsha.stocksense.ui.screen.PortfolioWatchlistScreen
import com.rohnsha.stocksense.ui.screen.ProfileSettingsScreen
import com.rohnsha.stocksense.ui.screen.StockDetailScreen
import com.rohnsha.stocksense.ui.screen.StockListScreen
import com.rohnsha.stocksense.ui.screen.StockSenseHomePage
import com.rohnsha.stocksense.ui.theme.StockSenseTheme

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StockSenseTheme {

                val items= listOf(
                    bottomNavItems.HomePage,
                    bottomNavItems.StockList,
                    bottomNavItems.AnalysisInsigth,
                    bottomNavItems.Watchlist,
                )

                var selectedItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }

                val navcontroller= rememberNavController()
                val navBackStackEntry by navcontroller.currentBackStackEntryAsState()
                val currentDestination= navBackStackEntry?.destination
                val currentItemIndex = items.indexOfFirst { it.route == currentDestination?.route }
                val bottomDestination= items.any { it.route== currentDestination?.route }

                if (currentItemIndex != -1 && currentItemIndex != selectedItemIndex){
                    selectedItemIndex= currentItemIndex
                }

                Scaffold(
                    bottomBar = {
                        if (bottomDestination){
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 0.dp
                            ) {
                                items.forEachIndexed { index, bottomNavItems ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex==index,
                                        onClick = {
                                            selectedItemIndex= index
                                            navcontroller.navigate(bottomNavItems.route){
                                                popUpTo(navcontroller.graph.findStartDestination().id)
                                                launchSingleTop= true
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = bottomNavItems.title,
                                                fontSize = 12.sp,
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        icon = {
                                            Icon(
                                                imageVector = if (selectedItemIndex==index){
                                                    bottomNavItems.selectedIcon
                                                } else bottomNavItems.unselectedIcon,
                                                contentDescription = bottomNavItems.title
                                            )
                                        },
                                        alwaysShowLabel = false,
                                    )
                                }
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ){ paddingValues ->
                    BottomNavGraph(
                        navController = navcontroller,
                        padding = paddingValues
                    )
                }


              // StockSenseHomePage()
               // StockDetailScreen()
                //StockListScreen {  }
                //ChatbotScreen()
                //PortfolioWatchlistScreen()
               // AnalysisInsightsScreen()
                //ProfileSettingsScreen()
            }
        }
    }
}