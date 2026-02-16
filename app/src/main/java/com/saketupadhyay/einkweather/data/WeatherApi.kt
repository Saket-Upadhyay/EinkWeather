package com.saketupadhyay.einkweather.data

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
         @Query("units") units: String = "metric"
    ): WeatherResponse

    @GET("geo/1.0/zip")
    suspend fun getCoordsByZip(
        @Query("zip") zipAndCountry: String,
        @Query("appid") apiKey: String
    ): ZipCodeResponse
}
