package com.alibaba.gaiax.js.utils

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

object IdGenerator {

    private var index: AtomicLong = AtomicLong(1L)

    fun genLongId(): Long {
        return index.getAndIncrement()
    }

    internal fun resetLongId() {
        index = AtomicLong(1L)
    }

    private var index2: AtomicInteger = AtomicInteger(1)

    fun genIntId(): Int {
        return index2.getAndIncrement()
    }

    internal fun resetIntId() {
        index2 = AtomicInteger(1)
    }

    internal fun reset() {
        resetIntId()
        resetLongId()
    }

}