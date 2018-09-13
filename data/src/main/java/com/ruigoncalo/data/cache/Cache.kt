package com.ruigoncalo.data.cache

interface Cache<Value> {

    fun get(): Value

    fun put(value: Value)
}