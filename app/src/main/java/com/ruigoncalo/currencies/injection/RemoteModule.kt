package com.ruigoncalo.currencies.injection

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ruigoncalo.data.model.RatesRaw
import com.ruigoncalo.data.remote.RatesApi
import com.ruigoncalo.data.remote.RatesDeserializer
import com.ruigoncalo.data.remote.RetrofitFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
class RemoteModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message -> Log.d("OkHttp", message) }
                .setLevel(HttpLoggingInterceptor.Level.BASIC)
    }

    @Provides
    fun provideClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Provides
    fun provideRetrofitFactory() = RetrofitFactory()

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
                .registerTypeAdapter(RatesRaw::class.java, RatesDeserializer())
                .create()
    }

    @Provides
    @Singleton
    fun provideSocialApi(retrofitFactory: RetrofitFactory, client: OkHttpClient, gson: Gson): RatesApi {
        return retrofitFactory
                .build(client, gson)
                .create(RatesApi::class.java)
    }
}