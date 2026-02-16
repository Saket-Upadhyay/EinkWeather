package com.saketupadhyay.einkweather

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.saketupadhyay.einkweather.data.WeatherResponse
import com.saketupadhyay.einkweather.ui.theme.EinkWeatherTheme
import com.saketupadhyay.einkweather.ui.theme.displayFontFamily
import com.saketupadhyay.einkweather.ui.theme.bodyFontFamily
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.content.edit
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EinkWeatherTheme(
                darkTheme = false,
                dynamicColor = false
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(modifier: Modifier = Modifier, viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("eink_weather_prefs", Context.MODE_PRIVATE)
    }

    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Default values
    var zipCode by remember {
        mutableStateOf(sharedPreferences.getString("last_zip_code", "22903") ?: "22903")
    }
    var useV2Icons by remember {
        mutableStateOf(sharedPreferences.getBoolean("use_v2_icons", false))
    }
    val apiKey = BuildConfig.OPENWEATHER_API_KEY

    var showAboutDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    LaunchedEffect(zipCode, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                viewModel.fetchWeatherByZip(zipCode, apiKey)
                delay(30 * 60 * 1000L) // 30 minutes
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Settings",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = zipCode,
                        onValueChange = { zipCode = it },
                        label = { Text("Zip Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Use V2 Icons", modifier = Modifier.weight(1f))
                        Switch(
                            checked = useV2Icons,
                            onCheckedChange = {
                                useV2Icons = it
                                sharedPreferences.edit { putBoolean("use_v2_icons", it) }
                            }
                        )
                    }

                    Button(
                        onClick = {
                            sharedPreferences.edit { putString("last_zip_code", zipCode) }
                            viewModel.fetchWeatherByZip(zipCode, apiKey)
                            scope.launch { drawerState.close() }
                        },

                        modifier = Modifier.padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Update Weather")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(onClick = { showAboutDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "About", tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    ) {
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text("About EinkWeather") },
                text = {
                    Column {
                        Text("Version: " + stringResource(R.string.app_version))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Author: Saket Upadhyay")
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("https://github.com/saketupadhyay/EinkWeather")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text("Eink Weather") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.fetchWeatherByZip(zipCode, apiKey) },
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                when (val state = uiState) {
                    is WeatherUiState.Loading -> {
                        Text("Loading...", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    is WeatherUiState.Success -> {
                        WeatherDisplay(weatherResponse = state.weather, useV2Icons = useV2Icons)
                    }

                    is WeatherUiState.Error -> {
                        Text(
                            text = "Error: ${state.message}",
                            color = Color.Black, // Keep it black for e-ink readability
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherDisplay(weatherResponse: WeatherResponse, useV2Icons: Boolean) {
    val weatherIconPath = if (useV2Icons) {
        getWeatherIconPathv2(weatherResponse.weather.firstOrNull()?.icon ?: "")
    } else {
        getWeatherIconPath(weatherResponse.weather.firstOrNull()?.icon ?: "")
    }
    val assetFolder = if (useV2Icons) "weatherIconsv2" else "weatherIcons"
    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/$assetFolder/$weatherIconPath")
                .build(),
            imageLoader = imageLoader,
            contentDescription = null,
            modifier = Modifier.size(300.dp),
//            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "${weatherResponse.main.temp.roundToInt()}°C",
            fontSize = 300.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = displayFontFamily
        )
        Text(
            text = weatherResponse.weather.firstOrNull()?.description?.uppercase() ?: "",
            fontSize = 60.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = bodyFontFamily
        )
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Humidity: ${weatherResponse.main.humidity}%",
            fontSize = 40.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = bodyFontFamily
        )
        Text(
            text = "Wind: ${weatherResponse.wind.speed} m/s",
            fontSize = 40.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = bodyFontFamily
        )
        Text(
            text = weatherResponse.name,
            fontSize = 40.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(top = 8.dp),
            fontFamily = bodyFontFamily
        )
    }
}

fun getWeatherIconPath(iconCode: String): String {
    return when (iconCode) {
        "01d" -> "clear_day.svg"
        "01n" -> "clear_night.svg"
        "02d" -> "partly_cloudy_day.svg"
        "02n" -> "partly_cloudy_night.svg"
        "03d", "03n" -> "cloudy.svg"
        "04d", "04n" -> "mostly_cloudy_day.svg"
        "09d", "09n" -> "showers_rain.svg"
        "10d" -> "sunny_with_rain_light.svg"
        "10n" -> "rain_with_cloudy_light.svg"
        "11d", "11n" -> "isolated_thunderstorms.svg"
        "13d", "13n" -> "heavy_snow.svg"
        "50d", "50n" -> "haze_fog_dust_smoke.svg"
        else -> "unknown.svg"
    }
}

fun getWeatherIconPathv2(iconCode: String): String {
    return when (iconCode) {
        "01d" -> "Sun.svg"
        "01n" -> "Moon.svg"
        "02d" -> "Cloud-Sun.svg"
        "02n" -> "Cloud-Moon.svg"
        "03d", "03n" -> "Cloud.svg"
        "04d", "04n" -> "Cloud.svg"
        "09d", "09n" -> "Cloud-Drizzle.svg"
        "10d" -> "Cloud-Rain-Sun.svg"
        "10n" -> "Cloud-Rain-Moon.svg"
        "11d", "11n" -> "Cloud-Lightning.svg"
        "13d", "13n" -> "Cloud-Snow.svg"
        "50d", "50n" -> "Cloud-Fog.svg"
        else -> "Cloud.svg"
    }
}
