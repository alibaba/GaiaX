/*
 * Copyright (c) 2021, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.gaiax.render.view

import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import app.visly.stretch.Layout
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.basic.GXIImageView
import com.alibaba.gaiax.render.view.basic.GXIconFont
import com.alibaba.gaiax.render.view.basic.GXText
import com.alibaba.gaiax.render.view.basic.GXView
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.render.view.container.GXContainerViewAdapter
import com.alibaba.gaiax.render.view.drawable.GXColorGradientDrawable
import com.alibaba.gaiax.render.view.drawable.GXLinearColorGradientDrawable
import com.alibaba.gaiax.template.*

/**
 * @suppress
 */
fun View.setRoundCornerRadiusAndRoundCornerBorder(style: GXStyle?) {
    // 1. Draw rounded corners, it will crop the View
    // 2. Draws a border with border width, color and rounded corners

    val borderRadius = style?.borderRadius?.value
    val borderWidth = style?.borderWidth?.valueFloat
    val borderColor = style?.borderColor?.value(this.context)
    var cornerRadius = style?.borderRadius?.value

    // Fix a ui bug,
    // if we set corner radius and border radius in the same time, it will produce some defects at the view corner.
    // we need make a special process in order fix the defects.
    if (cornerRadius != null && cornerRadius.size == 8 &&
        borderRadius != null && borderWidth != null && borderColor != null
    ) {
        val tl = cornerRadius[0]
        val tr = cornerRadius[2]
        val bl = cornerRadius[4]
        val br = cornerRadius[6]
        if (tl == tr && tr == bl && bl == br) {
            cornerRadius = FloatArray(8) { tl + 1 }
        }
    }

    if (this is GXIRoundCorner) {
        if (this is GXView) {
            if (cornerRadius != null) {
                this.setRoundCornerRadius(cornerRadius)
            }
            if (borderColor != null && borderWidth != null) {
                this.setRoundCornerBorder(
                    borderColor,
                    borderWidth,
                    borderRadius ?: FloatArray(8) { 0F })
            }
        } else if (this is GXText) {
            if (cornerRadius != null) {
                this.setRoundCornerRadius(cornerRadius)
            }
            if (borderColor != null && borderWidth != null) {
                this.setRoundCornerBorder(
                    borderColor,
                    borderWidth,
                    borderRadius ?: FloatArray(8) { 0F })
            }
        } else if (this is GXIImageView) {
            if (cornerRadius != null) {
                this.setRoundCornerRadius(cornerRadius)
            }
            if (borderColor != null && borderWidth != null) {
                this.setRoundCornerBorder(
                    borderColor,
                    borderWidth,
                    borderRadius ?: FloatArray(8) { 0F })
            }
        } else if (this is GXContainer) {
            if (cornerRadius != null) {
                this.setRoundCornerRadius(cornerRadius)
            }
            if (borderColor != null && borderWidth != null) {
                this.setRoundCornerBorder(
                    borderColor,
                    borderWidth,
                    borderRadius ?: FloatArray(8) { 0F })
            }
        }
    }
}

/**
 * @suppress
 */
fun View.setHidden(display: Int?, hidden: Int?) {
    if (display != null) {
        if (display == View.VISIBLE && hidden == View.INVISIBLE) {
            this.visibility = hidden
        } else if (display == View.VISIBLE && hidden == View.VISIBLE) {
            this.visibility = hidden
        } else {
            this.visibility = display
        }
    } else {
        hidden?.let { this.visibility = it }
    }
}

/**
 * @suppress
 */
fun View.setOpacity(opacity: Float?) {
    opacity?.let {
        this.alpha = opacity
    }
}

/**
 * @suppress
 */
fun View.setOverflow(overflow: Boolean?) {
    if (this is ViewGroup) {
        overflow?.let { this.clipChildren = it }
    }
}

/**
 * @suppress
 */
fun View.setBackgroundColorAndBackgroundImageWithRadius(style: GXStyle?) {
    if (style?.backgroundImage != null) {
        if (this is GXText) {
            // GXText needs to be handled separately
            // @see GXViewExt.setFontBackgroundImage
        } else {
            val drawable = style.backgroundImage.createDrawable()
            this.background = drawable
        }
    } else if (style?.backgroundColor != null) {
        val drawable = GXColorGradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                style.backgroundColor.value(this.context),
                style.backgroundColor.value(this.context)
            )
        )
        // Use left and right gradients to simulate solid colors
        // Convenient for rounded corner cutting
        this.background = drawable
    } else {
        this.background = null
    }

    if (background is GXColorGradientDrawable || background is GXLinearColorGradientDrawable) {
        (background as GradientDrawable).cornerRadii = style?.borderRadius?.value
    }
}

/**
 * @suppress
 */
