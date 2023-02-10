package com.alibaba.gaiax.context

import app.visly.stretch.Layout
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.utils.GXLog

fun GXTemplateContext.initLayoutForScroll() {
    if (scrollItemLayoutCache == null) {
        scrollItemLayoutCache = mutableMapOf()
    }
}

fun GXTemplateContext.clearLayoutForScroll() {
    scrollItemLayoutCache?.clear()
}

fun GXTemplateContext.isExistForScroll(key: Any): Boolean {
    return scrollItemLayoutCache?.containsKey(key) == true
}

fun GXTemplateContext.putLayoutForScroll(key: Any, value: Layout) {
    if (GXLog.isLog()) {
        GXLog.e("putLayoutForScroll key=$key value=$value")
    }
    scrollItemLayoutCache?.put(key, value)
}

fun GXTemplateContext.getLayoutForScroll(key: Any): Layout? {
    return scrollItemLayoutCache?.get(key)
}

fun GXTemplateContext.getMaxHeightLayoutForScroll(): Layout? {
    return scrollItemLayoutCache?.maxWithOrNull { a, b ->
        a.value.height.compareTo(b.value.height)
    }?.value
}


fun GXTemplateContext.initNodeForScroll() {
    if (scrollNodeCache == null) {
        scrollNodeCache = mutableMapOf()
    }
}

fun GXTemplateContext.isExistNodeForScroll(key: Any): Boolean {
    return scrollNodeCache?.containsKey(key) ?: false
}

fun GXTemplateContext.obtainNodeForScroll(key: Any): GXNode? {
    val value = scrollNodeCache?.remove(key)
    if (GXLog.isLog()) {
        GXLog.e("obtainNodeForScroll key=${key} value=${value}")
    }
    return value
}

fun GXTemplateContext.putNodeForScroll(key: Any, value: GXNode) {
    scrollNodeCache?.put(key, value)
    if (GXLog.isLog()) {
        GXLog.e("putNodeForScroll key=${key} value=${value}")
    }
}