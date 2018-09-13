package com.ruigoncalo.domain

import io.reactivex.Single
import java.math.BigDecimal
import javax.inject.Inject

class RatesCalculator @Inject constructor() {

    fun convert(currency: String, newValue: String, rates: Map<String, BigDecimal>): Single<Map<String, BigDecimal>> {
        return Single.create { emitter ->
            val newValueDecimal = BigDecimal(newValue)
            val base = rates[currency]

            base?.let {
                val baseValue = newValueDecimal.div(base)

                val newRates = mutableMapOf<String, BigDecimal>()
                newRates[currency] = newValueDecimal

                rates.forEach { key, value ->
                    if (key != currency) {
                        newRates[key] = baseValue.multiply(value)
                    }
                }

                emitter.onSuccess(newRates)
            } ?: emitter.onError(IllegalStateException("No currency found"))
        }
    }
}