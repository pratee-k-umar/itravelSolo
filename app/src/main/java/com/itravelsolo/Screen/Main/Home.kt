package com.itravelsolo.Screen.Main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.itravelsolo.R
import com.itravelsolo.Screen.Main.Profile.ProfileState
import com.itravelsolo.Screen.Main.Profile.ProfileViewModel
import com.itravelsolo.Screen.Main.Profile.ProfileViewModelFactory

val BgGreen = Color(0xFFE8F5E9)
val DarkGreen = Color(0xFF0F1905)
val AccentYellow = Color(0xFFDCE775)

data class HomeTheme (
    val quote: String,
    val imageRes: Int,
    val backgroundColor: Color,
    val contentColor: Color
)

@Composable
fun HomeLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = Color.Black,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun Home(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(LocalContext.current)
    ),
    locationViewModel: LocationViewModel = viewModel(
        factory = LocationViewModelFactory(LocalContext.current)
    )
) {
    val profileState by profileViewModel.profileState.collectAsState()
    val locationState by locationViewModel.locationState.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        locationViewModel.fetchLocationAndWeather()
    }
    LaunchedEffect(Unit) {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) locationViewModel.fetchLocationAndWeather()
        else launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    val isProfileLoading = profileState is ProfileState.Loading
    val isLocationReady = !locationState.isLoading || locationState.error != null
    val isLoading = isProfileLoading || !isLocationReady

    if(isLoading) HomeLoading()
    else {
        val userName = when(val state = profileState) {
            is ProfileState.Success -> state.user?.firstName ?: "Traveler"
            else -> "Traveler"
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgGreen)
        ) {
            Column(
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                ),
            ) {
                Spacer(modifier = Modifier.height(54.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hi, $userName \uD83D\uDC4B",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.riveruta_medium))
                        )
                    }
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WeatherIcon(
                            iconResId = locationState.weatherIcon,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Weather",
                                fontSize = 15.sp,
                                color = Color.Black,
                                fontFamily = FontFamily(Font(R.font.riveruta_medium))
                            )
                            Text(
                                text = locationState.temperature,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontFamily = FontFamily(Font(R.font.riveruta_medium))
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = locationState.countryCode,
                                fontSize = 16.sp,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = locationState.country,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray,
                                fontFamily = FontFamily(Font(R.font.riveruta_medium))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = locationState.city,
                                fontSize = 20.sp,
                                color = Color.Gray,
                                fontFamily = FontFamily(Font(R.font.riveruta_medium))
                            )
                        }
                        Text(
                            text = "Nature\nPower",
                            fontSize = 65.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.riveruta_medium)),
                            lineHeight = 60.sp
                        )
                    }
                    Card(
                        shape = RoundedCornerShape(50.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .height(100.dp)
                            .width(60.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = Color.Black,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
//            CategoryChip(icon = Icons.Default.Hiking, text = "Hiking", isActive = true)
//            CategoryChip(icon = Icons.Default.Kayaking, text = "Kayaking", isActive = false)
//            CategoryChip(icon = Icons.Default.PedalBike, text = "Biking", isActive = false)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(400.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                ) {
//            Image(
//                painter = painterResource(id = R.drawable.background),
//                contentDescription = "Forest",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.fillMaxSize()
//            )

                    // 2. Gradient Overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    ),
                                    startY = 300f
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "The Sounds of Nature",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "A real adventure where nature reveals its grandeur and beauty in its purest form.",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
//                    TripStat(icon = Icons.Default.CalendarMonth, text = "7 days")
//                    Spacer(modifier = Modifier.width(16.dp))
//                    TripStat(icon = Icons.Default.Route, text = "10 km")
//                    Spacer(modifier = Modifier.width(16.dp))
//                    TripStat(icon = Icons.Default.Group, text = "8/10")
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 32.dp, vertical = 16.dp)
                                .fillMaxWidth(0.7f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Start Trip", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(icon: ImageVector, text: String, isActive: Boolean) {
    val containerColor = if (isActive) DarkGreen else Color.White
    val contentColor = if (isActive) Color.White else DarkGreen

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun TripStat(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WeatherIcon(
    iconResId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context).components {
        if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
        else add(GifDecoder.Factory())
    }.build()

    AsyncImage(
        model = iconResId,
        imageLoader = imageLoader,
        contentDescription = "Weather Animation",
        modifier = modifier
    )
}