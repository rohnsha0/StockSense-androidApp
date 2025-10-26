package com.rohnsha.stocksense.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.rohnsha.stocksense.utils.dataclass.StockDetail
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

/**
 * Generates and shares a professional stock card image using Canvas
 */
object StockShareImageGenerator {

    private const val IMAGE_WIDTH = 1080
    private const val IMAGE_HEIGHT = 1920
    private const val PADDING = 48f

    fun shareStockCard(
        context: Context,
        stockDetail: StockDetail
    ) {
        try {
            // Generate bitmap
            val bitmap = createStockCardBitmap(stockDetail)

            // Save bitmap to cache
            val imageFile = saveBitmapToCache(context, bitmap, stockDetail.symbol)

            // Share the image
            shareImage(context, imageFile, stockDetail)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to text-only share
            shareTextOnly(context, stockDetail)
        }
    }

    private fun createStockCardBitmap(stockDetail: StockDetail): Bitmap {
        val isPositive = stockDetail.changePercent > 0
        val primaryColor = if (isPositive) Color.rgb(76, 175, 80) else Color.rgb(244, 67, 54)

        val bitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background gradient
        drawGradientBackground(canvas, primaryColor)

        // Decorative circles
        drawDecorativeCircles(canvas, primaryColor)

        // Content
        var yPosition = PADDING + 100f

        // App branding
        yPosition = drawAppBranding(canvas, yPosition, primaryColor)

        // Stock symbol and name
        yPosition = drawStockHeader(canvas, yPosition, stockDetail)

        // Main price card
        yPosition = drawPriceCard(canvas, yPosition, stockDetail, primaryColor, isPositive)

        // Footer
        drawFooter(canvas, primaryColor)

        return bitmap
    }

    private fun drawGradientBackground(canvas: Canvas, primaryColor: Int) {
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, IMAGE_HEIGHT.toFloat(),
                intArrayOf(
                    Color.rgb(10, 14, 39),
                    Color.rgb(26, 31, 58),
                    Color.rgb(10, 14, 39)
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, IMAGE_WIDTH.toFloat(), IMAGE_HEIGHT.toFloat(), bgPaint)
    }

    private fun drawDecorativeCircles(canvas: Canvas, primaryColor: Int) {
        val circlePaint = Paint().apply {
            color = ColorUtils.setAlphaComponent(primaryColor, 13)
            style = Paint.Style.FILL
        }

        // Top left circle
        canvas.drawCircle(-100f, -100f, 300f, circlePaint)

        // Bottom right circle
        circlePaint.alpha = 20
        canvas.drawCircle(IMAGE_WIDTH + 150f, IMAGE_HEIGHT + 150f, 400f, circlePaint)
    }

