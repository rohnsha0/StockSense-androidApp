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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data Models
data class StockDetail(
    val symbol: String,
    val name: String,
    val currentPrice: String,
    val changePercent: Float,
    val changeAmount: String,
    val dayHigh: String,
    val dayLow: String,
    val open: String,
    val previousClose: String,
    val volume: String,
    val marketCap: String,
    val peRatio: String,
    val week52High: String,
    val week52Low: String
)

data class PredictionDetail(
    val nextDayPrediction: String,
    val predictedChange: Float,
    val confidence: Int,
    val targetPrice: String,
    val predictionDate: String,
    val modelAccuracy: Int
)

data class ChartTimeframe(
    val label: String,
    val isSelected: Boolean
)

enum class TabSection { OVERVIEW, PREDICTION, FUNDAMENTALS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen() {
    var selectedTab by remember { mutableStateOf(TabSection.OVERVIEW) }
    var selectedTimeframe by remember { mutableStateOf("1D") }
    var isWatchlisted by remember { mutableStateOf(false) }

    // Mock Data
    val stockDetail = StockDetail(
        symbol = "RELIANCE",
        name = "Reliance Industries Ltd.",
        currentPrice = "₹2,845.30",
        changePercent = 2.34f,
        changeAmount = "+₹65.20",
        dayHigh = "₹2,867.50",
        dayLow = "₹2,780.15",
        open = "₹2,795.00",
        previousClose = "₹2,780.10",
        volume = "1.24 Cr",
        marketCap = "₹19.25 Lakh Cr",
        peRatio = "28.45",
        week52High = "₹3,024.90",
        week52Low = "₹2,220.30"
    )

    val predictionDetail = PredictionDetail(
        nextDayPrediction = "₹2,911.75",
        predictedChange = 2.34f,
        confidence = 92,
        targetPrice = "₹3,050.00",
        predictionDate = "Tomorrow, 15:30 IST",
        modelAccuracy = 92
    )

    val timeframes = listOf("1D", "1W", "1M", "3M", "1Y", "5Y")
    val chartData = listOf(2.3f, 2.7f, 2.4f, 2.9f, 3.1f, 2.8f, 3.3f, 3.6f, 3.4f, 3.8f)

