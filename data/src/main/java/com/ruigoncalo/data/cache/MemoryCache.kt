package com.ruigoncalo.data.cache

class MemoryCache<Value> : Cache<Value> {

    private var value: Value? = null

    override fun get(): Value {
        return value ?: throw IllegalStateException("No value cached")
    }

    override fun put(value: Value) {
        this.value = value
    }
}