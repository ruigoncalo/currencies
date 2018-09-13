package com.ruigoncalo.data.remote

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.ruigoncalo.data.model.RatesRaw
import java.lang.reflect.Type

class RatesDeserializer : JsonDeserializer<RatesRaw> {

    companion object {
        private const val ELEM_BASE = "base"
        private const val ELEM_DATE = "date"
        private const val ELEM_RATES = "rates"
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): RatesRaw? {
        return json?.let {
            val root: JsonObject = it as JsonObject

            val base = root.get(ELEM_BASE).asString
            val date = root.get(ELEM_DATE).asString

            val rates = root.get(ELEM_RATES).asJsonObject

            val ratesMap = mutableMapOf<String, String>()
            ratesMap[base] = "1" // add base currency to map

            rates.entrySet().forEach { entry ->
                val currency = entry.key
                val value = entry.value.asString
                ratesMap[currency] = value
            }

            return RatesRaw(base, date, ratesMap)
        }
    }
}