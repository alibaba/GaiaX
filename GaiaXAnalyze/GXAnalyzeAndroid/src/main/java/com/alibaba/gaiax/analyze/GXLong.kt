package com.alibaba.gaiax.analyze

class GXLong() : GXValue() {
    private var value: Long? = null

    constructor(value: Long) : this() {
        this.value = value
    }

    fun getLong(): Long? {
        return value
    }

    override fun getValue(): Any? {
        return value
    }
}