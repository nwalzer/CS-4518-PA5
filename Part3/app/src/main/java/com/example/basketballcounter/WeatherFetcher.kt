package com.example.basketballcounter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.basketballcounter.api.WeatherAPI
import com.example.basketballcounter.api.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "BasketballCounter"

class WeatherFetcher {

    private val weatherAPI: WeatherAPI
    private val url: String = "https://api.openweathermap.org/"

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherAPI = retrofit.create(WeatherAPI::class.java)
    }

    fun fetchContents(): LiveData<WeatherResponse> {
        val responseLiveData: MutableLiveData<WeatherResponse> = MutableLiveData()
        val weatherRequest: Call<WeatherResponse> = weatherAPI.fetchContents()

        weatherRequest.enqueue(object : Callback<WeatherResponse> {

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
            }

            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                val weatherResponse: WeatherResponse? = response.body()
                responseLiveData.value = weatherResponse
            }
        })

        return responseLiveData
    }
}