package com.alibaba.gaiax.utils

import app.visly.stretch.Layout
import com.alibaba.gaiax.GXTemplateEngine

class GXGlobalCache {

    fun putLayoutForPrepareView(key: GXTemplateEngine.GXTemplateItem, value: Layout) {
        layoutForPrepareView[key.getKey] = value
    }

    fun getLayoutForPrepareView(key: GXTemplateEngine.GXTemplateItem): Layout? {
        return layoutForPrepareView[key.getKey]
    }

    fun isExistForPrepareView(key: GXTemplateEngine.GXTemplateItem): Boolean {
        return layoutForPrepareView.containsKey(key.getKey)
    }

    private val layoutForPrepareView: MutableMap<String, Layout> = mutableMapOf()

    private val layoutForTemplateItem: MutableMap<String, Layout> = mutableMapOf()

    private val immutableTemplateItem: MutableSet<String> = mutableSetOf()

    fun isImmutableTemplate(key: GXTemplateEngine.GXTemplateItem): Boolean {
        return immutableTemplateItem.contains(key.getKey)
    }

    fun clear() {
        layoutForTemplateItem.clear()
        layoutForPrepareView.clear()
        immutableTemplateItem.clear()
    }

    fun putLayoutForTemplateItem(key: GXTemplateEngine.GXTemplateItem, value: Layout) {
        layoutForTemplateItem[key.getKey] = value
    }

    fun getLayoutForTemplateItem(key: GXTemplateEngine.GXTemplateItem): Layout? {
        return layoutForTemplateItem[key.getKey]
    }

    fun isExistForTemplateItem(key: GXTemplateEngine.GXTemplateItem): Boolean {
        return layoutForTemplateItem.containsKey(key.getKey)
    }

    fun addImmutableTemplate(key: GXTemplateEngine.GXTemplateItem) {
        immutableTemplateItem.add(key.getKey)
    }

    companion object {
        val instance by lazy {
            GXGlobalCache()
        }
    }
}