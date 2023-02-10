package com.alibaba.gaiax.context

import app.visly.stretch.Layout
import com.alibaba.gaiax.utils.GXLog


fun GXTemplateContext.initLayoutCache() {
    if (layoutCache == null) {
        layoutCache = mutableMapOf()
    }
}

fun GXTemplateContext.clearLayoutCache() {
    layoutCache?.clear()
}

fun GXTemplateContext.isExistOfLayoutCache(key: Any): Boolean {
    return layoutCache?.containsKey(key) == true
}

fun GXTemplateContext.putLayoutCache(key: Any, value: Layout) {
    if (GXLog.isLog()) {
        GXLog.e("putLayoutCache key=$key value=$value")
    }
    layoutCache?.put(key, value)
}

fun GXTemplateContext.getLayoutCache(key: Any): Layout? {
    return layoutCache?.get(key)
}

fun GXTemplateContext.getMaxHeightLayoutOfLayoutCache(): Layout? {
    return layoutCache?.maxWithOrNull { a, b ->
        a.value.height.compareTo(b.value.height)
    }?.value
}

