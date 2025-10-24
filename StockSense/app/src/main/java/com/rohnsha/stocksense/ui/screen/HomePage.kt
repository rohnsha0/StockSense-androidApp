package com.rohnsha.stocksense.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import com.rohnsha.stocksense.navigation.bottombar.bottomNavItems

// Data Models
data class MarketOverview(
    val indexValue: String,
    val changePercent: Float,
    val volume: String,
    val timestamp: String
)

data class StockPrediction(
    val symbol: String,
    val name: String,
    val currentPrice: String,
    val predictedChange: Float,
    val confidence: Int,
    val trend: TrendDirection,
    val sparklineData: List<Float>
)

enum class TrendDirection { UP, DOWN, NEUTRAL }

data class TopMover(
    val symbol: String,
    val name: String,
    val changePercent: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockSenseHomePage(
    padding: PaddingValues,
    navController: NavHostController
) {
    // Mock Data
    val marketOverview = MarketOverview(
        indexValue = "22,147.50",
        changePercent = 1.24f,
        volume = "₹45,231 Cr",
        timestamp = "15:30 IST"
    )

    val featuredPredictions = listOf(
        StockPrediction("RELIANCE", "Reliance Industries", "₹2,845.30", 2.3f, 92, TrendDirection.UP, listOf(1f, 1.2f, 0.9f, 1.5f, 1.8f, 2.1f)),
        StockPrediction("TCS", "Tata Consultancy Services", "₹3,542.15", -0.8f, 87, TrendDirection.DOWN, listOf(2f, 1.8f, 1.9f, 1.5f, 1.3f, 1.0f)),
        StockPrediction("HDFCBANK", "HDFC Bank", "₹1,645.80", 1.5f, 90, TrendDirection.UP, listOf(0.5f, 0.8f, 1.0f, 1.2f, 1.4f, 1.6f)),
        StockPrediction("INFY", "Infosys", "₹1,489.25", 0.9f, 85, TrendDirection.UP, listOf(0.3f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f))
    )

    val topGainers = listOf(
        TopMover("ADANIENT", "Adani Enterprises", 4.2f),
        TopMover("TATAMOTORS", "Tata Motors", 3.8f),
        TopMover("SUNPHARMA", "Sun Pharma", 2.9f)
    )

    val topLosers = listOf(
        TopMover("BAJFINANCE", "Bajaj Finance", -2.1f),
        TopMover("TECHM", "Tech Mahindra", -1.7f),
        TopMover("CIPLA", "Cipla", -1.3f)
    )

    Scaffold(
        topBar = {
            HomeTopBar()
        },
        floatingActionButton = {
            ChatbotFAB(padding = padding, navController= navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding(), bottom = padding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Market Overview Card
            item {
                MarketOverviewCard(marketOverview)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Top Movers Section
            item {
                TopMoversSection(topGainers, topLosers)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Featured Predictions Section
            item {
                SectionHeader(
                    title = "AI Predictions",
                    subtitle = "LSTM Model • 92% Accuracy",
                    icon = Icons.Outlined.AutoAwesome
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(featuredPredictions) { prediction ->
                PredictionCard(prediction = prediction, navController = navController)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // View All Button
            item {
                ViewAllButton(navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "StockSense",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "NIFTY50 Intelligence",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Search */ }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = { /* Notifications */ }) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.offset(x = (-8).dp, y = 8.dp)
                ) {
                    Text("3")
                }
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun MarketOverviewCard(overview: MarketOverview) {
    val isPositive = overview.changePercent > 0
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPositive)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box {
            // Gradient Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = if (isPositive) listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                            ) else listOf(
                                MaterialTheme.colorScheme.errorContainer,
                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                            ),
                            startX = shimmer * 1000f,
                            endX = shimmer * 2000f
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NIFTY 50",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = overview.indexValue,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isPositive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPositive) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${if (isPositive) "+" else ""}${overview.changePercent}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoChip(
                        label = "Volume",
                        value = overview.volume,
                        icon = Icons.Outlined.ShowChart
                    )
                    InfoChip(
                        label = "Updated",
                        value = overview.timestamp,
                        icon = Icons.Outlined.Schedule
                    )
                }
            }
        }
    }
}

@Composable
fun InfoChip(label: String, value: String, icon: ImageVector) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun TopMoversSection(gainers: List<TopMover>, losers: List<TopMover>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(
            title = "Market Movers",
            subtitle = "Live Updates",
            icon = Icons.Outlined.TrendingUp
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Gainers
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Top Gainers",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    gainers.forEach { mover ->
                        MoverItem(mover, isGainer = true)
                        if (mover != gainers.last()) Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Top Losers
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDownward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Top Losers",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    losers.forEach { mover ->
                        MoverItem(mover, isGainer = false)
                        if (mover != losers.last()) Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MoverItem(mover: TopMover, isGainer: Boolean) {
    Column {
        Text(
            text = mover.symbol,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${if (isGainer) "+" else ""}${mover.changePercent}%",
            style = MaterialTheme.typography.labelSmall,
            color = if (isGainer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PredictionCard(prediction: StockPrediction, navController: NavHostController) {
    val isPositive = prediction.predictedChange > 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { navController.navigate(bottomNavItems.Details.route) },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = prediction.symbol.take(2),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = prediction.symbol,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = prediction.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = prediction.currentPrice,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPositive)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPositive) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "${if (isPositive) "+" else ""}${prediction.predictedChange}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Analytics,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${prediction.confidence}% Confidence",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Mini Sparkline
            MiniSparkline(
                data = prediction.sparklineData,
                isPositive = isPositive,
                modifier = Modifier.size(80.dp, 50.dp)
            )
        }
    }
}

@Composable
fun MiniSparkline(
    data: List<Float>,
    isPositive: Boolean,
    modifier: Modifier,
    width: Dp = 80.dp,
    height: Dp = 50.dp
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val path = androidx.compose.ui.graphics.Path()

        val max = data.maxOrNull() ?: 1f
        val min = data.minOrNull() ?: 0f
        val range = max - min

        data.forEachIndexed { index, value ->
            val x: Dp = (index.toFloat() / (data.size - 1)) * width
            val y: Dp = height - ((value - min) / range * height)

            if (index == 0) {
                path.moveTo(x.value, y.value)
            } else {
                path.lineTo(x.value, y.value)
            }
        }

        drawPath(
            path = path,
            color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
        )
    }
}

@Composable
fun ViewAllButton(navController: NavHostController) {
    OutlinedButton(
        onClick = { navController.navigate(bottomNavItems.StockList.route) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("View All Stocks")
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun ChatbotFAB(padding: PaddingValues, navController: NavHostController) {
    FloatingActionButton(
        onClick = { navController.navigate(bottomNavItems.Chatbot.route) },
        modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Icon(
            imageVector = Icons.Filled.ChatBubble,
            contentDescription = "AI Assistant"
        )
    }
}