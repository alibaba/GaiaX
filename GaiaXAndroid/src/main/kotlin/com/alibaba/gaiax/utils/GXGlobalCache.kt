package com.alibaba.gaiax.utils

import app.visly.stretch.Layout
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.render.node.GXNode

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

    fun isExistNodeForItemPosition(key: Any): Boolean {
        return nodeForItemPosition.containsKey(key)
    }

    fun obtainNodeForItemPosition(key: Any): GXNode? {
        val value = nodeForItemPosition.remove(key)
        if (GXLog.isLog()) {
            GXLog.e("obtainNodeForItemPosition key=${key} value=${value}")
        }
        return value
    }

    fun putNodeForItemPosition(key: Any, value: GXNode) {
        nodeForItemPosition[key] = value
        if (GXLog.isLog()) {
            GXLog.e("putNodeForItemPosition key=${key} value=${value}")
        }
    }

    /**
     * layout cache for preview view.
     *
     * the cache will use to reduce computation at create view step.
     */
    private val layoutForPrepareView: MutableMap<GXTemplateEngine.GXTemplateItem, Layout> =
        mutableMapOf()

    /**
     * node cache for item position
     */
    private val nodeForItemPosition: MutableMap<Any, GXNode> = mutableMapOf()

    companion object {
        val instance by lazy {
            GXGlobalCache()
        }
    }
}