fun GXText.setFontTextDecoration(textDecoration: Int?) {
    if (textDecoration != null) {
        this.paint.flags = textDecoration
    }
}

/**
 * @suppress
 */
fun GXText.setFontTextLineHeight(style: GXStyle) {
    if (style.fontLineHeight != null) {
        val lineHeight = style.fontLineHeight.valueFloat

        GXRegisterCenter
            .instance
            .extensionDynamicProperty
            ?.convert(
                GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                    GXTemplateKey.STYLE_FONT_LINE_HEIGHT,
                    lineHeight
                ).apply {
                    this.cssStyle = style
                })
            ?.let {
                this.setTextLineHeight(it as Float)
                return
            }

        this.setTextLineHeight(lineHeight)
    }
}

/**
 * @suppress
 */
fun GXText.setFontTextAlign(style: GXStyle) {
    if (style.fontTextAlign != null) {
        // Center up and down by default
        setTextAlign(Gravity.CENTER_VERTICAL.or(style.fontTextAlign))
    } else {
        // Center up and down by default
        setTextAlign(Gravity.CENTER_VERTICAL.or(Gravity.LEFT))
    }
}

/**
 * @suppress
 */
fun GXText.setTextLineHeight(lineHeight: Float) {
    val fontHeight: Int = paint.getFontMetricsInt(null)
    if (lineHeight.toInt() != fontHeight) {
        setLineSpacing(lineHeight - fontHeight.toFloat(), 1f)
    }
}

/**
 * @suppress
 */
fun GXText.setFontTextOverflow(style: GXStyle) {
    if (style.fontTextOverflow == null) {
        this.ellipsize = null
    } else {
        this.ellipsize = style.fontTextOverflow
    }
}

/**
 * @suppress
 */
fun GXText.setTextAlign(gravity: Int) {
    this.gravity = gravity
}

/**
 * @suppress
 */
fun GXText.setFontLines(fontLiens: Int?) {
    if (fontLiens == null || fontLiens == 1) {
        this.setSingleLine(true)
    } else if (fontLiens == 0) {
        this.maxLines = Int.MAX_VALUE
    } else {
        this.maxLines = fontLiens
    }
}

/**
 * @suppress
 */
fun GXText.setFontColor(style: GXStyle) {
    if (style.fontColor != null) {
        this.setTextColor(style.fontColor.value(this.context))
    } else {
        // The default color is black
        this.setTextColor(Color.BLACK)
    }
}

/**
 * @suppress
 */
fun GXText.setFontBackgroundImage(backgroundImage: GXLinearColor?) {
    if (backgroundImage != null) {
        // The default color is black
        this.setTextColor(Color.BLACK)
        this.paint.shader = backgroundImage.createShader(this)
    }
}

/**
 * @suppress
 */
fun GXText.setFontFamilyAndFontWeight(style: GXStyle) {
    if (style.fontFamily != null) {
        this.typeface = style.fontFamily
    } else if (style.fontWeight != null) {
        this.typeface = style.fontWeight
    } else {
        this.typeface = null
    }

    // check
    if (this is GXIconFont) {
        if (GXRegisterCenter.instance.extensionCompatibility?.isPreventIconFontTypefaceThrowException() == true) {
            this.typeface =
                GXStyleConvert.instance.fontFamily(GXTemplateKey.GAIAX_ICONFONT_FONT_FAMILY_DEFAULT_NAME)
        } else {
            if (style.fontFamily == null) {
                throw IllegalArgumentException("GXIconFont view must have font family property")
            }
        }
    }
}

/**
 * @suppress
 */
fun GXText.setFontSize(fontSize: Float?) {
    if (fontSize != null && fontSize >= 0) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
    }
}

/**
 * @suppress
 */
fun GXText.setFontPadding(padding: app.visly.stretch.Rect<GXSize>?) {
    this.setPadding(
        padding?.start?.valueInt ?: 0,
        padding?.top?.valueInt ?: 0,
        padding?.end?.valueInt ?: 0,
        padding?.bottom?.valueInt ?: 0
    )
}

/**
 * @suppress
 */
fun GXText.setTextFontFamily(fontFamily: Typeface?) {
    this.typeface = fontFamily
}

/**
 * @suppress
 */
fun View.setDisplay(visibility: Int?) {
    visibility?.let {
        this.visibility = it
    }
}

/**
 * @suppress
 */
