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

package com.alibaba.gaiax.template

import app.visly.stretch.*
import com.alibaba.fastjson.JSONObject

/**
 * @suppress
 */
object GXFlexBoxConvert {

    fun size(cssJson: JSONObject): Size<GXSize>? {
        val width = cssJson.getString(GXTemplateKey.FLEXBOX_SIZE_WIDTH)
        val height = cssJson.getString(GXTemplateKey.FLEXBOX_SIZE_HEIGHT)
        return if (width != null && height != null) {
            Size(GXSize.create(width), GXSize.create(height))
        } else if (width != null && height == null) {
            Size(GXSize.create(width), GXSize.Auto)
        } else if (height != null && width == null) {
            Size(GXSize.Auto, GXSize.create(height))
        } else {
            null
        }
    }

    fun minSize(cssJson: JSONObject): Size<GXSize>? {
        val width = cssJson.getString(GXTemplateKey.FLEXBOX_MIN_WIDTH)
        val height = cssJson.getString(GXTemplateKey.FLEXBOX_MIN_HEIGHT)
        return if (width != null && height != null) {
            Size(GXSize.create(width), GXSize.create(height))
        } else if (width != null && height == null) {
            Size(GXSize.create(width), GXSize.Auto)
        } else if (height != null && width == null) {
            Size(GXSize.Auto, GXSize.create(height))
        } else {
            null
        }
    }

    fun maxSize(cssJson: JSONObject): Size<GXSize>? {
        val width = cssJson.getString(GXTemplateKey.FLEXBOX_MAX_WIDTH)
        val height = cssJson.getString(GXTemplateKey.FLEXBOX_MAX_HEIGHT)
        return if (width != null && height != null) {
            Size(GXSize.create(width), GXSize.create(height))
        } else if (width != null && height == null) {
            Size(GXSize.create(width), GXSize.Auto)
        } else if (height != null && width == null) {
            Size(GXSize.Auto, GXSize.create(height))
        } else {
            null
        }
    }

    fun display(cssJson: JSONObject): Display? =
        cssJson.getString(GXTemplateKey.FLEXBOX_DISPLAY)?.let { return display(it) }

    fun display(target: String): Display? = when (target) {
        "flex" -> Display.Flex
        "none" -> Display.None
        else -> null
    }

