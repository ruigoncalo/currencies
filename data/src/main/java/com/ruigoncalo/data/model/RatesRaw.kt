package com.ruigoncalo.data.model

data class RatesRaw(val base: String,
                    val date: String,
                    val rates: Map<String, String>)