package com.alibaba.gaiax.utils

import app.visly.stretch.Layout
import com.alibaba.gaiax.GXTemplateEngine

class GXCache {

    val layoutCacheForPrepareView: MutableMap<GXTemplateEngine.GXTemplateItem, Layout> =
        mutableMapOf()

    val layoutCacheForSingleType: MutableMap<GXTemplateEngine.GXTemplateItem, Layout> =
        mutableMapOf()

    companion object {
        val instance by lazy {
            GXCache()
        }
    }
}