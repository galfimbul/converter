package ru.shvetsov.converter.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.shvetsov.converter.App
import ru.shvetsov.converter.database.ConverterDatabase
import ru.shvetsov.converter.database.CurrencyEntity
import ru.shvetsov.converter.database.ExchangeRateEntity
import ru.shvetsov.converter.models.Currencies
import ru.shvetsov.converter.network.Retrofit

/**
 * Created by Alexander Shvetsov on 13.02.2020
 */
class Repository{
    private val retrofit = Retrofit.instance()
    private val database:ConverterDatabase = App.database!!
    private val currencyListDAO = database.currencyListDAO()
    private val exchangeRateDAO = database.exchangeRateDAO()
    private val currencyListFromDB = currencyListDAO.getCurrencyList()

    private val _exchangeRateValues = MutableLiveData<ExchangeRateEntity>()
    val exchangeRateValues: LiveData<ExchangeRateEntity>
    get() = _exchangeRateValues


    fun getCurrency():LiveData<List<CurrencyEntity>>{

        return currencyListFromDB
    }

    fun getCurrencyRequest(){
        if (!currencyListFromDB.value.isNullOrEmpty()){
            return
        }
        val call = retrofit.getCurrencyList(Retrofit.API_KEY)
        call.enqueue(object : Callback<Currencies> {
            override fun onFailure(call: Call<Currencies>, t: Throwable) {
                Log.d("M_MainActivity", "failed")
            }

            override fun onResponse(call: Call<Currencies>, response: Response<Currencies>) {
                when(response.code()) {
                    200 -> {
                        Thread(Runnable {
                            val responce = response.body()!!.currencies.values.toList()

                            currencyListDAO.deleteAllCurrency()
                            currencyListDAO.insert(responce)
                        }).start()
                    }
                }

            }

        })
    }


    fun getExchangeRate(idFrom:String,idTo:String){
        val call = retrofit.getExchangeResult("${idFrom}_$idTo","ultra",Retrofit.API_KEY)
        call.enqueue(object :Callback<Map<String,Float>>{
            override fun onFailure(call: Call<Map<String,Float>>, t: Throwable) {
                Log.d("M_Repository",t.localizedMessage)
            }

            override fun onResponse(call: Call<Map<String,Float>>, response: Response<Map<String,Float>>) {
                when(response.code()) {
                    200 -> {
                        Thread(Runnable {
                            val result = response.body()!!
                            Log.d("M_Repository",result.toString())
                            result.entries.forEach {
                                exchangeRateDAO.insert(ExchangeRateEntity(it.key,it.value))
                                _exchangeRateValues.postValue(exchangeRateDAO.getExchangeRate("${idFrom}_$idTo"))
                            }
                        }).start()
                    }
                }
            }

        })


    }
}