    fun positionType(cssJson: JSONObject): PositionType? =
        cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_TYPE)?.let { return positionType(it) }

    fun positionType(target: String): PositionType? = when (target) {
        "relative" -> PositionType.Relative
        "absolute" -> PositionType.Absolute
        else -> null
    }

    fun direction(cssJson: JSONObject): Direction? =
        cssJson.getString(GXTemplateKey.FLEXBOX_DIRECTION)?.let { return direction(it) }

    fun direction(target: String): Direction? = when (target) {
        "ltr" -> Direction.LTR
        "absolute" -> Direction.RTL
        "inherit" -> Direction.Inherit
        else -> null
    }

    fun flexDirection(cssJson: JSONObject): FlexDirection? =
        cssJson.getString(GXTemplateKey.FLEXBOX_FLEX_DIRECTION)?.let { return flexDirection(it) }

    fun flexDirection(target: String): FlexDirection? = when (target) {
        "row" -> FlexDirection.Row
        "column" -> FlexDirection.Column
        "column-reverse" -> FlexDirection.ColumnReverse
        "row-reverse" -> FlexDirection.RowReverse
        else -> null
    }

    fun flexWrap(cssJson: JSONObject): FlexWrap? =
        cssJson.getString(GXTemplateKey.FLEXBOX_FLEX_WRAP)?.let { return flexWrap(it) }

    fun flexWrap(target: String): FlexWrap? = when (target) {
        "nowrap" -> FlexWrap.NoWrap
        "wrap" -> FlexWrap.Wrap
        "wrap-reverse" -> FlexWrap.WrapReverse
        else -> null
    }

    fun overflow(cssJson: JSONObject): Overflow? =
        cssJson.getString(GXTemplateKey.FLEXBOX_OVERFLOW)?.let { return overflow(it) }

    fun overflow(target: String): Overflow? = when (target) {
        "visible" -> Overflow.Visible
        "hidden" -> Overflow.Hidden
        "scroll" -> Overflow.Scroll
        else -> null
    }

    fun alignItems(cssJson: JSONObject): AlignItems? =
        cssJson.getString(GXTemplateKey.FLEXBOX_ALIGN_ITEMS)?.let { return alignItems(it) }

    fun alignItems(target: String): AlignItems? = when (target) {
        "flex-start" -> AlignItems.FlexStart
        "flex-end" -> AlignItems.FlexEnd
        "center" -> AlignItems.Center
        "baseline" -> AlignItems.Baseline
        "stretch" -> AlignItems.Stretch
        else -> null
    }

    fun alignSelf(cssJson: JSONObject): AlignSelf? =
        cssJson.getString(GXTemplateKey.FLEXBOX_ALIGN_SELF)?.let { return alignSelf(it) }

    fun alignSelf(target: String): AlignSelf? = when (target) {
        "auto" -> AlignSelf.Auto
        "flex-start" -> AlignSelf.FlexStart
        "flex-end" -> AlignSelf.FlexEnd
        "center" -> AlignSelf.Center
        "baseline" -> AlignSelf.Baseline
        "stretch" -> AlignSelf.Stretch
        else -> null
    }

    fun alignContent(cssJson: JSONObject): AlignContent? =
        cssJson.getString(GXTemplateKey.FLEXBOX_ALIGN_CONTENT)?.let {
            return alignContent(it)
        }

    fun alignContent(target: String): AlignContent? = when (target) {
        "flex-start" -> AlignContent.FlexStart
        "flex-end" -> AlignContent.FlexEnd
        "center" -> AlignContent.Center
        "space-around" -> AlignContent.SpaceAround
        "space-between" -> AlignContent.SpaceBetween
        "stretch" -> AlignContent.Stretch
        else -> null
    }

    fun justifyContent(cssJson: JSONObject): JustifyContent? =
        cssJson.getString(GXTemplateKey.FLEXBOX_JUSTIFY_CONTENT)?.let { return justifyContent(it) }

    fun justifyContent(target: String): JustifyContent? = when (target) {
        "flex-start" -> JustifyContent.FlexStart
        "flex-end" -> JustifyContent.FlexEnd
        "center" -> JustifyContent.Center
        "space-around" -> JustifyContent.SpaceAround
        "space-between" -> JustifyContent.SpaceBetween
        "space-evenly" -> JustifyContent.SpaceEvenly
        else -> null
    }

    fun flexGrow(cssJson: JSONObject): Float? =
        cssJson.getString(GXTemplateKey.FLEXBOX_FLEX_GROW)?.let {
            return flexGrow(it)
        }

    fun flexGrow(target: String): Float = target.toFloat()

    fun flexShrink(cssJson: JSONObject): Float? =
        cssJson.getString(GXTemplateKey.FLEXBOX_FLEX_SHRINK)?.let {
            return flexShrink(it)
        }

    fun flexShrink(target: String): Float = target.toFloat()

    fun flexBasis(cssJson: JSONObject): GXSize? {
        val value = cssJson.getString(GXTemplateKey.FLEXBOX_FLEX_BASIS)
        if (value != null) {
            return flexBasis(value)
        }
        return null
    }

    fun flexBasis(target: String): GXSize = GXSize.create(target)

    fun aspectRatio(cssJson: JSONObject): Float? =
        cssJson.getString(GXTemplateKey.FLEXBOX_ASPECT_RATIO)?.let {
            return aspectRatio(it)
        }

    fun aspectRatio(ratio: String): Float? {
        try {
            return ratio.toFloat()
        } catch (e: Exception) {
            try {
                if (ratio.contains(":")) {
                    val splits = ratio.split(":")
                    val left = splits[0].toFloat()
                    val right = splits[1].toFloat()
                    return left / right
                }
            } catch (e: Exception) {
            }
        }
        return null
    }

    fun margin(cssJson: JSONObject): Rect<GXSize>? {
        cssJson.getString(GXTemplateKey.FLEXBOX_MARGIN)?.also {
            val size = GXSize.create(it)
            return Rect(size, size, size, size)
        }

        val left = cssJson.getString(GXTemplateKey.FLEXBOX_MARGIN_LEFT)
        val right = cssJson.getString(GXTemplateKey.FLEXBOX_MARGIN_RIGHT)
        val top = cssJson.getString(GXTemplateKey.FLEXBOX_MARGIN_TOP)
        val bottom = cssJson.getString(GXTemplateKey.FLEXBOX_MARGIN_BOTTOM)

        if (!left.isNullOrEmpty() || !right.isNullOrEmpty() || !top.isNullOrEmpty() || !bottom.isNullOrEmpty()) {
            return Rect(
                GXSize.create(left ?: ""),
                GXSize.create(right ?: ""),
                GXSize.create(top ?: ""),
                GXSize.create(bottom ?: "")
            )
        }

        return null
    }

    fun padding(cssJson: JSONObject): Rect<GXSize>? {

        cssJson.getString(GXTemplateKey.FLEXBOX_PADDING)?.also {
            val size = GXSize.create(it)
            return Rect(size, size, size, size)
        }

        val paddingLeft = cssJson.getString(GXTemplateKey.FLEXBOX_PADDING_LEFT)
        val paddingRight = cssJson.getString(GXTemplateKey.FLEXBOX_PADDING_RIGHT)
        val paddingTop = cssJson.getString(GXTemplateKey.FLEXBOX_PADDING_TOP)
        val paddingBottom = cssJson.getString(GXTemplateKey.FLEXBOX_PADDING_BOTTOM)

        if (!paddingLeft.isNullOrEmpty() || !paddingRight.isNullOrEmpty() || !paddingTop.isNullOrEmpty() || !paddingBottom.isNullOrEmpty()) {
            return Rect(
                GXSize.create(paddingLeft ?: ""),
                GXSize.create(paddingRight ?: ""),
                GXSize.create(paddingTop ?: ""),
                GXSize.create(paddingBottom ?: "")
            )
        }

        return null
    }

    fun border(cssJson: JSONObject): Rect<GXSize>? {

        val value = cssJson.getString(GXTemplateKey.FLEXBOX_BORDER)
        if (value != null) {
            val size = GXSize.create(value)
            return Rect(size, size, size, size)
        }

        val left = cssJson.getString(GXTemplateKey.FLEXBOX_BORDER_LEFT)
        val right = cssJson.getString(GXTemplateKey.FLEXBOX_BORDER_RIGHT)
        val top = cssJson.getString(GXTemplateKey.FLEXBOX_BORDER_TOP)
        val bottom = cssJson.getString(GXTemplateKey.FLEXBOX_BORDER_BOTTOM)

        if (!left.isNullOrEmpty() || !right.isNullOrEmpty() || !top.isNullOrEmpty() || !bottom.isNullOrEmpty()) {
            return Rect(
                GXSize.create(left ?: ""),
                GXSize.create(right ?: ""),
                GXSize.create(top ?: ""),
                GXSize.create(bottom ?: "")
            )
        }

        return null
    }

    fun position(cssJson: JSONObject): Rect<GXSize>? {
        if (positionType(cssJson) == PositionType.Absolute) {
            val start = cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_LEFT)
            val end = cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_RIGHT)
            val top = cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_TOP)
            val bottom = cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_BOTTOM)
            if (!start.isNullOrEmpty() || !end.isNullOrEmpty() || !top.isNullOrEmpty() || !bottom.isNullOrEmpty()) {
                return Rect(
                    GXSize.create(start ?: ""),
                    GXSize.create(end ?: ""),
                    GXSize.create(top ?: ""),
                    GXSize.create(bottom ?: "")
                )
            }
        }
        return null
    }

    fun positionByExtend(cssJson: JSONObject): Rect<GXSize>? {
        // 增加判断会导致databinding的时候无法执行过去
        // if (positionType(cssJson) == PositionType.Absolute) {
        val start = cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_LEFT)
        val end = cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_RIGHT)
        val top = cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_TOP)
        val bottom = cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_BOTTOM)
        if (!start.isNullOrEmpty() || !end.isNullOrEmpty() || !top.isNullOrEmpty() || !bottom.isNullOrEmpty()) {
            return Rect(
                GXSize.create(start ?: ""),
                GXSize.create(end ?: ""),
                GXSize.create(top ?: ""),
                GXSize.create(bottom ?: "")
            )
        }
        // }
        return null
    }
}