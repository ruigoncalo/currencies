package com.ruigoncalo.currencies.presentation

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ruigoncalo.currencies.model.RateRequest
import com.ruigoncalo.currencies.model.RatesViewEntity
import com.ruigoncalo.domain.RatesInteractor
import com.ruigoncalo.social.presentation.ViewResource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RatesViewModel @Inject constructor(
        private val interactor: RatesInteractor,
        private val mapper: RatesViewEntityMapper) : ViewModel() {

    private var disposable: Disposable? = null
    private var refreshDisposable: Disposable? = null

    private var lastRequest: RateRequest? = null

    private val ratesLiveData: MutableLiveData<ViewResource<RatesViewEntity>> = MutableLiveData()

    init {
        retrieveRates(RateRequest("EUR", "1"))
    }

    fun getRatesLiveData(): MutableLiveData<ViewResource<RatesViewEntity>> {
        return ratesLiveData
    }

    fun retrieveRates(request: RateRequest) {
        if (lastRequest != request) {
            lastRequest = request
            disposable?.dispose()
            disposable = Observable.just(request.value)
                    .doOnNext { pauseAndStartRefreshRates() }
                    .subscribeOn(Schedulers.computation())
                    .map(mapper::normalize)
                    .observeOn(Schedulers.io())
                    .flatMap { newValue -> interactor.getRates(request.currency, newValue) }
                    .observeOn(Schedulers.computation())
                    .map { mapper.map(it, request.currency) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ rates ->
                        ratesLiveData.value = ViewResource.success(rates)
                    }, { error ->
                        ratesLiveData.value = ViewResource.error(error.message)
                    })
        }
    }

    // start refresh rates after 3 seconds to avoid flicks on the UI while requesting rates
    private fun pauseAndStartRefreshRates() {
        refreshDisposable?.dispose()
        refreshDisposable =
                Observable.timer(3, TimeUnit.SECONDS)
                        .flatMap { Observable.interval(0L, 1L, TimeUnit.SECONDS) }
                        .flatMapCompletable { interactor.requestRates() }
                        .subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
        refreshDisposable?.dispose()
    }
}