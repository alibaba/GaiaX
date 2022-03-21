package com.alibaba.gaiax.analyze

public final class GXString(pointer: Long) : GXValue(pointer) {
    private var value: String? = null

    constructor(pointer: Long, value: String) : this(pointer) {
        this.value = value
    }

    fun getString(): String? {
        return value
    }

    override fun getValue(): Any? {
        return value;
    }
}