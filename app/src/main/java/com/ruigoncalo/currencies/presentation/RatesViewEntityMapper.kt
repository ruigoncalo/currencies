package com.ruigoncalo.currencies.presentation

import com.ruigoncalo.currencies.model.RateViewEntity
import com.ruigoncalo.currencies.model.RatesViewEntity
import com.ruigoncalo.domain.model.Rates
import java.math.BigDecimal
import java.text.DecimalFormat
import javax.inject.Inject

class RatesViewEntityMapper @Inject constructor() {

    private val format by lazy { DecimalFormat("#,###.##").apply { isParseBigDecimal = true } }

    fun map(model: Rates, currencySelected: String): RatesViewEntity {
        return RatesViewEntity(model.date, map(model.rates, currencySelected))
    }

    private fun map(model: Map<String, BigDecimal>, currencySelected: String): List<RateViewEntity> {
        return model.map { RateViewEntity(it.key, format.format(it.value), it.key == currencySelected) }
    }

    fun normalize(value: String): String {
        return if (value.isBlank()) "0" else format.parse(value).toString()
    }
}