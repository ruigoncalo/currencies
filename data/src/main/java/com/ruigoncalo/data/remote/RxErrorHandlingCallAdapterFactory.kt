package com.ruigoncalo.data.remote

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.lang.reflect.Type

class RxErrorHandlingCallAdapterFactory private constructor() : CallAdapter.Factory() {

    private val original by lazy { RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()) }

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *> {
        val wrapped = original.get(returnType, annotations, retrofit) as CallAdapter<out Any, *>
        return RxCallAdapterWrapper(retrofit, wrapped)
    }

    private class RxCallAdapterWrapper<R>(private val retrofit: Retrofit,
                                          private val wrapped: CallAdapter<R, *>) : CallAdapter<R, Single<R>> {

        override fun responseType(): Type {
            return wrapped.responseType()
        }

        @Suppress("UNCHECKED_CAST")
        override fun adapt(call: Call<R>): Single<R> {
            return (wrapped.adapt(call) as Single<R>).onErrorResumeNext {
                throwable: Throwable -> Single.error(asRetrofitException(throwable))
            }
        }

        private fun asRetrofitException(throwable: Throwable): NetworkException {
            // We had non-200 http error
            if (throwable is HttpException) {
                val response = throwable.response()
                return NetworkException.httpError(response.raw().request().url().toString(), response, retrofit)
            }

            // A network error happened
            if (throwable is IOException) {
                return NetworkException.networkError(throwable)
            }

            // We don't know what happened. We need to simply convert to an unknown error

            return NetworkException.unexpectedError(throwable)
        }
    }

    companion object {
        fun create(): CallAdapter.Factory {
            return RxErrorHandlingCallAdapterFactory()
        }
    }
}