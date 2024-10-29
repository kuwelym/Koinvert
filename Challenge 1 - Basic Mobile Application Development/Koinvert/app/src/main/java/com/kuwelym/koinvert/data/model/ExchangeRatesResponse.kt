package com.kuwelym.koinvert.data.model

data class ExchangeRatesResponse(
    val base: String,
    val rates: Map<String, Double>,
    val date: String
)
