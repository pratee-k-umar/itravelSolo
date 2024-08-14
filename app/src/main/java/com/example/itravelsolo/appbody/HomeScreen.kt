package com.example.itravelsolo.appbody

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.itravelsolo.MainViewModel
import com.example.itravelsolo.R
import com.example.itravelsolo.auth.UserData

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
    val bottomBar: @Composable () -> Unit = {
        if (currentScreen is Screens.BottomNavigation || currentScreen == Screens.BottomNavigation.Home) {
            NavigationBar(
                Modifier.wrapContentSize()
            ) {
                screenInNavigation.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            controller.navigate(item.route)
                            title.value = item.title
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(text = item.title)
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
                        fontFamily = FontFamily.Cursive,
                        fontSize = 30.sp
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
                }
            )
        },
        bottomBar = bottomBar
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            Text(
                modifier = Modifier.padding(top = 150.dp),
                text = "Welcome",
                fontSize = 40.sp,
                fontFamily = FontFamily(
                    Font(R.font.quicksand)
                )
            )
            Text(
                text = "${userData?.username},",
                fontSize = 60.sp,
                fontFamily = FontFamily(
                    Font(
                        R.font.quicksand_semibold,
                    )
                )
            )
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = "Planning a new",
                fontSize = 40.sp,
                fontFamily = FontFamily(
                    Font(
                        R.font.quicksand,
                    )
                )
            )
            Text(
                text = "Trip..?",
                fontSize = 40.sp,
                fontFamily = FontFamily(
                    Font(
                        R.font.quicksand,
                    )
                )
            )
            OutlinedButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 160.dp).padding(top = 100.dp)
            ) {
                Text(text = "Plan")
            }
        }
    }
}