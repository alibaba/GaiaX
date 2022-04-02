package com.alibaba.gaiax.analyze

public final class GXFloat() : GXValue() {
    private var value: Float? = null

    constructor(value: Float) : this() {
        this.value = value
    }

    fun getFloat(): Float? {
        return value
    }

    override fun getValue(): Any? {
        return value;
    }
}