package ru.shvetsov.converter.models
import com.google.gson.annotations.SerializedName
import ru.shvetsov.converter.database.CurrencyEntity

data class ExchangeResponce(
    val currencies: Map<String, Double>
)
