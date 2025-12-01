package com.itravelsolo.Screen.Main

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
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
    val isLoading: Boolean = true,
    val error: String? = null
)

class LocationViewModel(private val context: Context): ViewModel() {
    private val _locationState = MutableStateFlow(LocationData())
    val locationState: StateFlow<LocationData> = _locationState

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val client = OkHttpClient()

    fun fetchLocationAndWeather() {
        _locationState.value = _locationState.value.copy(isLoading = true)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if(location != null) {
                viewModelScope.launch {
                    val (country, code, city) = getCountryAndCity(location.latitude, location.longitude)
                    val temp = fetchTemperature(location.latitude, location.longitude)

                    _locationState.value = LocationData(
                        country = country,
                        countryCode = code,
                        city = city,
                        temperature = temp,
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
                address?.isNotEmpty()?.let {
                    if(!it) {
                        val address = address[0]
                        val country = address?.countryName ?: "Unknown"
                        val code = countryCodeToEmoji(address?.countryCode ?: "")
                        val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: "Unknown City"
                        Triple(country.toUpperCase(), code, city)
                    } else Triple("Unknown", "🌍", "Unknown")
                }
            } catch(e: Exception) {
                e.printStackTrace()
                Triple("Error", "🌍", "Unknown")
            } as Triple<String, String, String>

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

    private suspend fun fetchTemperature(lat: Double, lon: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val jsonData = response.body?.string()
                    val json = JSONObject(jsonData ?: "")
                    val temp = json.getJSONObject("current_weather").getDouble("temperature")
                    "$temp °C"
                } else {
                    "--"
                }
            } catch (e: Exception) {
                "--"
            }
        }
    }
}

class LocationViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LocationViewModel::class.java)) return LocationViewModel(context) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}