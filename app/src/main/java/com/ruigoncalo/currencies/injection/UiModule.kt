package com.ruigoncalo.currencies.injection

import com.ruigoncalo.currencies.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class UiModule {

    @ContributesAndroidInjector
    abstract fun contributesActivity(): MainActivity
}