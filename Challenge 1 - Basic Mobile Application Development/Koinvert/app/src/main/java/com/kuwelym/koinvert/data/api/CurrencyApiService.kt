package com.kuwelym.koinvert.data.api

import com.kuwelym.koinvert.BuildConfig
import com.kuwelym.koinvert.data.model.ExchangeRatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("access_key") apiKey: String = BuildConfig.API_KEY,
    ): Response<ExchangeRatesResponse>
}