package com.ruigoncalo.currencies.model

data class RateViewEntity(val currencyCode: String,
                          val currencyName: String,
                          val value: String,
                          val isSelected: Boolean)