package com.alibaba.gaiax.context

import app.visly.stretch.Layout
import com.alibaba.gaiax.utils.GXLog


fun GXTemplateContext.initLayoutForItemPosition() {
    if (layoutForItemPosition == null) {
        layoutForItemPosition = mutableMapOf()
    }
}

fun GXTemplateContext.clearLayoutForItemPosition() {
    layoutForItemPosition?.clear()
}

fun GXTemplateContext.isExistForItemPosition(key: Any): Boolean {
    return layoutForItemPosition?.containsKey(key) == true
}

fun GXTemplateContext.putLayoutForItemPosition(key: Any, value: Layout) {
    if (GXLog.isLog()) {
        GXLog.e("putLayoutForItemPosition key=$key value=$value")
    }
    layoutForItemPosition?.put(key, value)
}

fun GXTemplateContext.getLayoutForItemPosition(key: Any): Layout? {
    return layoutForItemPosition?.get(key)
}

fun GXTemplateContext.getMaxHeightLayoutForItemPosition(): Layout? {
    return layoutForItemPosition?.maxWithOrNull { a, b ->
        a.value.height.compareTo(b.value.height)
    }?.value
}

