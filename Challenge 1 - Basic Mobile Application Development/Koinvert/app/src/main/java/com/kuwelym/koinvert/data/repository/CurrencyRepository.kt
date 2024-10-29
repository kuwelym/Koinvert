package com.kuwelym.koinvert.data.repository

import com.kuwelym.koinvert.data.api.RetrofitInstance
import com.kuwelym.koinvert.data.model.CountryInfo
import com.kuwelym.koinvert.data.model.ExchangeRatesResponse

class CurrencyConverterRepository {

    suspend fun getAllCountries(): Result<List<CountryInfo>> {
        return try {
            val response = RetrofitInstance.countryApiService.getAllCountries()
            if (response.isSuccessful) {
                // Success: return the body content
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error fetching countries: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExchangeRates(): Result<ExchangeRatesResponse> {
        return try {
            val response = RetrofitInstance.exchangeApiService.getExchangeRates()
            if (response.isSuccessful) {
                // Success: return the body content
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Exchange rates data is null"))
            } else {
                Result.failure(Exception("Error fetching exchange rates: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
