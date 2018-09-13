package com.ruigoncalo.data

import com.ruigoncalo.data.remote.RatesApi
import com.ruigoncalo.data.store.Store
import com.ruigoncalo.domain.Repository
import com.ruigoncalo.domain.model.Rates
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import polanski.option.Option

data class RatesRepository(private val api: RatesApi,
                           private val store: Store<Rates>,
                           private val mapper: DataMapper) : Repository {

    override fun getRates(): Observable<Option<Rates>> {
        return store.get()
    }

    override fun fetchRates(): Completable {
        return api.getRates()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(mapper::map)
                .flatMapCompletable { rates ->
                    store.store(rates)
                }
    }
}