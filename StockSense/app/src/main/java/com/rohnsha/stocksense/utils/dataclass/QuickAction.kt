package com.rohnsha.stocksense.utils.dataclass

import androidx.compose.ui.graphics.vector.ImageVector

data class QuickAction(
    val text: String,
    val icon: ImageVector,
    val prompt: String
)