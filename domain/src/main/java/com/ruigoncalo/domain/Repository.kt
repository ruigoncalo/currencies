package com.ruigoncalo.domain

import com.ruigoncalo.domain.model.Rates
import io.reactivex.Completable
import io.reactivex.Observable
import polanski.option.Option

interface Repository {

    fun getRates(): Observable<Option<Rates>>

    fun fetchRates(): Completable
}