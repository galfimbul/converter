package ru.shvetsov.converter.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Alexander Shvetsov on 14.02.2020
 */
@Entity(tableName = "currency_table")
data class CurrencyEntity (
    @PrimaryKey
    val id:String,
    val currencyName:String
)