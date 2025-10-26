package com.rohnsha.stocksense.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rohnsha.stocksense.utils.dataclass.StockItem
import com.rohnsha.stocksense.utils.enums.FilterSector
import com.rohnsha.stocksense.utils.enums.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListScreen(
    onBackClick: () -> Unit = {},
    onStockClick: (StockItem) -> Unit = {},
    onChatbotClick: () -> Unit = {},
    padding: PaddingValues,
    navController: NavHostController
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf(SortOption.ALPHABETICAL) }
    var selectedSector by remember { mutableStateOf(FilterSector.ALL) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }

    val mockStocks = remember {
        listOf(
            StockItem(
                "RELIANCE",
                "Reliance Industries",
                "ENERGY",
                "₹2,845.30",
                1.2f,
                2.3f,
                92,
                "23.4M",
                listOf(1f, 1.2f, 0.9f, 1.5f, 1.8f, 2.1f)
            ),
            StockItem(
                "TCS",
                "Tata Consultancy Services",
                "IT",
                "₹3,542.15",
                -0.5f,
                -0.8f,
                87,
                "12.1M",
                listOf(2f, 1.8f, 1.9f, 1.5f, 1.3f, 1.0f)
            ),
            StockItem(
                "HDFCBANK",
                "HDFC Bank",
                "BANKING",
                "₹1,645.80",
                0.8f,
                1.5f,
                90,
                "45.2M",
                listOf(0.5f, 0.8f, 1.0f, 1.2f, 1.4f, 1.6f)
            ),
            StockItem(
                "INFY",
                "Infosys",
                "IT",
                "₹1,489.25",
                0.6f,
                0.9f,
                85,
                "18.7M",
                listOf(0.3f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f)
            ),
            StockItem(
                "ICICIBANK",
                "ICICI Bank",
                "BANKING",
                "₹1,123.45",
                -0.3f,
                0.5f,
                88,
                "32.1M",
                listOf(1.5f, 1.3f, 1.4f, 1.2f, 1.3f, 1.4f)
            ),
            StockItem(
                "HINDUNILVR",
                "Hindustan Unilever",
                "FMCG",
                "₹2,456.70",
                1.5f,
                1.8f,
                91,
                "8.5M",
                listOf(0.8f, 1.0f, 1.1f, 1.3f, 1.5f, 1.7f)
            ),
            StockItem(
                "ITC",
                "ITC Limited",
                "FMCG",
                "₹445.60",
                0.9f,
                1.2f,
                89,
                "28.9M",
                listOf(0.6f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f)
            ),
            StockItem(
                "SBIN",
                "State Bank of India",
                "BANKING",
                "₹623.40",
                -1.2f,
                -0.5f,
                83,
                "56.3M",
                listOf(1.8f, 1.5f, 1.4f, 1.2f, 1.0f, 0.9f)
            ),
            StockItem(
                "BHARTIARTL",
                "Bharti Airtel",
                "IT",
                "₹1,234.50",
                2.1f,
                2.5f,
                93,
                "15.2M",
                listOf(0.4f, 0.7f, 1.0f, 1.4f, 1.8f, 2.2f)
            ),
            StockItem(
                "SUNPHARMA",
                "Sun Pharmaceutical",
                "PHARMA",
                "₹1,567.80",
                1.8f,
                2.0f,
                90,
                "9.8M",
                listOf(0.7f, 0.9f, 1.2f, 1.5f, 1.7f, 1.9f)
            ),
            StockItem(
                "TATAMOTORS",
                "Tata Motors",
                "AUTO",
                "₹789.30",
                3.2f,
                3.5f,
                86,
                "42.1M",
                listOf(0.5f, 1.0f, 1.5f, 2.0f, 2.8f, 3.3f)
            ),
            StockItem(
                "TATASTEEL",
                "Tata Steel",
                "METALS",
                "₹145.60",
                -1.5f,
                -1.0f,
                84,
                "67.4M",
                listOf(2.0f, 1.7f, 1.5f, 1.3f, 1.1f, 0.9f)
            ),
            StockItem(
                "WIPRO",
                "Wipro Limited",
                "IT",
                "₹456.20",
                0.7f,
                1.1f,
                86,
                "19.3M",
                listOf(0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f)
            ),
            StockItem(
                "AXISBANK",
                "Axis Bank",
                "BANKING",
                "₹987.65",
                -0.9f,
                0.3f,
                85,
                "28.4M",
                listOf(1.2f, 1.0f, 0.9f, 0.8f, 0.9f, 1.0f)
            ),
            StockItem(
                "MARUTI",
                "Maruti Suzuki",
                "AUTO",
                "₹9,876.50",
                1.4f,
                1.9f,
                91,
                "5.2M",
                listOf(0.9f, 1.1f, 1.3f, 1.5f, 1.7f, 1.9f)
            ),
            StockItem(
                "BAJFINANCE",
                "Bajaj Finance",
                "BANKING",
                "₹7,234.80",
                -2.1f,
                -1.5f,
                87,
                "12.8M",
                listOf(2.5f, 2.2f, 2.0f, 1.7f, 1.5f, 1.3f)
            ),
            StockItem(
                "ADANIENT",
                "Adani Enterprises",
                "ENERGY",
                "₹2,567.40",
                4.2f,
                3.8f,
                89,
                "34.6M",
                listOf(0.3f, 0.8f, 1.5f, 2.3f, 3.2f, 4.0f)
            ),
            StockItem(
                "ONGC",
                "Oil & Natural Gas Corporation",
                "ENERGY",
                "₹178.90",
                0.5f,
                0.8f,
                84,
                "89.2M",
                listOf(0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f)
            ),
            StockItem(
                "KOTAKBANK",
                "Kotak Mahindra Bank",
                "BANKING",
                "₹1,845.30",
                1.1f,
                1.6f,
                90,
                "21.7M",
                listOf(0.7f, 0.9f, 1.1f, 1.3f, 1.5f, 1.6f)
            ),
            StockItem(
                "LT",
                "Larsen & Toubro",
                "METALS",
                "₹3,456.70",
                1.3f,
                1.7f,
                88,
                "14.9M",
                listOf(0.8f, 1.0f, 1.2f, 1.4f, 1.6f, 1.7f)
            )
        )
    }

    // Simulate search delay
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            isSearching = true
            kotlinx.coroutines.delay(300) // Simulate API delay
            isSearching = false
        } else {
            isSearching = false
        }
    }

    val filteredStocks = remember(searchQuery, selectedSector, selectedSortOption, mockStocks, isSearching) {
        if (isSearching) {
            emptyList()
        } else {
            var filtered = mockStocks

            // Apply search filter
            if (searchQuery.isNotEmpty()) {
                filtered = filtered.filter {
                    it.symbol.contains(searchQuery, ignoreCase = true) ||
                            it.name.contains(searchQuery, ignoreCase = true)
                }
            }

            // Apply sector filter
            if (selectedSector != FilterSector.ALL) {
                filtered = filtered.filter { it.sector == selectedSector.name }
            }

            // Apply sorting
            when (selectedSortOption) {
                SortOption.ALPHABETICAL -> filtered.sortedBy { it.symbol }
                SortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending {
                    it.currentPrice.replace("₹", "").replace(",", "").toFloatOrNull() ?: 0f
                }
                SortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy {
                    it.currentPrice.replace("₹", "").replace(",", "").toFloatOrNull() ?: 0f
                }
                SortOption.PREDICTED_CHANGE -> filtered.sortedByDescending { it.predictedChange }
                SortOption.CONFIDENCE -> filtered.sortedByDescending { it.confidence }
            }
        }
    }

    val listState = rememberLazyListState()
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 3 }
    }

    Scaffold(
        topBar = {
            StockListTopBar(
                searchQuery = searchQuery,
                isSearchActive = isSearchActive,
                onSearchQueryChange = { searchQuery = it },
                onSearchActiveChange = { isSearchActive = it },
                onBackClick = onBackClick,
                onFilterClick = { showFilterSheet = true },
                onSortClick = { showSortSheet = true }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedVisibility(
                    visible = showScrollToTop,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    SmallFloatingActionButton(
                        onClick = {
                            // Scroll to top
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            contentDescription = "Scroll to top"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
                .fillMaxSize(),
            ) {
            // Active filters chip row
            ActiveFiltersRow(
                selectedSector = selectedSector,
                selectedSortOption = selectedSortOption,
                onClearSector = { selectedSector = FilterSector.ALL },
                onClearSort = { selectedSortOption = SortOption.ALPHABETICAL }
            )

            // Stock count header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedContent(
                        targetState = isSearching,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "stock_count"
                    ) { searching ->
                        if (searching) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Searching...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Text(
                                text = "${filteredStocks.size} Stocks",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
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
                            text = "LSTM Predictions",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Stock List
            if (isSearching) {
                // Show loading state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Searching stocks...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (filteredStocks.isEmpty()) {
                EmptyState(searchQuery = searchQuery)
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredStocks,
                        key = { it.symbol }
                    ) { stock ->
                        StockListItem(
                            stock = stock,
                            onClick = { onStockClick(stock) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            selectedSector = selectedSector,
            onSectorSelected = {
                selectedSector = it
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }

    // Sort Bottom Sheet
    if (showSortSheet) {
        SortBottomSheet(
            selectedOption = selectedSortOption,
            onOptionSelected = {
                selectedSortOption = it
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListTopBar(
    searchQuery: String,
    isSearchActive: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit
) {
    TopAppBar(
        title = {
            if (isSearchActive) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search stocks...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                )
            } else {
                Column {
                    Text(
                        text = "All Stocks",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "NIFTY 50",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                if (isSearchActive && searchQuery.isNotEmpty()) {
                    onSearchQueryChange("")
                } else if (isSearchActive) {
                    onSearchActiveChange(false)
                } else {
                    onBackClick()
                }
            }) {
                Icon(
                    imageVector = if (isSearchActive && searchQuery.isNotEmpty())
                        Icons.Filled.Close
                    else
                        Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            if (!isSearchActive) {
                IconButton(onClick = { onSearchActiveChange(true) }) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search"
                    )
                }
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = "Filter"
                    )
                }
                IconButton(onClick = onSortClick) {
                    Icon(
                        imageVector = Icons.Outlined.Sort,
                        contentDescription = "Sort"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun ActiveFiltersRow(
    selectedSector: FilterSector,
    selectedSortOption: SortOption,
    onClearSector: () -> Unit,
    onClearSort: () -> Unit
) {
    val hasActiveFilters = selectedSector != FilterSector.ALL ||
            selectedSortOption != SortOption.ALPHABETICAL

    AnimatedVisibility(
        visible = hasActiveFilters,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedSector != FilterSector.ALL) {
                FilterChip(
                    selected = true,
                    onClick = onClearSector,
                    label = { Text(selectedSector.name) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Clear",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            if (selectedSortOption != SortOption.ALPHABETICAL) {
                FilterChip(
                    selected = true,
                    onClick = onClearSort,
                    label = {
                        Text(
                            when (selectedSortOption) {
                                SortOption.PRICE_HIGH_TO_LOW -> "Price ↓"
                                SortOption.PRICE_LOW_TO_HIGH -> "Price ↑"
                                SortOption.PREDICTED_CHANGE -> "Prediction"
                                SortOption.CONFIDENCE -> "Confidence"
                                else -> "A-Z"
                            }
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Clear",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun StockListItem(
    stock: StockItem,
    onClick: () -> Unit
) {
    val isPricePositive = stock.priceChange > 0
    val isPredictionPositive = stock.predictedChange > 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Section
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Stock Avatar
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = stock.symbol.take(2),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

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
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Current Price
                    Column {
                        Text(
                            text = "Current",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stock.currentPrice,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isPricePositive)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.errorContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPricePositive)
                                            Icons.Filled.ArrowUpward
                                        else
                                            Icons.Filled.ArrowDownward,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = if (isPricePositive)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "${if (isPricePositive) "+" else ""}${stock.priceChange}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPricePositive)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    // Prediction
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Prediction",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    text = "${stock.confidence}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isPredictionPositive)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPredictionPositive)
                                        Icons.Filled.ArrowUpward
                                    else
                                        Icons.Filled.ArrowDownward,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isPredictionPositive)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "${if (isPredictionPositive) "+" else ""}${stock.predictedChange}%",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPredictionPositive)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            // Right Section - Sparkline
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(100.dp)
            ) {
                IconButton(
                    onClick = { /* Add to watchlist */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.BookmarkBorder,
                        contentDescription = "Add to watchlist",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                MiniSparklinee(
                    data = stock.sparklineData,
                    isPositive = isPredictionPositive,
                    modifier = Modifier.size(70.dp, 40.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyState(searchQuery: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No stocks found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        if (searchQuery.isNotEmpty()) {
            Text(
                text = "Try searching with different keywords",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    selectedSector: FilterSector,
    onSectorSelected: (FilterSector) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Filter by Sector",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            FilterSector.values().forEach { sector ->
                FilterOption(
                    label = sector.name,
                    isSelected = sector == selectedSector,
                    onClick = { onSectorSelected(sector) }
                )

                if (sector != FilterSector.values().last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    selectedOption: SortOption,
    onOptionSelected: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Sort By",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            val sortOptions = mapOf(
                SortOption.ALPHABETICAL to "Alphabetical (A-Z)",
                SortOption.PRICE_HIGH_TO_LOW to "Price: High to Low",
                SortOption.PRICE_LOW_TO_HIGH to "Price: Low to High",
                SortOption.PREDICTED_CHANGE to "Predicted Change",
                SortOption.CONFIDENCE to "Confidence Score"
            )

            sortOptions.forEach { (option, label) ->
                FilterOption(
                    label = label,
                    isSelected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )

                if (option != sortOptions.keys.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FilterOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )

            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowUpward,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun MiniSparklinee(
    data: List<Float>,
    isPositive: Boolean,
    modifier: Modifier,
    width: Dp = 70.dp,
    height: Dp = 40.dp
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val path = androidx.compose.ui.graphics.Path()

        val max = data.maxOrNull() ?: 1f
        val min = data.minOrNull() ?: 0f
        val range = max - min

        data.forEachIndexed { index, value ->
            val x = (index.toFloat() / (data.size - 1)) * width.toPx()
            val y = height.toPx() - ((value - min) / range * height.toPx())

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.5.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )
    }
}