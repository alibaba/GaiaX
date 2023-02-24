package com.alibaba.gaiax.utils

import app.visly.stretch.Layout
import com.alibaba.gaiax.GXTemplateEngine

class GXGlobalCache {
    fun putLayoutForPrepareView(key: GXTemplateEngine.GXTemplateItem, value: Layout) {
        layoutForPrepareView[key] = value
        if (GXLog.isLog()) {
            GXLog.e("putLayoutForPrepareView key=${key.hashCode()} value=$value")
        }
    }

    fun getLayoutForPrepareView(key: GXTemplateEngine.GXTemplateItem): Layout? {
        return layoutForPrepareView[key]
    }

    fun isExistForPrepareView(key: GXTemplateEngine.GXTemplateItem): Boolean {
        return layoutForPrepareView.containsKey(key)
    }

    /**
     * layout cache for preview view.
     *
     * the cache will use to reduce computation at create view step.
     */
    private val layoutForPrepareView: MutableMap<GXTemplateEngine.GXTemplateItem, Layout> =
        mutableMapOf()

    private val layoutForTemplateItem: MutableMap<GXTemplateEngine.GXTemplateItem, Layout> =
        mutableMapOf()

    fun clear() {
        layoutForTemplateItem.clear()
        layoutForPrepareView.clear()
    }

    fun putLayoutForTemplateItem(key: GXTemplateEngine.GXTemplateItem, value: Layout) {
        layoutForTemplateItem[key] = value
    }

    fun getLayoutForTemplateItem(key: GXTemplateEngine.GXTemplateItem): Layout? {
        return layoutForTemplateItem[key]
    }

    fun isExistForTemplateItem(key: GXTemplateEngine.GXTemplateItem): Boolean {
        return layoutForTemplateItem.containsKey(key)
    }

    companion object {
        val instance by lazy {
            GXGlobalCache()
        }
    }
}