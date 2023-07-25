package com.rohnsha.stocksense.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohnsha.stocksense.HomeScreen
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.customToast
import com.rohnsha.stocksense.ui.theme.Purple40
import com.rohnsha.stocksense.ui.theme.Purple80
import com.rohnsha.stocksense.welcome_screen

@Composable
fun Details(
    stockSymbol: String,
    ltp: String,
    change: String,
    onClck: () -> Unit
) {
    val context= LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple80)
            .verticalScroll(
                enabled = true,
                state = rememberScrollState()
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(445.dp)
                .background(Purple40),
            verticalArrangement = Arrangement.Top

            ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .height(32.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    modifier = Modifier
                        .padding(start = 17.dp)
                        .fillMaxHeight()
                        .alpha(0F),
                    tint = Color.White
                )
                Text(
                    text = stockSymbol,
                    modifier = Modifier
                        .fillMaxHeight(),
                    fontSize = 26.sp,
                    fontWeight = FontWeight(600),
                    fontFamily = FontFamily(Font(R.font.titilliumweb_semi_bold)),
                    textAlign = TextAlign.Center,
                    color = Color.White

                )
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Back Button",
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 17.dp)
                        .clickable {
                            onClck

                            customToast.makeText(context = context, "added to list", 1).show()
                        },
                    tint = Color.White
                )
            }
        }
        Column(
            modifier = Modifier
                .height(750.dp)
                .fillMaxWidth()
                .background(Color.Black)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDetails() {
    Details(stockSymbol = "ITC", ltp = "0", change = "0", {})
}