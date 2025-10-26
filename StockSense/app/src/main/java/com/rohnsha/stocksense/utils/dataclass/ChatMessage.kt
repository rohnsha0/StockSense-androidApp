package com.rohnsha.stocksense.utils.dataclass

import com.rohnsha.stocksense.utils.enums.MessageType

// Data Models
data class ChatMessage(
    val id: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: String,
    val messageType: MessageType = MessageType.TEXT,
    val stockData: StockSuggestion? = null
)