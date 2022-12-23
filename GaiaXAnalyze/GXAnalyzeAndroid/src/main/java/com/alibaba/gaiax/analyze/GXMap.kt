package com.alibaba.gaiax.analyze

class GXMap() : GXValue() {
    private var value: Any? = null

    constructor(value: Any?) : this() {
        this.value = value
    }

    override fun getValue(): Any? {
        return value
    }
}