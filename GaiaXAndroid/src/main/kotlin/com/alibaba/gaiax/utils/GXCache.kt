package com.alibaba.gaiax.utils

import app.visly.stretch.Layout
import com.alibaba.gaiax.GXTemplateEngine

class GXCache {

    val layoutTreeCache: MutableMap<GXTemplateEngine.GXTemplateItem, Layout> = mutableMapOf()

    companion object {
        val instance by lazy {
            GXCache()
        }
    }
}