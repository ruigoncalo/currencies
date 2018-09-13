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
        val codeView = view.findViewById<TextView>(R.id.currencyCodeTextView)
        val nameView = view.findViewById<TextView>(R.id.currencyNameTextView)
        val inputView = view.findViewById<EditText>(R.id.rateInputTextView)

        codeView.text = rate.currencyCode
        nameView.text = rate.currencyName

        // set EditText text and move cursor to the end
        inputView.setText(rate.value)
        inputView.setSelection(inputView.text.length)

        // EditText focusable if selected rate
        inputView.isFocusableInTouchMode = rate.isSelected
        inputView.isFocusable = rate.isSelected

        // observer EditText if selected rate
        if (rate.isSelected) {
            textWatcherSubscription?.dispose()
            textWatcherSubscription = RxTextView.textChanges(inputView)
                    .filter { adapterPosition == 0 }
                    .map { it.toString() }
                    .subscribe({ s ->
                        inputListener.onRateRequest(s, adapterPosition)
                    }, { e -> Log.e("RateItemView", e.message) })
        } else {
            textWatcherSubscription?.dispose()
        }

        // if not selected rate, add click listeners to the item and to the EditText
        if (!rate.isSelected) {
            inputView.setOnClickListener {
                onViewSelected(inputView)
            }

            view.setOnClickListener {
                onViewSelected(inputView)
            }
        } else {
            inputView.setOnClickListener(null)
        }
    }

    private fun onViewSelected(inputView: View) {
        inputView.isFocusableInTouchMode = true
        inputView.isFocusable = true
        inputView.requestFocus()
        inputListener.onSelectCurrency(adapterPosition)
    }
}