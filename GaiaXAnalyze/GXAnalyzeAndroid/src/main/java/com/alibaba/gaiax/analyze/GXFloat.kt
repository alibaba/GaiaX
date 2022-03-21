package com.alibaba.gaiax.analyze

public final class GXFloat(pointer: Long) : GXValue(pointer) {
    private var value: Float? = null

    constructor(pointer: Long, value: Float) : this(pointer) {
        this.value = value
    }

    fun getFloat(): Float? {
        return value
    }

    override fun getValue(): Any? {
        return value;
    }
}