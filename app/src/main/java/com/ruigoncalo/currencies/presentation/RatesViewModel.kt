package com.ruigoncalo.currencies.presentation

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.ruigoncalo.currencies.model.RateRequest
import com.ruigoncalo.currencies.model.RatesViewEntity
import com.ruigoncalo.domain.RatesInteractor
import com.ruigoncalo.social.presentation.ViewResource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RatesViewModel @Inject constructor(
        private val interactor: RatesInteractor,
        private val mapper: RatesViewEntityMapper) : ViewModel() {

    private var disposable: Disposable? = null
    private var lastRequest: RateRequest? = null

    private val ratesLiveData: MutableLiveData<ViewResource<RatesViewEntity>> = MutableLiveData()

    init {
        retrieveRates(RateRequest("EUR", "1.0"))
    }

    fun getRatesLiveData(): MutableLiveData<ViewResource<RatesViewEntity>> {
        return ratesLiveData
    }

    fun retrieveRates(request: RateRequest) {
        if (lastRequest != request) {
            Log.d("Test", "Requesting ${request.currency} ${request.value}")
            lastRequest = request
            disposable?.dispose()
            disposable = Observable.just(request.value)
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

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}