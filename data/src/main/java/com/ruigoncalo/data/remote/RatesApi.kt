package com.ruigoncalo.data.remote

import com.ruigoncalo.data.model.RatesRaw
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesApi {

    @GET("latest")
    fun getRates(@Query("base") base: String = "eur"): Single<RatesRaw>
}