package com.ruigoncalo.currencies.ui

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.ruigoncalo.currencies.R
import com.ruigoncalo.currencies.model.RateViewEntity
import io.reactivex.disposables.Disposable


class RateItemView(private val view: View,
                   private val inputListener: RatesAdapter.InputListener) : RecyclerView.ViewHolder(view) {

    private var textWatcherSubscription: Disposable? = null

    fun bind(rate: RateViewEntity) {
        val nameView = view.findViewById<TextView>(R.id.currencyNameTextView)
        val inputView = view.findViewById<EditText>(R.id.rateInputTextView)

        nameView.text = rate.currency
        inputView.setText(rate.value)
        inputView.setSelection(inputView.text.length)

        inputView.isFocusableInTouchMode = rate.isSelected
        inputView.isFocusable = rate.isSelected

        if (rate.isSelected) {
            textWatcherSubscription?.dispose()
            textWatcherSubscription = RxTextView.textChanges(inputView)
                    .filter { adapterPosition == 0 }
                    .map { it.toString() }
                    .subscribe({ s ->
                        Log.d("Test", "${rate.currency}|${rate.value}($adapterPosition) send $s")
                        inputListener.onRateRequest(s, adapterPosition)
                    }, { e -> Log.e("RateItemView", e.message) })
        } else {
            textWatcherSubscription?.dispose()
        }

        if (!rate.isSelected) {
            inputView.setOnClickListener {
                inputView.isFocusableInTouchMode = true
                inputView.isFocusable = true
                inputView.requestFocus()
                inputListener.onSelectCurrency(adapterPosition)
            }
        } else {
            inputView.setOnClickListener(null)
        }
    }
}