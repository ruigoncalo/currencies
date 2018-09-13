package com.ruigoncalo.data

import com.ruigoncalo.data.model.RatesRaw
import com.ruigoncalo.domain.model.Rates
import java.math.BigDecimal
import javax.inject.Inject

class DataMapper @Inject constructor() {

    fun map(raw: RatesRaw): Rates {
        return Rates(raw.date, map(raw.rates))
    }

    private fun map(raw: Map<String, String>): Map<String, BigDecimal> {
        val result = mutableMapOf<String, BigDecimal>()
        raw.forEach { key, value -> result[key] = BigDecimal(value) }
        return result
    }
}