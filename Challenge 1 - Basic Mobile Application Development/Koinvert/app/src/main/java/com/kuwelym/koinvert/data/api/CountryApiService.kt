package com.kuwelym.koinvert.data.api

import com.kuwelym.koinvert.data.model.CountryInfo
import retrofit2.Response
import retrofit2.http.GET

interface CountryApiService {
    @GET("all")
    suspend fun getAllCountries(): Response<List<CountryInfo>>
}