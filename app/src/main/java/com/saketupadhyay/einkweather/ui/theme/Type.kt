package com.saketupadhyay.einkweather.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.saketupadhyay.einkweather.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val displayFontName = GoogleFont("Google Sans Display")
val bodyFontName = GoogleFont("Roboto")

val displayFontFamily = FontFamily(
    Font(googleFont = displayFontName, fontProvider = provider)
)

val bodyFontFamily = FontFamily(
    Font(googleFont = bodyFontName, fontProvider = provider),
    Font(googleFont = bodyFontName, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = bodyFontName, fontProvider = provider, weight = FontWeight.Medium)
)


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    displayLarge = TextStyle(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 80.sp
    ),
    displayMedium = TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 60.sp
    ),
    displaySmall = TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    )
)