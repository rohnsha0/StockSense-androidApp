package com.rohnsha.stocksense.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rohnsha.stocksense.BottomBarScreen
import com.rohnsha.stocksense.BottomNavGraph
import com.rohnsha.stocksense.customToast
import com.rohnsha.stocksense.stockDetail
import com.rohnsha.stocksense.ui.theme.Purple40
import com.rohnsha.stocksense.ui.theme.Purple80

var searchTexxt: MutableState<String> = mutableStateOf(value = "")

@SuppressLint("UnrememberedMutableState")
@Composable
fun SearchScrn() {
    var context= LocalContext.current
    val navController: NavHostController = rememberNavController()
    BottomNavGraph(navController = navController)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple40),
        contentAlignment = Alignment.Center
    ) {
        SearchBar(text = searchTexxt.value,
            onTextChange = { newValue ->
            UpdateValueText(newValue = newValue)
            },
            onCloseClicked = {
                             searchTexxt.value= ""
            },
            onSearchClicked = {
                Log.d("searchQuery", searchTexxt.value.uppercase()+".NS")
                //navController.navigate(BottomBarScreen.StockDetails.route)
                val intent= Intent(context, stockDetail::class.java)
                val symbol= "${searchTexxt.value.uppercase()}.NS"
                intent.putExtra("symbol", symbol)
                context.startActivity(intent)
                searchTexxt.value= ""
            })
    }
}

@Composable
fun SearchBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) ->Unit,
    modifier: Modifier= Modifier,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = Color.White,
        elevation = AppBarDefaults.TopAppBarElevation
    ) {
        TextField(
            value = text,
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = {
                onTextChange(it)
            },
            placeholder = {
                Text(
                    text = "Input your search Query",
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    color = Color.Black)
            },
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color.Gray)
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()){
                            onTextChange("")
                        } else {
                            onCloseClicked()
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear Icon",
                        tint = Color.Gray)
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            )
        )
    }
}

fun UpdateValueText(newValue: String) {
    searchTexxt.value= newValue
}

@Composable
@Preview(showBackground = true)
fun SearchUIPreview() {
    SearchScrn()
}