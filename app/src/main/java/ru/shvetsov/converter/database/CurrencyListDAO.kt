package ru.shvetsov.converter.database

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Created by Alexander Shvetsov on 14.02.2020
 */
@Dao
interface CurrencyListDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(currencyList: List<CurrencyEntity>)

    @Update
    fun update(currencyList: List<CurrencyEntity>)

    @Delete
    fun delete(currency: CurrencyEntity)

    @Query("DELETE FROM currency_table")
    fun deleteAllCurrency()

    @Query("SELECT * FROM currency_table")
    fun getCurrencyList():LiveData<List<CurrencyEntity>>

    @Query("SELECT * FROM currency_table WHERE id =:currencyId")
    fun getCurrencyInfo(currencyId:String):LiveData<CurrencyEntity>

}