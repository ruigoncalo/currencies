package com.ruigoncalo.data.store

import com.ruigoncalo.data.cache.Cache
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import polanski.option.Option

class RatesStore<Value>(private val cache: Cache<Value>) : Store<Value> {

    private val subject: PublishSubject<Option<Value>> = PublishSubject.create()

    override fun get(): Observable<Option<Value>> {
        return Observable.defer { Observable.just(Option.ofObj(cache.get())) }
                .onErrorResumeNext { _: Throwable -> Observable.just<Option<Value>>(Option.none()) }
                .concatWith(subject)
    }

    override fun store(value: Value): Completable {
        return Completable.fromCallable {
            cache.put(value)
            subject.onNext(Option.ofObj(value))
        }
    }
}