fun View.setGridContainerDirection(
    context: GXTemplateContext,
    config: GXGridConfig,
    layout: Layout?
) {
    val direction: Int = config.direction
    val column: Int = config.column(context)
    val scrollEnable: Boolean = config.scrollEnable
    if (this is RecyclerView) {
        val needForceRefresh =
            (this.adapter as? GXContainerViewAdapter)?.isNeedForceRefresh(layout?.width ?: 0F)
                ?: false
        if (this.layoutManager == null || needForceRefresh) {
            this.layoutManager = null
            val target = object : GridLayoutManager(this.context, column, direction, false) {
                override fun canScrollHorizontally(): Boolean {
                    // TODO: Grid横向处理不支持，此种情况暂时不做处理，很少见
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return direction == LinearLayoutManager.VERTICAL && scrollEnable
                }
            }
            this.layoutManager = target
        } else {
            (this.layoutManager as GridLayoutManager).spanCount = column
        }
    }
}

/**
 * @suppress
 */
fun View.setScrollContainerDirection(direction: Int, layout: Layout?) {
    if (this is RecyclerView) {
        val needForceRefresh =
            (this.adapter as? GXContainerViewAdapter)?.isNeedForceRefresh(layout?.width ?: 0F)
                ?: false
        if (this.layoutManager == null || needForceRefresh) {
            this.layoutManager = null
            val target = LinearLayoutManager(
                this.context,
                direction,
                false
            )
            this.layoutManager = target
        }
    }
}

/**
 * @suppress
 */
fun View.setScrollContainerPadding(padding: Rect) {
    if (this is RecyclerView) {
        this.setPadding(padding.left, padding.top, padding.right, padding.bottom)
    }
}

/**
 * @suppress
 */
fun View.setGridContainerItemSpacingAndRowSpacing(
    padding: Rect,
    itemSpacing: Int,
    rowSpacing: Int
) {
    if (this is RecyclerView) {
        if (this.itemDecorationCount > 0) {
            return
        }
        val decoration = object : RecyclerView.ItemDecoration() {

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)

                val manager: GridLayoutManager = parent.layoutManager as GridLayoutManager
                val childPosition: Int = parent.getChildAdapterPosition(view)
                val itemCount: Int = parent.adapter?.itemCount ?: 0
                val spanCount = manager.spanCount
                val orientation = manager.orientation

                val containerHeight = parent.layoutParams.height
                val itemHeight = view.layoutParams.height

                // 在一行的状态下，需要特殊处理上下居中的问题
                if (orientation == GridLayoutManager.VERTICAL && (itemCount * 1.0F / spanCount) <= 1 && itemHeight > 0 && containerHeight > 0) {
                    val heightDiff = containerHeight - itemHeight
                    val value = (heightDiff / 2.0F).toInt()
                    val finalPadding = Rect(padding.left, value, padding.right, value)
                    setSingleGridOffset(
                        itemSpacing.toFloat(),
                        finalPadding,
                        orientation,
                        spanCount,
                        outRect,
                        childPosition,
                        itemCount
                    )
                } else {
                    setGridOffset(
                        itemSpacing.toFloat(),
                        rowSpacing.toFloat(),
                        padding,
                        orientation,
                        spanCount,
                        outRect,
                        childPosition,
                        itemCount
                    )
                }
            }

            private fun setSingleGridOffset(
                itemSpacing: Float,
                padding: Rect,
                orientation: Int,
                spanCount: Int,
                outRect: Rect,
                childPosition: Int,
                itemCount: Int
            ) {
                val left: Float
                val right: Float

                val totalSpace: Float =
                    itemSpacing * (spanCount - 1) + (padding.left.toFloat() + padding.right.toFloat()) // 总共的padding值
                val eachSpace = totalSpace / spanCount // 分配给每个item的padding值
                val column = childPosition % spanCount // 列数

                val top: Float = padding.top.toFloat()  // 默认 top为0
                val bottom: Float = padding.bottom.toFloat() // 默认bottom为间距值

                if (padding.left == 0 && padding.right == 0 && padding.top == 0 && padding.bottom == 0) {
                    left = column * eachSpace / (spanCount - 1)
                    right = eachSpace - left
                } else {
                    left =
                        column * (eachSpace - padding.left - padding.right) / (spanCount - 1) + (padding.left + padding.right) / 2
                    right = eachSpace - left
                }

                outRect.left = left.toInt()
                outRect.top = top.toInt()
                outRect.right = right.toInt()
                outRect.bottom = bottom.toInt()
            }

            private fun setGridOffset(
                itemSpacing: Float,
                rowSpacing: Float,
                padding: Rect,
                orientation: Int,
                spanCount: Int,
                outRect: Rect,
                childPosition: Int,
                itemCount: Int
            ) {
                var left: Float
                var right: Float
                var top: Float
                var bottom: Float

                if (orientation == GridLayoutManager.VERTICAL) {

                    // 横向的总空间
                    // 总共的padding值
                    val totalSpace: Float =
                        itemSpacing * (spanCount - 1) + (padding.left.toFloat() + padding.right.toFloat())

                    // 分配给每个item的padding值
                    val eachSpace = totalSpace / spanCount

                    // 列数
                    val column = childPosition % spanCount

                    // 行数
                    val row = childPosition / spanCount

                    // 默认 top为0
                    top = 0f

                    // 默认 rawSpacing为边距值
                    bottom = rowSpacing

                    if (padding.left == 0 && padding.right == 0 && padding.top == 0 && padding.bottom == 0) {
                        left = column * eachSpace / (spanCount - 1)
                        right = eachSpace - left
                        // 无边距的话  只有最后一行bottom为0
                        if (itemCount / spanCount == row) {
                            bottom = 0f
                        }
                    } else {
                        if (childPosition < spanCount) {
                            if (padding.top != 0) {
                                // 有边距的话 第一行top为边距值
                                top = padding.top.toFloat()
                            }
                        } else if (itemCount / spanCount == row) {
                            if (padding.bottom != 0) {
                                // 有边距的话 最后一行bottom为边距值
                                bottom = padding.bottom.toFloat()
                            }
                        }
                        left =
                            column * (eachSpace - padding.left - padding.right) / (spanCount - 1) + (padding.left + padding.right) / 2
                        right = eachSpace - left
                    }
                } else {
                    val totalSpace: Float =
                        itemSpacing * (spanCount - 1) + (padding.top + padding.bottom) // 总共的padding值
                    val eachSpace = totalSpace / spanCount // 分配给每个item的padding值
                    val column = childPosition % spanCount // 列数
                    val row = childPosition / spanCount // 行数

                    // orientation == GridLayoutManager.HORIZONTAL 跟上面的大同小异, 将top,bottom替换为left,right即可
                    left = 0f

                    right = rowSpacing

                    if (padding.left == 0 && padding.right == 0 && padding.top == 0 && padding.bottom == 0) {
                        top = column * eachSpace / (spanCount - 1)
                        bottom = eachSpace - top
                        if (itemCount / spanCount == row) {
                            right = 0f
                        }
                    } else {
                        if (childPosition < spanCount) {
                            if (padding.left != 0) {
                                left = padding.left.toFloat()
                            }
                        } else if (itemCount / spanCount == row) {
                            if (padding.right != 0) {
                                right = padding.right.toFloat()
                            }
                        }
                        top =
                            column * (eachSpace - padding.top - padding.bottom) / (spanCount - 1) + (padding.top + padding.bottom) / 2
                        bottom = eachSpace - top
                    }
                }
                outRect.left = left.toInt()
                outRect.top = top.toInt()
                outRect.right = right.toInt()
                outRect.bottom = bottom.toInt()
            }
        }
        this.addItemDecoration(decoration)
    }
}

