package com.kuwelym.koinvert.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kuwelym.koinvert.data.model.CountryInfo
import com.kuwelym.koinvert.databinding.DialogSelectCurrencyBinding
import com.kuwelym.koinvert.ui.adapter.CurrencyAdapter
import com.kuwelym.koinvert.ui.adapter.CurrencyItem

class SelectCurrencyDialogFragment(
    private val countryList: List<CountryInfo>,
    private val onCurrencySelected: (CurrencyItem) -> Unit
) : DialogFragment() {

    private lateinit var adapter: CurrencyAdapter
    private var _binding: DialogSelectCurrencyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSelectCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchEditText()
        setupCancelButton()
    }

    private fun setupRecyclerView() {
        binding.currencyListView.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(context)
            adapter = CurrencyAdapter(context, countryList, onCurrencySelected, ::dismiss).also {
                this@SelectCurrencyDialogFragment.adapter = it
            }
        }
    }

    private fun setupSearchEditText() {
        binding.searchEditText.addTextChangedListener { text ->
            adapter.filter(text.toString())
        }
    }

    private fun setupCancelButton() {
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}