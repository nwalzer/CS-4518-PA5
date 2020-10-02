package com.example.basketballcounter.api

import retrofit2.Call
import retrofit2.http.GET


interface WeatherAPI {
    @GET("data/2.5/weather?q=Worcester,us&appid=f8604eb72a4b394e57fe226038fef554")
    fun fetchContents(): Call<WeatherResponse>

    @GET("data/2.5/weather?q=Worcester,us&appid=f8604eb72a4b394e57fe226038fef554")
    fun getWeatherString(): Call<String>
}