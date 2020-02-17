package ru.shvetsov.converter.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.shvetsov.converter.database.CurrencyEntity
import ru.shvetsov.converter.database.ExchangeRateEntity
import ru.shvetsov.converter.models.Currency
import ru.shvetsov.converter.repositories.Repository

/**
 * Created by Alexander Shvetsov on 13.02.2020
 */
class ViewModel :ViewModel(){
    private var repository:Repository? = Repository()

    fun getCurrency(): LiveData<List<CurrencyEntity>>? {
        return repository?.getCurrency()
    }

    fun getExchangeRate():LiveData<ExchangeRateEntity>{
        return repository!!.exchangeRateValues
    }

    fun requestCurrencyFromAPI(){
        repository?.getCurrencyRequest()
    }
    override fun onCleared() {
        super.onCleared()
        repository = null
    }

    fun requestExchangeFromAPI(idFrom:String,idTo:String) {
        repository!!.getExchangeRate(idFrom,idTo)
    }
}