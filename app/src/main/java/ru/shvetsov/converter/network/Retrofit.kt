package ru.shvetsov.converter.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Alexander Shvetsov on 13.02.2020
 */
object Retrofit {
    const val API_KEY = "49ff26951d1d4ae4d8b8"
    private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
    fun instance():CurrencyApi{
        return Retrofit.Builder()
            .baseUrl("https://free.currconv.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build().create(CurrencyApi::class.java)
    }
}