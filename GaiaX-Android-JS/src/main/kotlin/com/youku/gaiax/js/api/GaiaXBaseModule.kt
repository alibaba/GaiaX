package com.youku.gaiax.js.api

import androidx.annotation.Keep
import com.youku.gaiax.js.utils.IdGenerator

@Keep
abstract class GaiaXBaseModule : IGaiaXModule {

    private val _id: Long by lazy {
        IdGenerator.genLongId()
    }

    override val id: Long
        get() = _id
}