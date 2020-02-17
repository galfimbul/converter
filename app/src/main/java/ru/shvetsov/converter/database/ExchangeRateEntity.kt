package ru.shvetsov.converter.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Alexander Shvetsov on 14.02.2020
 */
@Entity(tableName = "exchange_rate_table")
data class ExchangeRateEntity (
    @PrimaryKey
    val exchangePair:String,
    val exchangeValue:Float
)