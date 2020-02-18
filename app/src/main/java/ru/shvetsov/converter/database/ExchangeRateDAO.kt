package ru.shvetsov.converter.database

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.shvetsov.converter.models.Currency

/**
 * Created by Alexander Shvetsov on 14.02.2020
 */
@Dao
interface ExchangeRateDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(exchangeRate: ExchangeRateEntity)

    @Update
    fun update(exchangeRate: ExchangeRateEntity)

    @Delete
    fun delete(exchangeRate: ExchangeRateEntity)

    @Query("DELETE FROM exchange_rate_table")
    fun deleteAllCurrency()

    @Query("SELECT * FROM exchange_rate_table")
    fun getExchangeRateList():LiveData<List<ExchangeRateEntity>>

    @Query("SELECT * FROM exchange_rate_table WHERE exchangePair =:exchangePair")
    fun getExchangeRate(exchangePair:String):ExchangeRateEntity

}