package com.kuwelym.koinvert.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuwelym.koinvert.data.model.CountryInfo
import com.kuwelym.koinvert.data.model.ExchangeRatesResponse
import com.kuwelym.koinvert.data.repository.CurrencyConverterRepository
import com.kuwelym.koinvert.utils.isInternetAvailable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CurrencyViewModel(private val repository: CurrencyConverterRepository, private val context: Context) : ViewModel() {
    private val _conversionResult = MutableLiveData<String>()
    val conversionResult: LiveData<String> = _conversionResult

    private val _indicativeRate = MutableLiveData<String>()
    val indicativeRate: LiveData<String> = _indicativeRate

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _countryList = MutableLiveData<List<CountryInfo>>()
    val countryList: LiveData<List<CountryInfo>> = _countryList

    private val _exchangeRates = MutableLiveData<ExchangeRatesResponse>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var exchangeRatesDeferred: CompletableDeferred<Boolean>? = null


    val currentAmount = MutableLiveData<String>()
    val currentSourceCurrency = MutableLiveData<String>()
    val currentTargetCurrency = MutableLiveData<String>()


    private val _autoConvertTrigger = MediatorLiveData<Unit>().apply {
        addSource(currentAmount) { value = Unit }
        addSource(currentSourceCurrency) { value = Unit }
        addSource(currentTargetCurrency) { value = Unit }
    }

    init {
        _autoConvertTrigger.observeForever {
            autoConvertCurrency()
        }
    }

    fun fetchCountries() {
        runBlocking {
            val result = repository.getAllCountries()
            result.onSuccess {
                _countryList.value = it
            }.onFailure {
                _error.value = "Failed to fetch countries."
            }
        }
    }

    private suspend fun ensureExchangeRatesLoaded(): Boolean {
        if (!isInternetAvailable(context)) {
            _error.value = "No internet connection."
            return false
        }
        if (exchangeRatesDeferred == null) {
            // This to ensure that we only fetch exchange rates once
            exchangeRatesDeferred = CompletableDeferred()

            _isLoading.value = true
            val result = repository.getExchangeRates()
            _isLoading.value = false

            result.onSuccess {
                _exchangeRates.value = it
                exchangeRatesDeferred?.complete(true)
            }.onFailure {
                _error.value = "Failed to fetch exchange rates."
                exchangeRatesDeferred?.complete(false)
            }
        }
        return exchangeRatesDeferred!!.await()
    }

    fun manualFetchExchangeRates() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getExchangeRates()
            _isLoading.value = false

            result.onSuccess {
                _exchangeRates.value = it
            }.onFailure {
                _error.value = "Failed to fetch exchange rates."
            }
        }
    }

    private fun getIndicativeExchangeRate() = viewModelScope.launch {
        val fromCurrency = currentSourceCurrency.value
        val toCurrency = currentTargetCurrency.value

        if (fromCurrency.isNullOrBlank() || toCurrency.isNullOrBlank()) {
            _indicativeRate.value = "0.00"
            return@launch
        }

        if (ensureExchangeRatesLoaded()) {
            _exchangeRates.value?.let { ratesResponse ->
                val rate = ratesResponse.rates[toCurrency]!! / ratesResponse.rates[fromCurrency]!!
                _indicativeRate.value = "%.2f".format(rate)
            }
        } else {
            _error.value = "Failed to fetch exchange rates."
        }
    }

    private fun autoConvertCurrency() {
        viewModelScope.launch {
            val amount = currentAmount.value?.toDoubleOrNull()
            val fromCurrency = currentSourceCurrency.value
            val toCurrency = currentTargetCurrency.value

            if (amount != null && !fromCurrency.isNullOrBlank() && !toCurrency.isNullOrBlank()) {
                convertCurrency(amount, fromCurrency, toCurrency)
                getIndicativeExchangeRate()
            }
        }
    }

    private fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String) {
        viewModelScope.launch {
            if (ensureExchangeRatesLoaded()) {
                _exchangeRates.value?.let { ratesResponse ->
                    val rate = ratesResponse.rates[toCurrency]!! / ratesResponse.rates[fromCurrency]!!
                    _conversionResult.value = "%.2f".format(amount * rate)
                }
            } else {
                _error.value = "Exchange rates not available"
                _conversionResult.value = "0.00"
            }
        }
    }
}
