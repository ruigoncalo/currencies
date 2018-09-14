package com.ruigoncalo.domain

import com.ruigoncalo.domain.model.Rates
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import polanski.option.Option
import polanski.option.OptionUnsafe
import javax.inject.Inject

class GetRatesUseCase @Inject constructor(private val repository: Repository,
                                          private val calculator: RatesCalculator) : RatesInteractor {

    override fun getRates(currency: String, newValue: String): Observable<Rates> {
        return repository.getRates()
                .flatMapSingle { fetchWhenNoneAndThenRetrieve(it) }
                .filter(Option<Rates>::isSome)
                .map { OptionUnsafe.getUnsafe(it) }
                .flatMapSingle { rates ->
                    calculator.convert(currency, newValue, rates.rates)
                            .map { newRates -> Rates(rates.date, newRates) }
                }
    }

    private fun fetchWhenNoneAndThenRetrieve(rates: Option<Rates>): Single<Option<Rates>> {
        return fetchWhenNone(rates).andThen(Single.just(rates))
    }

    private fun fetchWhenNone(posts: Option<Rates>): Completable {
        return if (posts.isNone) repository.fetchRates() else Completable.complete()
    }

    override fun requestRates(): Completable {
        return repository.fetchRates()
    }
}