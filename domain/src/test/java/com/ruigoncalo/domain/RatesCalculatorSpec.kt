package com.ruigoncalo.domain

import io.reactivex.observers.TestObserver
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import java.math.BigDecimal

class RatesCalculatorSpec : Spek({

    val tested = RatesCalculator()
    var testObservable = TestObserver<Map<String, BigDecimal>>()

    beforeEachTest {
        testObservable = TestObserver()
    }

    given("a map of currencies and rates") {
        val map = mapOf(
                "EUR" to BigDecimal("1"),
                "USD" to BigDecimal("1.5"),
                "GBP" to BigDecimal("2"))

        given("a currency EUR and a value of 2") {
            beforeEachTest {
                tested.convert("EUR", "2", map).subscribe(testObservable)
            }

            it("should multiply each value by 2") {
                val result = mapOf(
                        "EUR" to BigDecimal("2"),
                        "USD" to BigDecimal("3.0"),
                        "GBP" to BigDecimal("4"))

                testObservable.assertValue(result)
            }
        }

        given("a currency USD and a value of 6") {
            beforeEachTest {
                tested.convert("USD", "6", map).subscribe(testObservable)
            }

            it("should apply EUR = 6/1.5 and GBP = (6/1.5)*2") {
                val result = mapOf(
                        "EUR" to BigDecimal("4"),
                        "USD" to BigDecimal("6"),
                        "GBP" to BigDecimal("8"))

                testObservable.assertValue(result)
            }
        }
    }

})