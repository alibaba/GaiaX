package com.alibaba.gaiax.analyze

public final class GXNull(pointer: Long) : GXValue(pointer) {

    override fun getValue(): Any? {
        return null
    }
}