/**
 * @suppress
 */
fun View.setVerticalScrollContainerLineSpacing(lineSpacing: Int) {
    if (this is RecyclerView) {
        if (this.itemDecorationCount > 0) {
            return
        }
        val decoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                if (parent.adapter != null) {
                    val childLayoutPosition = parent.getChildLayoutPosition(view)
                    if (childLayoutPosition != (parent.adapter!!.itemCount - 1)) {
                        outRect.bottom = lineSpacing
                    }
                }
            }
        }
        this.addItemDecoration(decoration)
    }
}

/**
 * @suppress
 */
fun View.setHorizontalScrollContainerLineSpacing(lineSpacing: Int) {
    if (this is RecyclerView) {
        if (this.itemDecorationCount > 0) {
            return
        }
        val decoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                if (parent.adapter != null) {
                    val childLayoutPosition = parent.getChildLayoutPosition(view)
                    if (childLayoutPosition != (parent.adapter!!.itemCount - 1)) {
                        outRect.right = lineSpacing
                    }
                }
            }
        }
        this.addItemDecoration(decoration)
    }
}

/**
 * @suppress
 */
fun View.setHorizontalScrollContainerLineSpacing(left: Int, right: Int, lineSpacing: Int) {
    if (this is RecyclerView) {
        if (this.itemDecorationCount > 0) {
            return
        }
        val decoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                if (parent.adapter != null) {
                    when (parent.getChildLayoutPosition(view)) {
                        0 -> {
                            outRect.left = left
                            outRect.right = lineSpacing
                        }
                        parent.adapter!!.itemCount - 1 -> {
                            outRect.right = right
                        }
                        else -> {
                            outRect.right = lineSpacing
                        }
                    }
                }
            }
        }
        this.addItemDecoration(decoration)
    }
}

fun View.setSpanSizeLookup() {
    if (this is RecyclerView && this.layoutManager is GridLayoutManager) {
        val adapterSize = this.adapter?.itemCount ?: 1
        val column = (this.layoutManager as GridLayoutManager).spanCount
        (this.layoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(currentPosition: Int): Int {
                    return if (adapterSize - 1 == currentPosition) {
                        column
                    } else {
                        1
                    }
                }
            }
    }
}