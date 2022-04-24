package com.alibaba.gaiax.analyze

public final class GXString() : GXValue() {
    private var value: String? = null

    constructor(value: String) : this() {
        this.value = value
    }

    fun getString(): String? {
        return value
    }

    override fun getValue(): Any? {
        return value;
    }
}