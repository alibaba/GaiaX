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

    fun isExistForTemplateItem(key: GXTemplateEngine.GXTemplateItem): Boolean {
        return layoutForTemplateItem.containsKey(key)
    }

    fun clearLayoutForTemplateItem() {
        layoutForTemplateItem.clear()
    }

    fun getLayoutForTemplateItem(key: GXTemplateEngine.GXTemplateItem): Layout? {
        return layoutForTemplateItem[key]
    }

    fun putLayoutForTemplateItem(key: GXTemplateEngine.GXTemplateItem, value: Layout) {
        layoutForTemplateItem[key] = value
        if (GXLog.isLog()) {
            GXLog.e("putLayoutForTemplateItem key=${key.hashCode()} value=$value")
        }
    }

    /**
     * layout cache for preview view
     */
    private val layoutForPrepareView: MutableMap<GXTemplateEngine.GXTemplateItem, Layout> =
        mutableMapOf()

    /**
     * layout cache for template item
     */
    private val layoutForTemplateItem: MutableMap<GXTemplateEngine.GXTemplateItem, Layout> =
        mutableMapOf()

    companion object {
        val instance by lazy {
            GXGlobalCache()
        }
    }
}