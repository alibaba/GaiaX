package com.alibaba.gaiax.utils

import app.visly.stretch.Layout
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.render.node.GXNode

class GXGlobalCache {
    fun putLayoutForPrepareView(key: GXTemplateEngine.GXTemplateItem, value: Layout) {
        layoutForPrepareView[key.key()] = value
        if (GXLog.isLog()) {
            GXLog.e("putLayoutForPrepareView key=${key.hashCode()} value=$value")
        }
    }

    fun getLayoutForPrepareView(key: GXTemplateEngine.GXTemplateItem): Layout? {
        return layoutForPrepareView[key.key()]
    }

    fun isExistForPrepareView(key: GXTemplateEngine.GXTemplateItem): Boolean {
        return layoutForPrepareView.containsKey(key.key())
    }

    /**
     * layout cache for preview view.
     *
     * the cache will use to reduce computation at create view step.
     */
    private val layoutForPrepareView: MutableMap<String, Layout> =
        mutableMapOf()

    private val layoutForTemplateItem: MutableMap<String, Layout> =
        mutableMapOf()

    fun clean() {
        layoutForTemplateItem.clear()
        layoutForPrepareView.clear()
    }

    fun putLayoutForTemplateItem(key:  GXTemplateEngine.GXTemplateItem, value: Layout) {
        layoutForTemplateItem[key.key()] = value
    }

    fun getLayoutForTemplateItem(key:  GXTemplateEngine.GXTemplateItem): Layout? {
        return layoutForTemplateItem[key.key()]
    }

    fun isExistForTemplateItem(key:  GXTemplateEngine.GXTemplateItem): Boolean {
        return layoutForTemplateItem.containsKey(key.key())
    }

    companion object {
        val instance by lazy {
            GXGlobalCache()
        }
    }
}