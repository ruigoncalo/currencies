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
                   private val listener: (RateViewEntity, String) -> Unit) : RecyclerView.Adapter<RateItemView>() {

    private val inflater by lazy { LayoutInflater.from(context) }

    private val rates = mutableListOf<RateViewEntity>()

    private val inputListener: InputListener = object : InputListener {
        override fun onRateRequest(value: String, position: Int) {
            listener.invoke(rates[position], value)
        }

        override fun onSelectCurrency(position: Int) {
            // update clicked rate as selected
            val selected = rates[position]
            val newCurrencySelected =
                    RateViewEntity(selected.currencyCode, selected.currencyName, selected.value, true)

            // change previous selected as not selected
            val previous = rates[0]
            val previousCurrency =
                    RateViewEntity(previous.currencyCode, previous.currencyName, previous.value, false)
            rates.removeAt(0)
            rates.add(0, previousCurrency)

            // add selected to first position
            rates.removeAt(position)
            rates.add(0, newCurrencySelected)

            // update recyclerview views
            notifyItemMoved(position, 0)

            // Hack to update all items. This should be improved!
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

    // When list is empty, fill in with all items
    // When list is not empty, update all items and save in new list, but don't notify the first element
    fun update(newRates: List<RateViewEntity>) {
        if (rates.isEmpty()) {
            rates.addAll(newRates)
            notifyDataSetChanged()
        } else {
            val list = mutableListOf<RateViewEntity>()
            rates.forEachIndexed { index, rate ->
                    val value = newRates.first { it.currencyCode == rate.currencyCode }.value
                    list.add(index,
                            RateViewEntity(rate.currencyCode, rate.currencyName, value, false))
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