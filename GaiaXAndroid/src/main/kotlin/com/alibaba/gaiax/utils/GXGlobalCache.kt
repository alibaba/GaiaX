package com.alibaba.gaiax.utils

import app.visly.stretch.Layout
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext

class GXGlobalCache {
    fun putLayoutForPrepareView(
        gxTemplateContext: GXTemplateContext,
        key: GXTemplateEngine.GXTemplateItem,
        value: Layout
    ) {

        layoutForPrepareView[key.key(gxTemplateContext.size)] = value
        if (Log.isLog()) {
            Log.e(gxTemplateContext.tag, "traceId=${gxTemplateContext.traceId} tag=putLayoutForPrepareView key=${key.hashCode()} value=$value")
        }
    }

    fun getLayoutForPrepareView(
        gxTemplateContext: GXTemplateContext,
        key: GXTemplateEngine.GXTemplateItem
    ): Layout? {
        if (Log.isLog()) {
            Log.e(gxTemplateContext.tag, "traceId=${gxTemplateContext.traceId} tag=getLayoutForPrepareView key=${key.hashCode()}")
        }
        return layoutForPrepareView[key.key(gxTemplateContext.size)]
    }

    fun isExistForPrepareView(
        gxMeasureSize: GXTemplateEngine.GXMeasureSize,
        key: GXTemplateEngine.GXTemplateItem
    ): Boolean {
        return layoutForPrepareView.containsKey(key.key(gxMeasureSize))
    }

    /**
     * layout cache for preview view.
     *
     * the cache will use to reduce computation at create view step.
     */
    private val layoutForPrepareView: MutableMap<String, Layout> = mutableMapOf()

    private val layoutForTemplateItem: MutableMap<String, Layout> = mutableMapOf()

    fun clean() {
        layoutForTemplateItem.clear()
        layoutForPrepareView.clear()
    }

    fun putLayoutForTemplateItem(
        gxTemplateContext: GXTemplateContext,
        key: GXTemplateEngine.GXTemplateItem,
        value: Layout
    ) {
        layoutForTemplateItem[key.key(gxTemplateContext.size)] = value
        if (Log.isLog()) {
            Log.e(gxTemplateContext.tag, "traceId=${gxTemplateContext.traceId} tag=putLayoutForTemplateItem key=${key.key(gxTemplateContext.size)} value=${value}")
        }
    }

    fun getLayoutForTemplateItem(
        gxTemplateContext: GXTemplateContext,
        key: GXTemplateEngine.GXTemplateItem
    ): Layout? {
        val value = layoutForTemplateItem[key.key(gxTemplateContext.size)]
        if (Log.isLog()) {
            Log.e(gxTemplateContext.tag, "traceId=${gxTemplateContext.traceId} tag=getLayoutForTemplateItem key=${key.key(gxTemplateContext.size)} value=${value}")
        }
        return value
    }

    fun isExistForTemplateItem(gxMeasureSize: GXTemplateEngine.GXMeasureSize, key: GXTemplateEngine.GXTemplateItem): Boolean {
        return layoutForTemplateItem.containsKey(key.key(gxMeasureSize))
    }

    companion object {
        val instance by lazy {
            GXGlobalCache()
        }
    }
}