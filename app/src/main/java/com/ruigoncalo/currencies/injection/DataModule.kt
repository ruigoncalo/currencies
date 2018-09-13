package com.ruigoncalo.currencies.injection

import com.ruigoncalo.data.DataMapper
import com.ruigoncalo.data.RatesRepository
import com.ruigoncalo.data.cache.Cache
import com.ruigoncalo.data.cache.MemoryCache
import com.ruigoncalo.data.remote.RatesApi
import com.ruigoncalo.data.store.RatesStore
import com.ruigoncalo.data.store.Store
import com.ruigoncalo.domain.Repository
import com.ruigoncalo.domain.model.Rates
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun providesCache(): Cache<Rates> {
        return MemoryCache()
    }

    @Provides
    @Singleton
    fun providesStore(cache: Cache<Rates>): Store<Rates> {
        return RatesStore(cache)
    }

    @Provides
    fun providesRepository(api: RatesApi, store: Store<Rates>, mapper: DataMapper): Repository {
        return RatesRepository(api, store, mapper)
    }
}