package com.kuwelym.koinvert.data.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val EXCHANGE_BASE_URL = "https://api.exchangeratesapi.io/v1/"
    private const val COUNTRIES_BASE_URL = "https://restcountries.com/v3.1/"


    val exchangeApiService: CurrencyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(EXCHANGE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(CurrencyApiService::class.java)
    }
    val countryApiService: CountryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(COUNTRIES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(CountryApiService::class.java)
    }
}