package com.ruigoncalo.currencies.model

data class RatesViewEntity(val date: String,
                           val rates: List<RateViewEntity>)