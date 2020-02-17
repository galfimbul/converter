package ru.shvetsov.converter

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
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
import java.util.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener{
    lateinit var currencyList:List<CurrencyEntity>
    lateinit var spinnerAdapter:ArrayAdapter<String>
    lateinit var selectedItemFrom:CurrencyEntity
    lateinit var selectedItemTo:CurrencyEntity
    var timer:Timer? = null
    private val viewModel by lazy {
        ViewModelProvider(this).get(ViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initObservers()
        initViews()
        when {
            isNetworkConnected(this) -> {
                viewModel.requestCurrencyFromAPI()
                viewModel.requestExchangeFromAPI("USD","PHP")
            }
            else ->
                Toast.makeText(this,"No internet found. Showing cached list in the view",Toast.LENGTH_LONG).show()
        }
    }


    private fun initViews() {
        et_convert_from.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                startTimerWithWork{

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (timer != null) {
                    timer!!.cancel()
                }
            }
        })

        et_convert_to.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                startTimerWithWork{

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (timer != null) {
                    timer!!.cancel()
                }
            }

        })

    }

    private fun startTimerWithWork(block: () -> Unit) {
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                block.invoke()
            }
        }, 600)
    }

    private fun initObservers() {
        viewModel.getCurrency()?.observe(this, Observer {listFromDatabase->
            Log.d("M_MainActivity","List of Currency size is : ${listFromDatabase.size}")
            currencyList = listFromDatabase
            spinnerAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,currencyList.map { it.id })
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp_convert_to.adapter = spinnerAdapter
            sp_convert_from.adapter = spinnerAdapter

        })

        viewModel.getExchangeRate().observe(this, Observer {
            ll_result.visibility = View.VISIBLE
            Log.d("M_MainActivity_TEST","$it")
            val string: String = if (it==null){
                "TEST NULL"
            } else{
                "exchange value is : ${it.exchangePair}: ${it.exchangeValue}"
            }

            tv_result.text = string
        })
    }

    private fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        parent?.setSelection(0)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id){
            R.id.sp_convert_from->{selectedItemFrom = currencyList[position]}
            R.id.sp_convert_to->{selectedItemTo = currencyList[position]}
        }
    }

}
