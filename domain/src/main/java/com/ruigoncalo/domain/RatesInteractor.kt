package com.ruigoncalo.domain

import com.ruigoncalo.domain.model.Rates
import io.reactivex.Completable
import io.reactivex.Observable

interface RatesInteractor {

    fun getRates(currency: String, newValue: String): Observable<Rates>

    fun requestRates(): Completable
}