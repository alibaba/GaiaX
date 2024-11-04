package com.alibaba.gaiax.context

import app.visly.stretch.Layout
import com.alibaba.gaiax.render.node.GXNode

fun GXTemplateContext.clearLayout() {
    sliderItemLayoutCache?.clear()
    gridItemLayoutCache?.clear()
    scrollItemLayoutCache?.clear()
}

fun GXTemplateContext.getMaxHeightLayoutForScroll(): Layout? {
    return scrollItemLayoutCache?.maxWithOrNull { a, b -> a.value.height.compareTo(b.value.height) }?.value
}

fun GXTemplateContext.getMinHeightLayoutForScroll(): Layout? {
    return scrollItemLayoutCache?.minWithOrNull { a, b -> a.value.height.compareTo(b.value.height) }?.value
}


