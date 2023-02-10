package com.alibaba.gaiax.context

import app.visly.stretch.Layout
import com.alibaba.gaiax.utils.GXGlobalCache


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
    layoutCache?.put(key, value)

    // 对于容器的子模板，此处计算的结果可以直接利用
    if (!GXGlobalCache.instance.layoutFPV.containsKey(this.templateItem)) {
        GXGlobalCache.instance.layoutFPV[this.templateItem] = value
    }
}

fun GXTemplateContext.getLayoutCache(key: Any): Layout? {
    return layoutCache?.get(key)
}

fun GXTemplateContext.getMaxHeightLayoutOfLayoutCache(): Layout? {
    return layoutCache?.maxWithOrNull { a, b ->
        a.value.height.compareTo(b.value.height)
    }?.value
}

