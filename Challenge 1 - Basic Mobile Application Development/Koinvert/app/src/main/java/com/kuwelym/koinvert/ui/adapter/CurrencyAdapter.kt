package com.kuwelym.koinvert.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.kuwelym.koinvert.R
import com.kuwelym.koinvert.data.model.CountryInfo
import de.hdodenhof.circleimageview.CircleImageView

const val FLAG_BASE_URL = "https://cdnjs.cloudflare.com/ajax/libs/flag-icons/7.2.3/flags/1x1/"

class CurrencyAdapter(
    private val context: Context,
    private val countryList: List<CountryInfo>,
    private val onCurrencySelected: (CurrencyItem) -> Unit,
    private val dismissDialog: () -> Unit
) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private val currencyCodeNameList: Set<CurrencyItem> = countryList.mapNotNull {
        val code = it.currencyCode
        val name = it.currencyName
        if (code != null && name != null) {
            CurrencyItem(code, name)
        } else {
            null
        }
    }.toSet()
    private var filteredCurrencySet = currencyCodeNameList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_currency, parent, false)
        return CurrencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currency = filteredCurrencySet.elementAt(position)
        holder.bind(currency)
    }

    override fun getItemCount(): Int = filteredCurrencySet.size

    fun filter(query: String) {
        filteredCurrencySet = if (query.isEmpty()) {
            currencyCodeNameList
        } else {
            currencyCodeNameList.filter {
                it.currencyCode.contains(query, ignoreCase = true) ||
                        it.currencyName.contains(query, ignoreCase = true)
            }.toSet()
        }

        notifyDataSetChanged()
    }

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val currencyNameTextView: TextView = itemView.findViewById(R.id.currency_name)
        private val currencyCodeTextView: TextView = itemView.findViewById(R.id.currency_code)
        private val flagImageView: CircleImageView = itemView.findViewById(R.id.currency_flag)

        fun bind(currency: CurrencyItem) {
            currencyCodeTextView.text = currency.currencyCode
            currencyNameTextView.text = currency.currencyName
            val primaryUrl = FLAG_BASE_URL + currency.currencyCode.lowercase().substring(0, 2) + ".svg"
            val fallbackUrl = FLAG_BASE_URL + countryList.find { it.currencyCode == currency.currencyCode }?.cca2LowerCase + ".svg"
            flagImageView.loadWithFallback(primaryUrl, fallbackUrl)

            itemView.setOnClickListener {
                onCurrencySelected(currency)
                dismissDialog()
            }
        }
    }
}

data class CurrencyItem(
    val currencyCode: String,
    val currencyName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CurrencyItem) return false
        return currencyCode == other.currencyCode
    }

    override fun hashCode(): Int {
        return currencyCode.hashCode()
    }
}

fun ImageView.loadWithFallback(primaryUrl: String, fallbackUrl: String) {
    val imageLoader = ImageLoader.Builder(context)
        .components { add(SvgDecoder.Factory()) }
        .build()
    val request = ImageRequest.Builder(context)
        .data(primaryUrl)
        .target(
            onSuccess = { result -> this.setImageDrawable(result) },
            onError = {
                val fallbackRequest = ImageRequest.Builder(context)
                    .data(fallbackUrl)
                    .target(this)
                    .build()
                imageLoader.enqueue(fallbackRequest)
            }
        )
        .build()
    imageLoader.enqueue(request)
}