    private fun drawAppBranding(canvas: Canvas, startY: Float, primaryColor: Int): Float {
        var y = startY

        // App icon background
        val iconBgPaint = Paint().apply {
            color = ColorUtils.setAlphaComponent(primaryColor, 51)
            style = Paint.Style.FILL
        }
        val iconRect = RectF(PADDING, y, PADDING + 120f, y + 120f)
        canvas.drawRoundRect(iconRect, 32f, 32f, iconBgPaint)

        // App icon (chart symbol)
        val iconPaint = Paint().apply {
            color = primaryColor
            strokeWidth = 8f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        val path = Path().apply {
            moveTo(PADDING + 30f, y + 80f)
            lineTo(PADDING + 50f, y + 60f)
            lineTo(PADDING + 70f, y + 70f)
            lineTo(PADDING + 90f, y + 40f)
        }
        canvas.drawPath(path, iconPaint)

        // App name
        val appNamePaint = Paint().apply {
            color = Color.WHITE
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("StockSense", PADDING + 140f, y + 50f, appNamePaint)

        // App subtitle
        val subtitlePaint = Paint().apply {
            color = ColorUtils.setAlphaComponent(Color.WHITE, 179)
            textSize = 28f
            isAntiAlias = true
        }
        canvas.drawText("AI-Powered Stock Analysis", PADDING + 140f, y + 90f, subtitlePaint)

        return y + 200f
    }

    private fun drawStockHeader(canvas: Canvas, startY: Float, stockDetail: StockDetail): Float {
        var y = startY

        // Stock symbol
        val symbolPaint = Paint().apply {
            color = Color.WHITE
            textSize = 140f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(stockDetail.symbol, PADDING, y, symbolPaint)
        y += 40f

        // Stock name
        val namePaint = Paint().apply {
            color = ColorUtils.setAlphaComponent(Color.WHITE, 204)
            textSize = 38f
            isAntiAlias = true
        }
        canvas.drawText(stockDetail.name, PADDING, y, namePaint)

        return y + 100f
    }

    private fun drawPriceCard(
        canvas: Canvas,
        startY: Float,
        stockDetail: StockDetail,
        primaryColor: Int,
        isPositive: Boolean
    ): Float {
        val cardLeft = PADDING
        val cardTop = startY
        val cardRight = IMAGE_WIDTH - PADDING
        val cardBottom = cardTop + 800f

        // Card background
        val cardPaint = Paint().apply {
            color = ColorUtils.setAlphaComponent(Color.rgb(26, 31, 58), 204)
            style = Paint.Style.FILL
        }
        val cardRect = RectF(cardLeft, cardTop, cardRight, cardBottom)
        canvas.drawRoundRect(cardRect, 64f, 64f, cardPaint)

        var y = cardTop + 80f

        // "Current Price" label
        val labelPaint = Paint().apply {
            color = ColorUtils.setAlphaComponent(Color.WHITE, 153)
            textSize = 32f
            isAntiAlias = true
        }
        canvas.drawText("Current Price", cardLeft + 60f, y, labelPaint)
        y += 80f

        // Price
        val pricePaint = Paint().apply {
            color = Color.WHITE
            textSize = 100f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(stockDetail.currentPrice, cardLeft + 60f, y, pricePaint)

        // Change badge
        val badgeWidth = 280f
        val badgeHeight = 100f
        val badgeLeft = cardRight - badgeWidth - 60f
        val badgeTop = y - 80f
        val badgeRect = RectF(badgeLeft, badgeTop, badgeLeft + badgeWidth, badgeTop + badgeHeight)

        val badgePaint = Paint().apply {
            color = primaryColor
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(badgeRect, 40f, 40f, badgePaint)

        // Change percentage
        val changePaint = Paint().apply {
            color = Color.WHITE
            textSize = 44f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        val changeText = "${if (isPositive) "+" else ""}${stockDetail.changePercent}%"
        canvas.drawText(changeText, badgeLeft + badgeWidth / 2, badgeTop + 65f, changePaint)

        y += 120f

        // Stats grid
        val statsY = y
        drawStatItem(canvas, "Day High", stockDetail.dayHigh, cardLeft + 60f, statsY)
        drawStatItem(canvas, "Day Low", stockDetail.dayLow, cardLeft + 510f, statsY)

        drawStatItem(canvas, "Volume", stockDetail.volume, cardLeft + 60f, statsY + 180f)
        drawStatItem(canvas, "Market Cap", stockDetail.marketCap, cardLeft + 510f, statsY + 180f)

        return cardBottom + 80f
    }

    private fun drawStatItem(canvas: Canvas, label: String, value: String, x: Float, y: Float) {
        // Stat background
        val statBgPaint = Paint().apply {
            color = ColorUtils.setAlphaComponent(Color.WHITE, 13)
            style = Paint.Style.FILL
        }
        val statRect = RectF(x, y, x + 420f, y + 140f)
        canvas.drawRoundRect(statRect, 32f, 32f, statBgPaint)

        // Label
        val labelPaint = Paint().apply {
            color = ColorUtils.setAlphaComponent(Color.WHITE, 153)
            textSize = 26f
            isAntiAlias = true
        }
        canvas.drawText(label, x + 40f, y + 50f, labelPaint)

        // Value
        val valuePaint = Paint().apply {
            color = Color.WHITE
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(value, x + 40f, y + 100f, valuePaint)
    }

    private fun drawFooter(canvas: Canvas, primaryColor: Int) {
        val y = IMAGE_HEIGHT - 250f

        // AI badge
        val badgeWidth = 700f
        val badgeHeight = 100f
        val badgeLeft = (IMAGE_WIDTH - badgeWidth) / 2
        val badgeRect = RectF(badgeLeft, y, badgeLeft + badgeWidth, y + badgeHeight)

        val badgePaint = Paint().apply {
            color = ColorUtils.setAlphaComponent(primaryColor, 38)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(badgeRect, 50f, 50f, badgePaint)

        val badgeTextPaint = Paint().apply {
            color = primaryColor
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "✨ AI-Powered Predictions Available",
            IMAGE_WIDTH / 2f,
            y + 65f,
            badgeTextPaint
        )

        // Timestamp
        val currentTime =
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val timestampPaint = Paint().apply {
            color = ColorUtils.setAlphaComponent(Color.WHITE, 128)
            textSize = 26f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Generated on $currentTime", IMAGE_WIDTH / 2f, y + 160f, timestampPaint)
    }

    private fun saveBitmapToCache(
        context: Context,
        bitmap: Bitmap,
        stockSymbol: String
    ): File {
        val cacheDir = File(context.cacheDir, "stock_shares")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(cacheDir, "stock_${stockSymbol}_$timestamp.png")

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        return file
    }

    private fun shareImage(
        context: Context,
        imageFile: File,
        stockDetail: StockDetail
    ) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )

        val shareText = buildShareText(stockDetail)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(shareIntent, "Share ${stockDetail.symbol} via")
        )
    }

    private fun shareTextOnly(
        context: Context,
        stockDetail: StockDetail
    ) {
        val shareText = buildShareText(stockDetail)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        context.startActivity(
            Intent.createChooser(shareIntent, "Share ${stockDetail.symbol} via")
        )
    }

    private fun buildShareText(stockDetail: StockDetail): String {
        val isPositive = stockDetail.changePercent > 0
        val direction = if (isPositive) "up" else "down"

        return """
            📈 ${stockDetail.symbol} - ${stockDetail.name}
            
            Current Price: ${stockDetail.currentPrice}
            Change: ${stockDetail.changeAmount} (${if (isPositive) "+" else ""}${stockDetail.changePercent}%)
            
            ${stockDetail.symbol} is $direction ${abs(stockDetail.changePercent)}% today!
            
            Track live stock prices and AI predictions with StockSense 🚀
        """.trimIndent()
    }
}

object ColorUtils {
    fun setAlphaComponent(color: Int, alpha: Int): Int {
        return (alpha shl 24) or (color and 0x00ffffff)
    }
}