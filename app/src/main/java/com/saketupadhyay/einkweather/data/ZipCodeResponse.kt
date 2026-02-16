package com.saketupadhyay.einkweather.data

data class ZipCodeResponse(
    val zip: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
)

