package com.ruigoncalo.data.store

import io.reactivex.Completable
import io.reactivex.Observable
import polanski.option.Option

interface Store<Value> {

    fun get(): Observable<Option<Value>>

    fun store(value: Value): Completable
}