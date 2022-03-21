package com.alibaba.gaiax.analyze

public final class GXBool(pointer: Long) : GXValue(pointer) {
    private var value: Boolean? = null

    constructor(pointer: Long, value: Boolean) : this(pointer) {
        this.value = value
    }

    fun getBool(): Boolean? {
        return value
    }

    override fun getValue(): Any? {
        return value
    }
}