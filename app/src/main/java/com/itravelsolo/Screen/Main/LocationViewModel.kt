package com.itravelsolo.Screen.Main

import android.Manifest
import android.content.Context
import android.location.Geocoder
import androidx.annotation.RequiresPermission
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.itravelsolo.R
import com.itravelsolo.data.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Locale

data class LocationData (
    val country: String = "Unknown",
    val countryCode: String = "🌍",
    val city: String = "Unknown",
    val temperature: String = "--",
    val weatherCondition: String = "Unknown",
    val weatherIcon: Int = R.drawable.sun,
    val isLoading: Boolean = true,
    val isLocationPublic: Boolean = true,
    val error: String? = null
)

class LocationViewModel(private val context: Context): ViewModel() {
    private val repository = AuthRepository()
    private val _locationState = MutableStateFlow(LocationData())
    val locationState: StateFlow<LocationData> = _locationState

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val client = OkHttpClient()

    fun sendLocationUpdate() {
        viewModelScope.launch {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModelScope.launch {
                        try {
                            repository.updateUserLocation(
                                lat = location.latitude,
                                lon = location.longitude,
                                showLocation = _locationState.value.isLocationPublic
                            )
                        }
                        catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun toggleLocationPrivacy(isPublic: Boolean) {
        _locationState.value = _locationState.value.copy(isLocationPublic = isPublic)

        sendLocationUpdate()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun fetchLocationAndWeather() {
        _locationState.value = _locationState.value.copy(isLoading = true)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if(location != null) {
                viewModelScope.launch {
                    val (country, code, city) = getCountryAndCity(location.latitude, location.longitude)
                    val (temp, condition, icon) = fetchTemperature(location.latitude, location.longitude)

                    sendLocationUpdate()

                    _locationState.value = LocationData(
                        country = country,
                        countryCode = code,
                        city = city,
                        temperature = temp,
                        weatherCondition = condition,
                        weatherIcon = icon,
                        isLoading = false
                    )
                }
            }
            else _locationState.value = _locationState.value.copy(
                isLoading = false,
                error = "Location not found..!"
            )
        }.addOnFailureListener {
            _locationState.value = _locationState.value.copy(
                isLoading = false,
                error = it.message
            )
        }
    }

    private suspend fun getCountryAndCity(lat: Double, lon: Double): Triple<String, String, String> {
        return withContext(Dispatchers.IO) {
            try {
                val geoCoder = Geocoder(context, Locale.ENGLISH)
                val address = geoCoder.getFromLocation(lat, lon, 1)
                if(!address.isNullOrEmpty()) {
                    val address = address[0]
                    val country = address.countryName ?: "Unknown"
                    val code = countryCodeToEmoji(address.countryCode ?: "")
                    val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: "Unknown City"

                    return@withContext Triple(country.uppercase(), code, city)
                }
            } catch(e: Exception) {
                e.printStackTrace()
            }

            try {
                val url = "https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=$lat&longitude=$lon&localityLanguage=en"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val jsonData = response.body?.string()
                    val json = JSONObject(jsonData ?: "")

                    val country = json.optString("countryName", "Unknown")
                    val countryCode = json.optString("countryCode", "")
                    val city = json.optString("locality").ifEmpty {
                        json.optString("principalSubdivision", "Unknown City")
                    }

                    val emoji = countryCodeToEmoji(countryCode)
                    return@withContext Triple(country.uppercase(), emoji, city)
                }
            }
            catch(e: Exception) {
                e.printStackTrace()
            }
            return@withContext Triple("Unknown", "", "Unknown")
        }
    }

    private fun countryCodeToEmoji(countryCode: String): String {
        if(countryCode.length != 2) return "🌍"
        val firstLetter = Character.codePointAt(countryCode.toUpperCase(), 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode.toUpperCase(), 1) - 0x41 + 0x1F1E6
        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }

    private suspend fun fetchTemperature(lat: Double, lon: Double): Triple<String, String, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val jsonData = response.body?.string()
                    val json = JSONObject(jsonData ?: "")
                    val current = json.getJSONObject("current_weather")

                    val temp = current.getDouble("temperature")
                    val code = current.getInt("weathercode")

                    val (condition, icon) = interpretWeatherCode(code)
                    Triple("$temp °C", condition, icon)
                } else {
                    Triple("--", "Unknown", Icons.Default.Refresh)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Triple("--", "Error", Icons.Default.Refresh)
            } as Triple<String, String, Int>
        }
    }

    private fun interpretWeatherCode(code: Int): Pair<String, Int> {
        return when (code) {
            0 -> "Clear Sky" to R.drawable.sun
            1, 2, 3 -> "Cloudy" to R.drawable.cloudy
            45, 48 -> "Foggy" to R.drawable.foggy
            51, 53, 55 -> "Drizzle" to R.drawable.rain
            61, 63, 65 -> "Rainy" to R.drawable.rain
            71, 73, 75 -> "Snow" to R.drawable.snow
            95, 96, 99 -> "Storm" to R.drawable.storm
            else -> "Unknown" to Icons.Default.Refresh
        } as Pair<String, Int>
    }
}

class LocationViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LocationViewModel::class.java)) return LocationViewModel(context) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}