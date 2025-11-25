package com.example.proyecto_bmi.data.remote.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class MindicadorResponse(
    val serie: List<SerieData>
)

data class SerieData(
    val fecha: String,
    val valor: Double
)

interface EconomicApi {
    @GET("dolar")
    suspend fun getDolar(): MindicadorResponse
}

object EconomicRetrofit {
    val api: EconomicApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://mindicador.cl/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EconomicApi::class.java)
    }
}