package com.ruigoncalo.domain.model

import java.math.BigDecimal

data class Rates(val date: String,
                 val rates: Map<String, BigDecimal>)