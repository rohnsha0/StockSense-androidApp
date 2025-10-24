package com.rohnsha.stocksense.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data Models
data class WatchlistStock(
    val symbol: String,
    val name: String,
    val currentPrice: String,
    val priceChange: Float,
    val changePercent: Float,
    val predictedChange: Float,
    val confidence: Int,
    val volume: String,
    val dayHigh: String,
    val dayLow: String,
    val sparklineData: List<Float>,
    val addedDate: String
)

data class PortfolioSummary(
    val totalValue: String,
    val totalGainLoss: Float,
    val totalGainLossAmount: String,
    val stocksCount: Int,
    val bestPerformer: String,
    val worstPerformer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioWatchlistScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Watchlist", "Portfolio")

    // Mock Data
    val portfolioSummary = PortfolioSummary(
        totalValue = "₹8,45,230",
        totalGainLoss = 12.4f,
        totalGainLossAmount = "₹93,450",
        stocksCount = 8,
        bestPerformer = "RELIANCE +15.2%",
        worstPerformer = "TECHM -3.8%"
    )

    val watchlistStocks = listOf(
        WatchlistStock(
            "RELIANCE", "Reliance Industries", "₹2,845.30", 45.20f, 1.6f, 2.3f, 92,
            "45.2L", "₹2,890", "₹2,820",
            listOf(1f, 1.2f, 0.9f, 1.5f, 1.8f, 2.1f, 2.3f),
            "Added 2 days ago"
        ),
        WatchlistStock(
            "TCS", "Tata Consultancy Services", "₹3,542.15", -28.50f, -0.8f, -0.8f, 87,
            "32.8L", "₹3,580", "₹3,525",
            listOf(2f, 1.8f, 1.9f, 1.5f, 1.3f, 1.0f, 0.9f),
            "Added 5 days ago"
        ),
        WatchlistStock(
            "HDFCBANK", "HDFC Bank", "₹1,645.80", 24.60f, 1.5f, 1.5f, 90,
            "58.3L", "₹1,658", "₹1,630",
            listOf(0.5f, 0.8f, 1.0f, 1.2f, 1.4f, 1.6f, 1.5f),
            "Added 1 week ago"
        ),
        WatchlistStock(
            "INFY", "Infosys", "₹1,489.25", 13.40f, 0.9f, 0.9f, 85,
            "41.2L", "₹1,495", "₹1,478",
            listOf(0.3f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 0.9f),
            "Added 3 days ago"
        ),
        WatchlistStock(
            "TATAMOTORS", "Tata Motors", "₹892.40", 31.80f, 3.7f, 2.8f, 83,
            "95.6L", "₹905", "₹878",
            listOf(0.8f, 1.2f, 1.5f, 2.0f, 2.5f, 3.2f, 3.7f),
            "Added today"
        )
    )

    Scaffold(
        topBar = {
            PortfolioTopBar()
        },
        floatingActionButton = {
            AddStockFAB()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(4.dp)
                            .padding(horizontal = 24.dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Content based on selected tab
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "tab_content"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> WatchlistContent(watchlistStocks)
                    1 -> PortfolioContent(portfolioSummary, watchlistStocks.take(3))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioTopBar() {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "My Portfolio",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Track & Analyze",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { /* Navigate back */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Sort */ }) {
                Icon(
                    imageVector = Icons.Outlined.Sort,
                    contentDescription = "Sort"
                )
            }
            IconButton(onClick = { /* More options */ }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "More"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun WatchlistContent(stocks: List<WatchlistStock>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            WatchlistHeader(stockCount = stocks.size)
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(stocks) { stock ->
            WatchlistStockCard(stock)
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun WatchlistHeader(stockCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tracking",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$stockCount Stocks",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Outlined.Visibility,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(28.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun WatchlistStockCard(stock: WatchlistStock) {
    val isPositive = stock.changePercent > 0
    val isPredictionPositive = stock.predictedChange > 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { /* Navigate to stock detail */ },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = stock.symbol.take(2),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stock.symbol,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stock.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                IconButton(onClick = { /* Remove from watchlist */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stock.currentPrice,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "${if (isPositive) "+" else ""}${stock.priceChange} (${if (isPositive) "+" else ""}${stock.changePercent}%)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                MiniSparkline(
                    data = stock.sparklineData,
                    isPositive = isPositive,
                    modifier = Modifier.size(100.dp, 60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatChip(
                    label = "High",
                    value = stock.dayHigh,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label = "Low",
                    value = stock.dayLow,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label = "Volume",
                    value = stock.volume,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prediction Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isPredictionPositive)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (isPredictionPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "AI Prediction: ${if (isPredictionPositive) "+" else ""}${stock.predictedChange}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isPredictionPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                    Text(
                        text = "${stock.confidence}% confidence",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stock.addedDate,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PortfolioContent(summary: PortfolioSummary, holdings: List<WatchlistStock>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            PortfolioSummaryCard(summary)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionHeader(
                title = "Holdings",
                subtitle = "${summary.stocksCount} Stocks",
                icon = Icons.Outlined.AccountBalance
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(holdings) { stock ->
            PortfolioHoldingCard(stock)
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun PortfolioSummaryCard(summary: PortfolioSummary) {
    val isPositive = summary.totalGainLoss > 0
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
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
                    .height(220.dp)
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
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Value",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = summary.totalValue,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isPositive)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Column {
                            Text(
                                text = "${if (isPositive) "+" else ""}${summary.totalGainLossAmount}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "${if (isPositive) "+" else ""}${summary.totalGainLoss}% Overall",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PerformanceChip(
                        label = "Best",
                        value = summary.bestPerformer,
                        icon = Icons.Outlined.TrendingUp,
                        isPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                    PerformanceChip(
                        label = "Worst",
                        value = summary.worstPerformer,
                        icon = Icons.Outlined.TrendingDown,
                        isPositive = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PerformanceChip(
    label: String,
    value: String,
    icon: ImageVector,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun PortfolioHoldingCard(stock: WatchlistStock) {
    val isPositive = stock.changePercent > 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { /* Navigate to stock detail */ },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stock.symbol.take(2),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stock.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stock.currentPrice,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPositive)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "${if (isPositive) "+" else ""}${stock.changePercent}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${if (isPositive) "+" else ""}${stock.priceChange}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AddStockFAB() {
    ExtendedFloatingActionButton(
        onClick = { /* Open stock search to add */ },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add Stock"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add Stock")
    }
}

@Composable
fun MiniSparkline(
    data: List<Float>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val path = androidx.compose.ui.graphics.Path()

        val max = data.maxOrNull() ?: 1f
        val min = data.minOrNull() ?: 0f
        val range = max - min

        val width = size.width
        val height = size.height

        data.forEachIndexed { index, value ->
            val x = (index.toFloat() / (data.size - 1)) * width
            val y = height - ((value - min) / range * height)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
        )
    }
}