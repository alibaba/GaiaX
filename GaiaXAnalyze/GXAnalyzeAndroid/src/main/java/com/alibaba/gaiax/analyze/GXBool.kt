package com.alibaba.gaiax.analyze

class GXBool() : GXValue() {
    private var value: Boolean? = null

    constructor(value: Boolean) : this() {
        this.value = value
    }

    fun getBool(): Boolean? {
        return value
    }

    override fun getValue(): Any? {
        return value
    }
}