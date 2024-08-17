package com.example.itravelsolo.appbody

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.itravelsolo.MainViewModel
import com.example.itravelsolo.R
import com.example.itravelsolo.auth.UserData

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userData: UserData?
) {
    val viewModel: MainViewModel = viewModel()
    val currentScreen = remember {
        viewModel.currentScreen.value
    }
    val controller: NavController = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val title = remember {
        mutableStateOf(currentScreen.title)
    }
    val profilePictureUrl = userData?.profilePictureUrl
    val bottomBar: @Composable () -> Unit = {
        if (currentScreen is Screens.BottomNavigation || currentScreen == Screens.BottomNavigation.Home) {
            NavigationBar(
                Modifier.wrapContentSize(),
            ) {
                screenInNavigation.forEach { item ->
                    val painter: Painter = if (item is Screens.BottomNavigation.Account && profilePictureUrl.isNullOrEmpty()) {
                        rememberAsyncImagePainter(model = profilePictureUrl)
                    }
                    else {
                        painterResource(id = item.icon)
                    }
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            controller.navigate(item.route)
                            title.value = item.title
                        },
                        icon = {
                            Icon(
                                painter = painter,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                            )
                        }
                    )
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "itravelSolo",
                        fontFamily = FontFamily(
                            Font(R.font.quicksand_semibold)
                        ),
                        fontSize = 40.sp,
                        color = Color.Black
                    )
                },
                colors = topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    AsyncImage(
                        model = userData?.profilePictureUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                },
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        },
        bottomBar = bottomBar
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 20.dp),
        ) {
            OutlinedButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(top = 30.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Where to now ?",
                            fontSize = 25.sp,
                            fontFamily = FontFamily(
                                Font(R.font.quicksand_semibold)
                            ),
                            color = Color.Black
                        )
                    }
                    VerticalDivider(
                        modifier = Modifier.height(30.dp)
                    )
                    OutlinedButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.padding(start = 10.dp),
                        contentPadding = PaddingValues(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = null,
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Now",
                                fontSize = 15.sp
                            )
                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "People Trip Post")
            }
        }
    }
}