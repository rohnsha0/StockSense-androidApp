package com.rohnsha.stocksense.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.StockDataResponse
import com.rohnsha.stocksense.customToast
import com.rohnsha.stocksense.ui.theme.DarkBlue
import com.rohnsha.stocksense.ui.theme.Pink40
import com.rohnsha.stocksense.ui.theme.Purple40
import com.rohnsha.stocksense.ui.theme.Purple80
import com.rohnsha.stocksense.view_models.stock_details_viewModel
import okhttp3.OkHttpClient
import okhttp3.Request

@Composable
fun Details(
    navController: NavController,
    stockSymbol: String
) {
    val context= LocalContext.current
    val networkCallVM= viewModel<stock_details_viewModel>()

    val (ltp, previousClose)= networkCallVM.networkCall(stockSymbol)
    var change by remember {
        mutableStateOf("")
    }
    if (ltp.isNotBlank() || previousClose.isNotBlank()){
        change= String.format("%.2f", (ltp.toDouble()-previousClose.toDouble()))
    }
    Log.d("dataState", ltp.isBlank().toString())
    Log.d("data", ltp)
    Log.d("data", previousClose)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .verticalScroll(
                enabled = true,
                state = rememberScrollState()
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBlue),
            verticalArrangement = Arrangement.Top

            ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .height(40.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    modifier = Modifier
                        .padding(start = 17.dp)
                        .fillMaxHeight()
                        .clickable {
                            navController.popBackStack()
                        },
                    tint = Color.White
                )
                Text(
                    text = stockSymbol,
                    modifier = Modifier
                        .fillMaxHeight(),
                    fontSize = 26.sp,
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
                        .alpha(0F)
                        .clickable {
                            customToast
                                .makeText(context = context, "added to list", 1)
                                .show()
                        },
                    tint = Color.White
                )
            }
            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                text = ltp,
                fontSize = 32.sp,
                fontFamily = FontFamily(Font(R.font.titillium_web_bold)),
                textAlign = TextAlign.Center,
                color = Color.White,
            )
            Text(
                text = change,
                modifier = Modifier
                    .padding(top = 0.dp, bottom = 0.dp)
                    .fillMaxWidth(),
                fontSize = 15.sp,
                fontWeight = FontWeight(400),
                fontFamily = FontFamily(Font(R.font.titilliumweb_regular)),
                textAlign = TextAlign.Center,
                color = Color.White
            )

            Spacer(
                modifier = Modifier
                    .padding(top = 77.dp)
                    .fillMaxWidth()
                    .height(165.dp)
                    .background(Color.Black.copy(.10f))
            )

            Row(
                modifier = Modifier
                    .padding(top = 92.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(size = 4.dp))
                        .background(Color.White.copy(alpha = .10f))
                        .padding(horizontal = 9.dp, vertical = 3.dp),
                    text = "Last Updated at: ",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.titilliumweb_semi_bold)),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(size = 4.dp))
                        .background(Color.White.copy(alpha = .10f))
                        .padding(horizontal = 9.dp, vertical = 3.dp),
                    text = "7D Trendline",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.titilliumweb_regular)),
                    fontSize = 14.sp
                )
            }
        }
        Column(
            modifier = Modifier
                .height(750.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .fillMaxWidth()
                .background(Color.White),
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDetails() {
    Details(navController = rememberNavController(), "")
}