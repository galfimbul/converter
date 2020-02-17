package ru.shvetsov.converter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Created by Alexander Shvetsov on 14.02.2020
 */
@Database(entities = [CurrencyEntity::class,ExchangeRateEntity::class],version = 1,exportSchema = false)
abstract class ConverterDatabase :RoomDatabase(){

    abstract fun currencyListDAO():CurrencyListDAO
    abstract fun exchangeRateDAO():ExchangeRateDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ConverterDatabase? = null

        fun getDatabase(context: Context): ConverterDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConverterDatabase::class.java,
                    "converter_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}