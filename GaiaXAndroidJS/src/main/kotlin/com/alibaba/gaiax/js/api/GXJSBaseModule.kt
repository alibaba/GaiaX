package com.alibaba.gaiax.js.api

import androidx.annotation.Keep
import com.alibaba.gaiax.js.utils.IdGenerator

@Keep
abstract class GXJSBaseModule : IGXModule {

    private val _id: Long by lazy {
        IdGenerator.genLongId()
    }

    override val id: Long
        get() = _id
}