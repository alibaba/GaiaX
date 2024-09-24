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
        Log.runE(TAG) { "putLayoutForPrepareView traceId=${gxTemplateContext.traceId} key=${key.hashCode()} value=$value"}
    }

    fun getLayoutForPrepareView(
        gxTemplateContext: GXTemplateContext,
        key: GXTemplateEngine.GXTemplateItem
    ): Layout? {
        Log.runE(TAG) { "getLayoutForPrepareView traceId=${gxTemplateContext.traceId} key=${key.hashCode()}"}
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
        Log.runE(TAG) { "putLayoutForTemplateItem traceId=${gxTemplateContext.traceId} key=${key.hashCode()} value=$value"}
    }

    fun getLayoutForTemplateItem(
        gxTemplateContext: GXTemplateContext,
        key: GXTemplateEngine.GXTemplateItem
    ): Layout? {
        val value = layoutForTemplateItem[key.key(gxTemplateContext.size)]
        Log.runE(TAG) { "getLayoutForTemplateItem traceId=${gxTemplateContext.traceId} key=${key.hashCode()}"}
        return value
    }

    fun isExistForTemplateItem(gxMeasureSize: GXTemplateEngine.GXMeasureSize, key: GXTemplateEngine.GXTemplateItem): Boolean {
        return layoutForTemplateItem.containsKey(key.key(gxMeasureSize))
    }

    companion object {
        private const val TAG = "GXGlobalCache"
        val instance by lazy {
            GXGlobalCache()
        }
    }
}