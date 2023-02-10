package com.alibaba.gaiax.context

import app.visly.stretch.Layout
import com.alibaba.gaiax.utils.GXLog


fun GXTemplateContext.initLayoutForScroll() {
    if (layoutForScroll == null) {
        layoutForScroll = mutableMapOf()
    }
}

fun GXTemplateContext.clearLayoutForScroll() {
    layoutForScroll?.clear()
}

fun GXTemplateContext.isExistForScroll(key: Any): Boolean {
    return layoutForScroll?.containsKey(key) == true
}

fun GXTemplateContext.putLayoutForScroll(key: Any, value: Layout) {
    if (GXLog.isLog()) {
        GXLog.e("putLayoutForScroll key=$key value=$value")
    }
    layoutForScroll?.put(key, value)
}

fun GXTemplateContext.getLayoutForScroll(key: Any): Layout? {
    return layoutForScroll?.get(key)
}

fun GXTemplateContext.getMaxHeightLayoutForScroll(): Layout? {
    return layoutForScroll?.maxWithOrNull { a, b ->
        a.value.height.compareTo(b.value.height)
    }?.value
}

