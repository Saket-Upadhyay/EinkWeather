package com.saketupadhyay.einkweather.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object WeatherRepository {
    private const val BASE_URL = "https://api.openweathermap.org/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }


    suspend fun getWeatherByZip(zipCode: String, apiKey: String): WeatherResponse {
        val response = api.getCoordsByZip(zipCode, apiKey)
        return api.getCurrentWeatherByCoords(response.lat, response.lon, apiKey)
    }
}