    Scaffold(
        topBar = {
            StockDetailTopBar(
                stockDetail = stockDetail,
                isWatchlisted = isWatchlisted,
                onBackClick = { /* Navigate back */ },
                onWatchlistClick = { isWatchlisted = !isWatchlisted },
                onShareClick = { /* Share stock */ }
            )
        },
        floatingActionButton = {
            ChatbotFAB(stockSymbol = stockDetail.symbol)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Stock Header
            item {
                StockHeader(stockDetail)
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Chart Section
            item {
                ChartSection(
                    timeframes = timeframes,
                    selectedTimeframe = selectedTimeframe,
                    onTimeframeSelected = { selectedTimeframe = it },
                    chartData = chartData,
                    isPositive = stockDetail.changePercent > 0
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Tab Section
            item {
                TabSelector(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Content based on selected tab
            when (selectedTab) {
                TabSection.OVERVIEW -> {
                    item {
                        OverviewSection(stockDetail)
                    }
                }
                TabSection.PREDICTION -> {
                    item {
                        PredictionSection(predictionDetail, stockDetail)
                    }
                }
                TabSection.FUNDAMENTALS -> {
                    item {
                        FundamentalsSection(stockDetail)
                    }
                }
            }

            // About Company
            item {
                Spacer(modifier = Modifier.height(24.dp))
                AboutCompanySection(stockDetail.name)
            }

            // Ask AI Button
            item {
                Spacer(modifier = Modifier.height(20.dp))
                AskAIButton(stockDetail.symbol)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailTopBar(
    stockDetail: StockDetail,
    isWatchlisted: Boolean,
    onBackClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onShareClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stockDetail.symbol,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stockDetail.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onWatchlistClick) {
                Icon(
                    imageVector = if (isWatchlisted) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Watchlist",
                    tint = if (isWatchlisted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun StockHeader(stockDetail: StockDetail) {
    val isPositive = stockDetail.changePercent > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = stockDetail.currentPrice,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPositive)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPositive) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${if (isPositive) "+" else ""}${stockDetail.changePercent}%",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Text(
                        text = stockDetail.changeAmount,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "NSE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "LIVE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ChartSection(
    timeframes: List<String>,
    selectedTimeframe: String,
    onTimeframeSelected: (String) -> Unit,
    chartData: List<Float>,
    isPositive: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Timeframe Selector
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(timeframes) { timeframe ->
                TimeframeChip(
                    label = timeframe,
                    isSelected = timeframe == selectedTimeframe,
                    onClick = { onTimeframeSelected(timeframe) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Chart Placeholder
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(280.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Advanced Chart Visualization
                AdvancedStockChart(
                    data = chartData,
                    isPositive = isPositive,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun AdvancedStockChart(
    data: List<Float>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = androidx.compose.ui.graphics.Path()
        val fillPath = androidx.compose.ui.graphics.Path()

        val max = data.maxOrNull() ?: 1f
        val min = data.minOrNull() ?: 0f
        val range = max - min

        // Create path
        data.forEachIndexed { index, value ->
            val x = (index.toFloat() / (data.size - 1)) * width
            val y = height - ((value - min) / range * height)

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        // Close fill path
        fillPath.lineTo(width, height)
        fillPath.close()

        // Draw gradient fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = if (isPositive) listOf(
                    Color(0xFF4CAF50).copy(alpha = 0.3f),
                    Color.Transparent
                ) else listOf(
                    Color(0xFFF44336).copy(alpha = 0.3f),
                    Color.Transparent
                )
            )
        )

        // Draw line
        drawPath(
            path = path,
            color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
        )
    }
}

@Composable
fun TimeframeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TabSelector(
    selectedTab: TabSection,
    onTabSelected: (TabSection) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TabChip(
                label = "Overview",
                icon = Icons.Outlined.Analytics,
                isSelected = selectedTab == TabSection.OVERVIEW,
                onClick = { onTabSelected(TabSection.OVERVIEW) }
            )
        }
        item {
            TabChip(
                label = "AI Prediction",
                icon = Icons.Outlined.AutoAwesome,
                isSelected = selectedTab == TabSection.PREDICTION,
                onClick = { onTabSelected(TabSection.PREDICTION) }
            )
        }
        item {
            TabChip(
                label = "Fundamentals",
                icon = Icons.Outlined.ShowChart,
                isSelected = selectedTab == TabSection.FUNDAMENTALS,
                onClick = { onTabSelected(TabSection.FUNDAMENTALS) }
            )
        }
    }
}

@Composable
fun TabChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.secondaryContainer
        else
            MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (isSelected)
                    MaterialTheme.colorScheme.onSecondaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onSecondaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OverviewSection(stockDetail: StockDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Today's Stats",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                StatRow("Day High", stockDetail.dayHigh)
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                StatRow("Day Low", stockDetail.dayLow)
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                StatRow("Open", stockDetail.open)
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                StatRow("Prev. Close", stockDetail.previousClose)
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                StatRow("Volume", stockDetail.volume)
            }
        }
    }
}

@Composable
fun PredictionSection(predictionDetail: PredictionDetail, stockDetail: StockDetail) {
    val isPositive = predictionDetail.predictedChange > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Main Prediction Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isPositive)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Next Day Prediction",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = predictionDetail.nextDayPrediction,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isPositive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPositive) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${if (isPositive) "+" else ""}${predictionDetail.predictedChange}%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PredictionMetric(
                        label = "Confidence",
                        value = "${predictionDetail.confidence}%",
                        icon = Icons.Outlined.Verified
                    )
                    PredictionMetric(
                        label = "Model Accuracy",
                        value = "${predictionDetail.modelAccuracy}%",
                        icon = Icons.Outlined.Psychology
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Target Price Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
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
                        text = "7-Day Target Price",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = predictionDetail.targetPrice,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Model Info
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Powered by LSTM Neural Network • Trained on 5 years of NIFTY50 data",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PredictionMetric(label: String, value: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
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
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun FundamentalsSection(stockDetail: StockDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Key Metrics",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                label = "Market Cap",
                value = stockDetail.marketCap,
                icon = Icons.Outlined.Analytics,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "P/E Ratio",
                value = stockDetail.peRatio,
                icon = Icons.Outlined.Calculate,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "52 Week Range",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Low",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stockDetail.week52Low,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "High",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stockDetail.week52High,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AboutCompanySection(companyName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "About $companyName",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Reliance Industries Limited is an Indian multinational conglomerate, headquartered in Mumbai. It has diverse businesses including energy, petrochemicals, natural gas, retail, telecommunications, mass media, and textiles.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CompanyInfoChip(
                        label = "Industry",
                        value = "Conglomerate",
                        modifier = Modifier.weight(1f)
                    )
                    CompanyInfoChip(
                        label = "Founded",
                        value = "1973",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun CompanyInfoChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AskAIButton(stockSymbol: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { /* Navigate to chatbot */ },
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Column {
                    Text(
                        text = "Ask AI about $stockSymbol",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Get insights & analysis",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ChatbotFAB(stockSymbol: String) {
    ExtendedFloatingActionButton(
        onClick = { /* Open chatbot with stock context */ },
        icon = {
            Icon(
                imageVector = Icons.Filled.ChatBubble,
                contentDescription = "AI Assistant"
            )
        },
        text = {
            Text(
                text = "Ask AI",
                fontWeight = FontWeight.Bold
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
}