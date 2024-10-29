package com.kuwelym.koinvert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.kuwelym.koinvert.data.repository.CurrencyConverterRepository
import com.kuwelym.koinvert.databinding.ActivityMainBinding
import com.kuwelym.koinvert.ui.adapter.FLAG_BASE_URL
import com.kuwelym.koinvert.ui.adapter.loadWithFallback
import com.kuwelym.koinvert.ui.viewmodel.CurrencyViewModel
import com.kuwelym.koinvert.ui.viewmodel.CurrencyViewModelFactory
import com.kuwelym.koinvert.utils.ConnectivityReceiver
import com.kuwelym.koinvert.utils.CurrencyDialogUtil
import com.kuwelym.koinvert.utils.isInternetAvailable

class MainActivity : AppCompatActivity() {

    private val viewModel: CurrencyViewModel by viewModels {
        CurrencyViewModelFactory(CurrencyConverterRepository(), this)
    }
    private lateinit var binding: ActivityMainBinding
    private val connectivityReceiver = ConnectivityReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        checkInternetAndShowToast()

        setupObservers()
        setupListeners()

        viewModel.fetchCountries()
        initializeDefaultValues()
        registerConnectivityBroadcastReceiver()

    }

    private fun checkInternetAndShowToast() {
        if (!isInternetAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show()
        }
    }

    private fun registerConnectivityBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == ConnectivityReceiver.ACTION_INTERNET_AVAILABLE) {
                        viewModel.fetchCountries()
                        viewModel.manualFetchExchangeRates()
                        reloadValues()
                    }
                }
            },
            IntentFilter(ConnectivityReceiver.ACTION_INTERNET_AVAILABLE)
        )
    }

    private fun reloadValues() {
        viewModel.currentAmount.value = binding.conversionCard.sourceAmount.text.toString()
        viewModel.currentSourceCurrency.value =
            binding.conversionCard.sourceCurrency.text.toString()
        viewModel.currentTargetCurrency.value =
            binding.conversionCard.targetCurrency.text.toString()
    }

    private fun setupObservers() {
        viewModel.conversionResult.observe(this) { result ->
            binding.conversionCard.targetAmount.text = result
        }

        viewModel.indicativeRate.observe(this) {
            binding.indicativeExchangeRate.indicativeToAmount.text = it
        }

        viewModel.currentSourceCurrency.observe(this) { updateCurrencyUI(it, true) }
        viewModel.currentTargetCurrency.observe(this) { updateCurrencyUI(it, false) }

        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCurrencyUI(currencyCode: String, isSource: Boolean) {
        val countryInfo = viewModel.countryList.value?.find { it.currencyCode == currencyCode }
        val currencyName =
            countryInfo?.currencies?.entries?.firstOrNull { it.key == currencyCode }?.value?.name
        val flagUrlPrimary = FLAG_BASE_URL + currencyCode.lowercase().substring(0, 2) + ".svg"
        val flagUrlFallback = FLAG_BASE_URL + countryInfo?.cca2LowerCase + ".svg"

        if (isSource) {
            with(binding.conversionCard) {
                sourceCurrency.text = currencyCode
                sourceCurrencyName.text = currencyName
                sourceCurrencyFlag.loadWithFallback(flagUrlPrimary, flagUrlFallback)
            }
            binding.indicativeExchangeRate.indicativeFromCurrency.text = currencyCode
        } else {
            with(binding.conversionCard) {
                targetCurrency.text = currencyCode
                targetCurrencyName.text = currencyName
                targetCurrencyFlag.loadWithFallback(flagUrlPrimary, flagUrlFallback)
            }
            binding.indicativeExchangeRate.indicativeToCurrency.text = currencyCode
        }
    }

    private fun initializeDefaultValues() {
        viewModel.apply {
            currentAmount.value = "1.0"
            currentSourceCurrency.value = "USD"
            currentTargetCurrency.value = "EUR"
        }
    }

    private fun setupListeners() {
        binding.conversionCard.sourceAmount.addTextChangedListener(afterTextChanged = {
            viewModel.currentAmount.value = it.toString().ifEmpty { "0" }
        })

        binding.conversionCard.exchangeButton.setOnClickListener {
            viewModel.currentSourceCurrency.value = viewModel.currentTargetCurrency.value.also {
                viewModel.currentTargetCurrency.value = viewModel.currentSourceCurrency.value
            }
        }

        setupCurrencyDialog(
            binding.conversionCard.sourceAmountLayout,
            viewModel.currentSourceCurrency
        )
        setupCurrencyDialog(
            binding.conversionCard.convertedAmountLayout,
            viewModel.currentTargetCurrency
        )
    }

    private fun setupCurrencyDialog(layout: View, currencyCodeLiveData: MutableLiveData<String>) {
        layout.setOnClickListener {
            CurrencyDialogUtil.showCurrencyDialog(
                supportFragmentManager,
                viewModel.countryList.value
            ) {
                currencyCodeLiveData.value = it.currencyCode
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            connectivityReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        registerReceiver(
            connectivityReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(connectivityReceiver)
        unregisterReceiver(connectivityReceiver)
    }
}