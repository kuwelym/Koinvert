package com.kuwelym.koinvert.data.model

import com.google.gson.annotations.SerializedName

data class CountryInfo(
    @SerializedName("name") val name: Name,
    @SerializedName("currencies") val currencies: Map<String, Currency>?,
    @SerializedName("cca2") private val cca2: String
) {
    val currencyCode: String?
        get() = currencies?.entries?.first()?.key

    val currencyName: String?
        get() = currencies?.entries?.first()?.value?.name

    val cca2LowerCase: String
        get() = cca2.lowercase()
}

data class Name(
    @SerializedName("common") val common: String
)

data class Currency(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String
)