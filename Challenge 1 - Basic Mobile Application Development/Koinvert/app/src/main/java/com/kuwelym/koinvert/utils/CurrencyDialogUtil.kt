package com.kuwelym.koinvert.utils

import androidx.fragment.app.FragmentManager
import com.kuwelym.koinvert.data.model.CountryInfo
import com.kuwelym.koinvert.ui.adapter.CurrencyItem
import com.kuwelym.koinvert.ui.dialog.SelectCurrencyDialogFragment

object CurrencyDialogUtil {
    fun showCurrencyDialog(
        fragmentManager: FragmentManager,
        countryList: List<CountryInfo>?,
        onCurrencySelected: (CurrencyItem) -> Unit
    ) {
        countryList?.let {
            val dialog = SelectCurrencyDialogFragment(it, onCurrencySelected)
            dialog.show(fragmentManager, "SelectCurrencyDialog")
        }
    }
}