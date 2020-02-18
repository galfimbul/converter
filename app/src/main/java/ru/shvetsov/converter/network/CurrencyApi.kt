package ru.shvetsov.converter.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.shvetsov.converter.database.ExchangeRateEntity
import ru.shvetsov.converter.models.Currencies
import ru.shvetsov.converter.models.ExchangeResponce

/**
 * Created by Alexander Shvetsov on 13.02.2020
 */
interface CurrencyApi {
    @GET("/api/v7/currencies")
    fun getCurrencyList(@Query("apiKey") apiKey:String):Call<Currencies>

    @GET("/api/v7/convert")
    fun getExchangeResult(
        @Query("q") exchangeValues:String,
        @Query("compact") compact:String,
        @Query("apiKey") apiKey:String
    ):Call<Map<String,Float>>
}