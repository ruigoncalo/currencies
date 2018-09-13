package com.ruigoncalo.currencies.model

data class RateViewEntity(val currency: String,
                          val value: String,
                          val isSelected: Boolean)