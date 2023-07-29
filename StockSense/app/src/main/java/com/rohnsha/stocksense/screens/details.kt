package com.rohnsha.stocksense.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.rohnsha.stocksense.R
import com.rohnsha.stocksense.StockSenseIcon
import com.rohnsha.stocksense.customToast
import com.rohnsha.stocksense.previousCloseDC
import com.rohnsha.stocksense.stocksenseicon.`Bar-chart`
import com.rohnsha.stocksense.stocksenseicon.`Bar-chart-2`
import com.rohnsha.stocksense.stocksenseicon.Cpu
import com.rohnsha.stocksense.stocksenseicon.List
import com.rohnsha.stocksense.ui.theme.DarkBlue
import com.rohnsha.stocksense.ui.theme.DashButton
import com.rohnsha.stocksense.view_models.stock_details_viewModel

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
    var timeData by remember {
        mutableStateOf(networkCallVM.updateTime())
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
        ltpSection(
            navController = navController,
            stockSymbol = stockSymbol,
            ltp= ltp,
            change= change,
            timeData= timeData,
            context= context
        )
        StocksInfoOptions()
    }
}

@Composable
fun ltpSection(
    navController: NavController,
    stockSymbol: String,
    ltp: String,
    change: String,
    timeData: String,
    context: Context
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

        LineChart(
            listOf(10f, 1f, 23f, 12f, 8f, 6f, 19f)
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
                text = "Last Updated at: $timeData",
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
}

@Composable
fun LineChart(
    dataPoints: List<Float>
) {

    Canvas(
        modifier = Modifier
            .padding(top = 77.dp)
            .fillMaxWidth()
            .height(165.dp)
    ){
        val canvasWidth= size.width
        val canvasHeight= size.height
        val dataSize= dataPoints.size

        val xStep= canvasWidth/ (dataSize-1)
        val yStep= canvasHeight/ dataPoints.maxOrNull()!!

        dataPoints.forEachIndexed { index, data ->
            val x= index* xStep
            val y= canvasHeight- data*yStep

            drawCircle(color = Color.White, radius = 9f, center = Offset(x, y))

            if (index>0){
                val prevX = (index - 1) * xStep
                val prevY = canvasHeight - dataPoints[index - 1] * yStep

                drawLine(
                    color = Color.White,
                    start = Offset(prevX, prevY),
                    end = Offset(x, y),
                    cap = StrokeCap.Square,
                    strokeWidth = 10f,
                )
            }
        }
    }

}

@Composable
fun StocksInfoOptions() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(top = 25.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OptionItems(icon = StockSenseIcon.Cpu, inpText = "Prediction")
            Spacer(modifier = Modifier.width(25.dp))
            OptionItems(icon = StockSenseIcon.`Bar-chart-2`, inpText = "Statistics")
            Spacer(modifier = Modifier.width(25.dp))
            OptionItems(icon = StockSenseIcon.List, inpText = "Watchlist", boxWidth = 75)
        }
        PreviousCloses(
            listOf(
                previousCloseDC(2.0, +5.0, "POSITIVE"),
                previousCloseDC(2.0, +5.0, "NEGATIVE"),
                previousCloseDC(2.0, +5.0, "POSITIVE"),
                previousCloseDC(2.0, +5.0, "NEGATIVE"),
                previousCloseDC(2.0, +5.0, "POSITIVE")
            )
        )
        Text(
            modifier = Modifier
                .padding(top = 6.dp),
            text = "See more informations",
            fontFamily = FontFamily(Font(R.font.titilliumweb_semi_bold)),
            )
        Spacer(modifier = Modifier.height(20.dp))

    }
}

@Composable
fun OptionItems(
    icon: ImageVector,
    inpText: String,
    boxWidth: Int= 100
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .height(49.dp)
                .width(boxWidth.dp)
                .shadow(2.dp, shape = RoundedCornerShape(16.dp))
                .background(DashButton),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "predictionIcon",
                modifier = Modifier
                    .height(24.dp)
                    .width(24.dp)
            )
        }
        Text(
            modifier = Modifier
                .padding(top = 6.dp),
            text = inpText,
            fontSize = 15.sp
        )
    }
}

@Composable
fun PreviousCloses(
    dataList: List<previousCloseDC>
) {
    Column(
        modifier = Modifier
            .padding(top = 30.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(start = 25.dp, bottom = 20.dp),
            text = "Previous Closes",
            fontFamily = FontFamily(Font(R.font.titilliumweb_semi_bold)),
            fontSize = 15.sp
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ){
            items(dataList){item ->
                PreviousClosePerItem(ltp = item.ltp, change = item.change, status = item.status)
            }
        }
    }
}

@Composable
fun PreviousClosePerItem(
    ltp: Double,
    change: Double,
    status: String
) {
    Box(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .padding(start = 25.dp, end = 25.dp)
            .shadow(2.dp, shape = RoundedCornerShape(16.dp))
            .background(DashButton),
    ){
        Row(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, bottom = 20.dp, end = 20.dp)
                    .height(24.dp)
                    .width(24.dp)
                    .background(
                        color = checkColor(change),
                        shape = CircleShape
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(.5f)
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    text = ltp.toString(),
                    fontFamily = FontFamily(Font(R.font.titilliumweb_semi_bold)),
                    fontSize = 15.sp
                    )
                Text(
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    text = change.toString(),
                    fontFamily = FontFamily(Font(R.font.titilliumweb_regular)),
                    fontSize = 15.sp
                    )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(.5f)
                    .padding(end = 18.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    text = status,
                    fontFamily = FontFamily(Font(R.font.titilliumweb_semi_bold)),
                    color = checkColor(change)
                    )
                Text(
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    text = change.toString(),
                    fontFamily = FontFamily(Font(R.font.titilliumweb_regular)),
                    fontSize = 15.sp
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(14.dp))
}

fun checkColor(change: Double): Color{
    if (change>0){
        return Color.Green
    } else if (change<0){
        return Color.Red
    }
    return Color.Gray
}

@Preview()
@Composable
fun PreviewDetails() {
    //Details(navController = rememberNavController(), "")
    StocksInfoOptions()
}