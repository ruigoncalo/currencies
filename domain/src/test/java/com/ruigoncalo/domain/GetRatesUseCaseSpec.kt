package com.ruigoncalo.domain

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.ruigoncalo.domain.model.Rates
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import polanski.option.Option
import java.math.BigDecimal

class GetRatesUseCaseSpec : Spek({

    val repository: Repository = mock()
    val calculator: RatesCalculator = mock()
    val tested = GetRatesUseCase(repository, calculator)

    var testObserver = TestObserver<Rates>()

    val mockRates = Rates("date", mapOf("EUR" to BigDecimal("1")))

    beforeEachTest {
        whenever(calculator.convert("EUR", "1", mockRates.rates)).thenReturn(Single.just(mockRates.rates))
        testObserver = TestObserver()
    }

    describe("get rates from repository") {

        context("no rates to get") {
            beforeEachTest {
                whenever(repository.getRates()).thenReturn(Observable.just(Option.none()))
                whenever(repository.fetchRates()).thenReturn(Completable.complete())
                tested.getRates("EUR", "1").subscribe(testObserver)
            }

            it("should fetch rates") {
                verify(repository).fetchRates()
            }

            it("should return nothing and complete") {
                with(testObserver) {
                    assertNoValues()
                    assertNoErrors()
                    assertComplete()
                }
            }
        }

        context("rates to get") {
            beforeEachTest {
                whenever(repository.getRates()).thenReturn(Observable.just(Option.ofObj(mockRates)))
                whenever(repository.fetchRates()).thenReturn(Completable.complete())
                tested.getRates("EUR", "1").subscribe(testObserver)
            }

            it("should not fetch rates") {
                verify(repository, never()).fetchRates()
            }

            it("should return rates and complete") {
                with(testObserver) {
                    assertValue(mockRates)
                    assertNoErrors()
                    assertComplete()
                }
            }
        }
    }


})