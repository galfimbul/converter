package ru.shvetsov.converter

import android.app.Application
import ru.shvetsov.converter.database.ConverterDatabase

/**
 * Created by Alexander Shvetsov on 14.02.2020
 */
class App :Application(){
    override fun onCreate() {
        super.onCreate()
        database = ConverterDatabase.getDatabase(applicationContext)

    }
    companion object {
        var database: ConverterDatabase? = null
    }
}