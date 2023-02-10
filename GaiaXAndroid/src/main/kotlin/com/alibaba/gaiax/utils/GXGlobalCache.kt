package com.alibaba.gaiax.utils

import app.visly.stretch.Layout
import com.alibaba.gaiax.GXTemplateEngine

class GXGlobalCache {

    /**
     * layout cache for preview view
     */
    val layoutFPV: MutableMap<GXTemplateEngine.GXTemplateItem, Layout> =
        mutableMapOf()

    /**
     * layout cache for template item
     */
    val layoutCache: MutableMap<GXTemplateEngine.GXTemplateItem, Layout> = mutableMapOf()

    companion object {
        val instance by lazy {
            GXGlobalCache()
        }
    }
}