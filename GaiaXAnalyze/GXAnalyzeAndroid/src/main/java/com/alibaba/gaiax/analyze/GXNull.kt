package com.alibaba.gaiax.analyze

public final class GXNull() : GXValue() {

    override fun getValue(): Any? {
        return null
    }
}