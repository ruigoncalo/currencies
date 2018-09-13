package com.ruigoncalo.currencies.injection

import com.ruigoncalo.domain.GetRatesUseCase
import com.ruigoncalo.domain.RatesInteractor
import dagger.Binds
import dagger.Module

@Module
abstract class DomainModule {

    @Binds
    abstract fun bindsInteractor(useCase: GetRatesUseCase): RatesInteractor
}