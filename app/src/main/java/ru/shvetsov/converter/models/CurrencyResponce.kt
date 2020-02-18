package ru.shvetsov.converter.models
import com.google.gson.annotations.SerializedName
import ru.shvetsov.converter.database.CurrencyEntity

data class Currencies(
    @SerializedName("results")
    val currencies: Map<String, CurrencyEntity>
)
