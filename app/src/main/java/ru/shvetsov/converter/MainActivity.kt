package ru.shvetsov.converter

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import ru.shvetsov.converter.database.CurrencyEntity
import ru.shvetsov.converter.viewmodels.ViewModel
import java.lang.IllegalStateException
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var currencyList: List<CurrencyEntity>
    lateinit var spinnerAdapter: ArrayAdapter<String>
    var selectedItemFromIndex: Int = 0
    var selectedItemToIndex: Int = 0
    var timer: Timer = Timer()
    var isInit = false
    private val viewModel by lazy {
        ViewModelProvider(this).get(ViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null) {
            selectedItemFromIndex = savedInstanceState.getInt("selectedItemFromIndex")
            selectedItemToIndex = savedInstanceState.getInt("selectedItemToIndex")
        }
        subscribeObservers()
        when {
            isNetworkConnected(this) -> {
                viewModel.requestCurrencyFromAPI()
            }
            else ->
                Toast.makeText(this, "No internet found. Showing cached list in the view", Toast.LENGTH_LONG).show()
        }
    }


    private fun initViews() {
        sp_convert_to.adapter = spinnerAdapter
        sp_convert_from.adapter = spinnerAdapter
        sp_convert_from.setSelection(selectedItemFromIndex,false)
        sp_convert_to.setSelection(selectedItemToIndex,false)
        sp_convert_from.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("M_MainActivity", "convertFrom OnItemSelected. Position is: $position")
                if (selectedItemFromIndex!=position){
                    selectedItemFromIndex = position
                }
                if (isInit) {
                    Log.d("M_MainActivity", "convert from getQuery")
                    getQuery(parent, view, position)
                }
                else{
                    isInit = true
                }
            }
        }

        sp_convert_to.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("M_MainActivity", "convertTo OnItemSelected. Position is: $position")
                if (selectedItemToIndex!=position){
                    selectedItemToIndex = position
                }
                if (isInit) {
                    Log.d("M_MainActivity", "convert to getQuery")
                    getQuery(parent, view, position)
                }
                else{
                    isInit = true
                }
            }
        }


        et_convert_from.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (et_convert_from.hasFocus()) {
                    startTimerWithWork {
                        Log.d("M_MainActivity", "convert from start")
                        viewModel.requestExchangeFromAPI(
                            currencyList[selectedItemFromIndex].id,
                            currencyList[selectedItemToIndex].id
                        )
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        et_convert_to.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (et_convert_to.hasFocus()) {
                    startTimerWithWork {
                        Log.d("M_MainActivity", "convert to start")
                        viewModel.requestExchangeFromAPI(
                            currencyList[selectedItemToIndex].id,
                            currencyList[selectedItemFromIndex].id
                        )
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    Log.d("M_MainActivity","OnTextChanged")
                    timer.cancel()

            }

        })
    }

    private fun startTimerWithWork(block: () -> Unit) {
        timer.cancel()
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                block.invoke()
            }
        }, 1000)
    }

    private fun subscribeObservers() {
        viewModel.getCurrency()?.observe(this, Observer { listFromDatabase ->
            //Log.d("M_MainActivity","List of Currency size is : ${listFromDatabase.size}")
            currencyList = listFromDatabase
            spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyList.map { it.id })
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            initViews()
        })

        viewModel.getExchangeRate().observe(this, Observer {
            ll_result.visibility = View.VISIBLE
            //Log.d("M_MainActivity_TEST","$it")
            val string: String = if (it == null) {
                "TEST NULL"
            } else {
                getString(
                    R.string.main_activity_exchange_rate_hint,
                    it.exchangePair,
                    String.format("%1.2f", it.exchangeValue)
                )
            }
            tv_result.text = string
            when {
                it.exchangePair.startsWith(currencyList[selectedItemFromIndex].id) -> {
                    if (et_convert_from.text.isNotEmpty()) {
                        et_convert_to.setText(
                            String.format(
                                "%1.2f",
                                et_convert_from.text.toString().toFloat() * it.exchangeValue
                            )
                        )
                    }
                }
                it.exchangePair.startsWith(currencyList[selectedItemToIndex].id) -> {
                    if (et_convert_to.text.isNotEmpty()) {
                        et_convert_from.setText(
                            String.format("%1.2f", et_convert_to.text.toString().toFloat() * it.exchangeValue)
                        )
                    }
                }
            }
        })

    }

    private fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }


    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putInt("selectedItemFromIndex", selectedItemFromIndex)
        outState?.putInt("selectedItemToIndex", selectedItemToIndex)
    }

    private fun getQuery(parent: AdapterView<*>?, view: View?, position: Int) {
        if (view != null) {
            when (parent?.id) {
                R.id.sp_convert_from -> {
                    selectedItemFromIndex = position

                    if (et_convert_from.text.isNotEmpty()) {
                        Log.d("M_Main_OnItem_Sel", "convert_from_OnItemSelected")
                        viewModel.requestExchangeFromAPI(
                            currencyList[selectedItemFromIndex].id,
                            currencyList[selectedItemToIndex].id
                        )

                    }
                }
                R.id.sp_convert_to -> {
                    selectedItemToIndex = position
                    if (et_convert_from.text.isNotEmpty()) {
                        Log.d("M_Main_OnItem_Sel", "convert_to_OnItemSelected")
                        viewModel.requestExchangeFromAPI(
                            currencyList[selectedItemFromIndex].id,
                            currencyList[selectedItemToIndex].id
                        )
                    }

                }
            }
        }

    }
}
