package com.ruigoncalo.currencies.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import com.ruigoncalo.currencies.R
import com.ruigoncalo.currencies.model.RateViewEntity

class RatesAdapter(private val context: Context,
                   private val listener: (String, String) -> Unit) : RecyclerView.Adapter<RateItemView>() {

    private val inflater by lazy { LayoutInflater.from(context) }

    private val rates = mutableListOf<RateViewEntity>()

    private val inputListener: InputListener = object : InputListener {
        override fun onRateRequest(value: String, position: Int) {
            listener.invoke(rates[position].currency, value)
        }

        override fun onSelectCurrency(position: Int) {
            val rate = rates[position]
            val newCurrencySelected = RateViewEntity(rate.currency, rate.value, true)

            val previousSelectedCurrency = rates[0]
            val resetSelectedCurrency = RateViewEntity(previousSelectedCurrency.currency, previousSelectedCurrency.value, false)
            rates.removeAt(0)
            rates.add(0, resetSelectedCurrency)

            rates.removeAt(position)
            rates.add(0, newCurrencySelected)

            notifyItemMoved(position, 0)
            notifyItemRangeChanged(0, rates.size)
        }
    }

    override fun getItemCount(): Int {
        return rates.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateItemView {
        val view = inflater.inflate(R.layout.currency_item_view, parent, false)
        val inputView = view.findViewById<EditText>(R.id.rateInputTextView)
        inputView.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        return RateItemView(view, inputListener)
    }

    override fun onBindViewHolder(holder: RateItemView, position: Int) {
        holder.bind(rates[position])
    }

    fun update(newRates: List<RateViewEntity>) {
        if (rates.isEmpty()) {
            rates.addAll(newRates)
            notifyDataSetChanged()
        } else {
            val list = mutableListOf<RateViewEntity>()
            rates.forEachIndexed { index, rate ->
                val currency = rate.currency
                val value = newRates.first { it.currency == currency }.value
                list.add(index, RateViewEntity(currency, value, index == 0))
            }

            rates.clear()
            rates.addAll(list)
            notifyItemRangeChanged(1, rates.size - 1)
        }
    }

    interface InputListener {
        fun onRateRequest(value: String, position: Int)
        fun onSelectCurrency(position: Int)
    }
}