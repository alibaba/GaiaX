package com.alibaba.gaiax.analyze

public final class GXMap() : GXValue() {
    private var value: Any? = null

    constructor(value: Any?) : this() {
        this.value = value
    }

    override fun getValue(): Any? {
        return value;
    }
}