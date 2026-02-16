# EinkWeather

<p align="center">
  <img alt="EinkWeather.png" src="EinkWeather.png"/>
<br>
  A lightweight weather app optimized for E-ink Android tablets.
</p>



Tested on Boox GO 10.3 using Android 12. It fetches data from OpenWeatherMap and displays it in a clean, minimalistic UI designed for low refresh rates.

## Setup

1.  **Clone**:
    ```bash
    git clone https://github.com/Saket-Upadhyay/EinkWeather.git
    ```

2.  **Configure**:
    *   Get a key from [OpenWeatherMap](https://openweathermap.org/api).
    *   Create a `local.properties` file in the project root if it doesn't exist.
    *   Add your API key: `OPENWEATHER_API_KEY=your_api_key_here`

3.  **Run**: Build via Android Studio.

## Tech Stack

*   Kotlin & Jetpack Compose
*   Retrofit + OkHttp
*   Coil (SVG loading)
*   Google Downloadable Fonts

## License

[MIT License](LICENSE)
