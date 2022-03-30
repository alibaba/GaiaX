package com.alibaba.gaiax.analyze

public final class GXArray(pointer: Long) : GXValue(pointer) {
    private var value: Any? = null

    constructor(pointer: Long, value: Any?) : this(pointer) {
        this.value = value
    }

    override fun getValue(): Any? {
        return value;
    }
}