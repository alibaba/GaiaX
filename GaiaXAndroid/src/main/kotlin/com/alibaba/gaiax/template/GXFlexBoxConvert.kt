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

    fun size(cssJson: JSONObject): Size<Dimension>? {
        val width = cssJson.getString(GXTemplateKey.FLEXBOX_SIZE_WIDTH)
        val height = cssJson.getString(GXTemplateKey.FLEXBOX_SIZE_HEIGHT)
        return if (width != null && height != null) {
            Size(GXSize.create(width).valueDimension, GXSize.create(height).valueDimension)
        } else if (width != null && height == null) {
            Size(GXSize.create(width).valueDimension, GXSize.Auto.valueDimension)
        } else if (height != null && width == null) {
            Size(GXSize.Auto.valueDimension, GXSize.create(height).valueDimension)
        } else {
            null
        }
    }

    fun minSize(cssJson: JSONObject): Size<Dimension>? {
        val width = cssJson.getString(GXTemplateKey.FLEXBOX_MIN_WIDTH)
        val height = cssJson.getString(GXTemplateKey.FLEXBOX_MIN_HEIGHT)
        return if (width != null && height != null) {
            Size(GXSize.create(width).valueDimension, GXSize.create(height).valueDimension)
        } else if (width != null && height == null) {
            Size(GXSize.create(width).valueDimension, GXSize.Auto.valueDimension)
        } else if (height != null && width == null) {
            Size(GXSize.Auto.valueDimension, GXSize.create(height).valueDimension)
        } else {
            null
        }
    }

    fun maxSize(cssJson: JSONObject): Size<Dimension>? {
        val width = cssJson.getString(GXTemplateKey.FLEXBOX_MAX_WIDTH)
        val height = cssJson.getString(GXTemplateKey.FLEXBOX_MAX_HEIGHT)
        return if (width != null && height != null) {
            Size(GXSize.create(width).valueDimension, GXSize.create(height).valueDimension)
        } else if (width != null && height == null) {
            Size(GXSize.create(width).valueDimension, GXSize.Auto.valueDimension)
        } else if (height != null && width == null) {
            Size(GXSize.Auto.valueDimension, GXSize.create(height).valueDimension)
        } else {
            null
        }
    }

    fun display(cssJson: JSONObject): Display? =
        when (cssJson.getString(GXTemplateKey.FLEXBOX_DISPLAY)) {
            "flex" -> Display.Flex
            "none" -> Display.None
            else -> null
        }

    fun positionType(cssJson: JSONObject): PositionType? =
        when (cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_TYPE)) {
            "relative" -> PositionType.Relative
            "absolute" -> PositionType.Absolute
            else -> null
        }

    fun direction(cssJson: JSONObject): Direction? =
        when (cssJson.getString(GXTemplateKey.FLEXBOX_DIRECTION)) {
            "ltr" -> Direction.LTR
            "absolute" -> Direction.RTL
            "inherit" -> Direction.Inherit
            else -> null
        }

    fun flexDirection(cssJson: JSONObject): FlexDirection? =
        when (cssJson.getString(GXTemplateKey.FLEXBOX_FLEX_DIRECTION)) {
            "row" -> FlexDirection.Row
            "column" -> FlexDirection.Column
            "column-reverse" -> FlexDirection.ColumnReverse
            "row-reverse" -> FlexDirection.RowReverse
            else -> null
        }

    fun flexWrap(cssJson: JSONObject): FlexWrap? =
        when (cssJson.getString(GXTemplateKey.FLEXBOX_FLEX_WRAP)) {
            "nowrap" -> FlexWrap.NoWrap
            "wrap" -> FlexWrap.Wrap
            "wrap-reverse" -> FlexWrap.WrapReverse
            else -> null
        }

    fun overflow(cssJson: JSONObject): Overflow? =
        when (cssJson.getString(GXTemplateKey.FLEXBOX_OVERFLOW)) {
            "visible" -> Overflow.Visible
            "hidden" -> Overflow.Hidden
            "scroll" -> Overflow.Scroll
            else -> null
        }

    fun alignItems(cssJson: JSONObject): AlignItems? =
        when (cssJson.getString(GXTemplateKey.FLEXBOX_ALIGN_ITEMS)) {
            "flex-start" -> AlignItems.FlexStart
            "flex-end" -> AlignItems.FlexEnd
            "center" -> AlignItems.Center
            "baseline" -> AlignItems.Baseline
            "stretch" -> AlignItems.Stretch
            else -> null
        }

    fun alignSelf(cssJson: JSONObject): AlignSelf? =
        when (cssJson.getString(GXTemplateKey.FLEXBOX_ALIGN_SELF)) {
            "auto" -> AlignSelf.Auto
            "flex-start" -> AlignSelf.FlexStart
            "flex-end" -> AlignSelf.FlexEnd
            "center" -> AlignSelf.Center
            "baseline" -> AlignSelf.Baseline
            "stretch" -> AlignSelf.Stretch
            else -> null
        }

    fun alignContent(cssJson: JSONObject): AlignContent? =
        when (cssJson.getString(GXTemplateKey.FLEXBOX_ALIGN_CONTENT)) {
            "flex-start" -> AlignContent.FlexStart
            "flex-end" -> AlignContent.FlexEnd
            "center" -> AlignContent.Center
            "space-around" -> AlignContent.SpaceAround
            "space-between" -> AlignContent.SpaceBetween
            "stretch" -> AlignContent.Stretch
            else -> null
        }

    fun justifyContent(cssJson: JSONObject): JustifyContent? =
        when (cssJson.getString(GXTemplateKey.FLEXBOX_JUSTIFY_CONTENT)) {
            "flex-start" -> JustifyContent.FlexStart
            "flex-end" -> JustifyContent.FlexEnd
            "center" -> JustifyContent.Center
            "space-around" -> JustifyContent.SpaceAround
            "space-between" -> JustifyContent.SpaceBetween
            "space-evenly" -> JustifyContent.SpaceEvenly
            else -> null
        }

    fun flexGrow(cssJson: JSONObject): Float? =
        cssJson.getString(GXTemplateKey.FLEXBOX_FLEX_GROW)?.toFloat()

    fun flexShrink(cssJson: JSONObject): Float? =
        cssJson.getString(GXTemplateKey.FLEXBOX_FLEX_SHRINK)?.toFloat()

    fun flexBasis(cssJson: JSONObject): Dimension? {
        val value = cssJson.getString(GXTemplateKey.FLEXBOX_FLEX_BASIS)
        if (value != null) {
            return GXSize.create(value).valueDimension
        }
        return null
    }

    fun aspectRatio(cssJson: JSONObject): Float? {
        cssJson.getString(GXTemplateKey.FLEXBOX_ASPECT_RATIO)?.let { ratio ->
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
        }
        return null
    }

    fun margin(cssJson: JSONObject): Rect<Dimension>? {
        cssJson.getString(GXTemplateKey.FLEXBOX_MARGIN)?.also {
            val size = GXSize.create(it).valueDimension
            return Rect(size, size, size, size)
        }

        val left = cssJson.getString(GXTemplateKey.FLEXBOX_MARGIN_LEFT)
        val right = cssJson.getString(GXTemplateKey.FLEXBOX_MARGIN_RIGHT)
        val top = cssJson.getString(GXTemplateKey.FLEXBOX_MARGIN_TOP)
        val bottom = cssJson.getString(GXTemplateKey.FLEXBOX_MARGIN_BOTTOM)

        if (!left.isNullOrEmpty() || !right.isNullOrEmpty() || !top.isNullOrEmpty() || !bottom.isNullOrEmpty()) {
            return Rect(
                GXSize.create(left ?: "").valueDimension,
                GXSize.create(right ?: "").valueDimension,
                GXSize.create(top ?: "").valueDimension,
                GXSize.create(bottom ?: "").valueDimension
            )
        }

        return null
    }

    fun padding(cssJson: JSONObject): Rect<Dimension>? {

        cssJson.getString(GXTemplateKey.FLEXBOX_PADDING)?.also {
            val size = GXSize.create(it).valueDimension
            return Rect(size, size, size, size)
        }

        val paddingLeft = cssJson.getString(GXTemplateKey.FLEXBOX_PADDING_LEFT)
        val paddingRight = cssJson.getString(GXTemplateKey.FLEXBOX_PADDING_RIGHT)
        val paddingTop = cssJson.getString(GXTemplateKey.FLEXBOX_PADDING_TOP)
        val paddingBottom = cssJson.getString(GXTemplateKey.FLEXBOX_PADDING_BOTTOM)

        if (!paddingLeft.isNullOrEmpty() || !paddingRight.isNullOrEmpty() || !paddingTop.isNullOrEmpty() || !paddingBottom.isNullOrEmpty()) {
            return Rect(
                GXSize.create(paddingLeft ?: "").valueDimension,
                GXSize.create(paddingRight ?: "").valueDimension,
                GXSize.create(paddingTop ?: "").valueDimension,
                GXSize.create(paddingBottom ?: "").valueDimension
            )
        }

        return null
    }

    fun border(cssJson: JSONObject): Rect<Dimension>? {

        val value = cssJson.getString(GXTemplateKey.FLEXBOX_BORDER)
        if (value != null) {
            val size = GXSize.create(value).valueDimension
            return Rect(size, size, size, size)
        }

        val left = cssJson.getString(GXTemplateKey.FLEXBOX_BORDER_LEFT)
        val right = cssJson.getString(GXTemplateKey.FLEXBOX_BORDER_RIGHT)
        val top = cssJson.getString(GXTemplateKey.FLEXBOX_BORDER_TOP)
        val bottom = cssJson.getString(GXTemplateKey.FLEXBOX_BORDER_BOTTOM)

        if (!left.isNullOrEmpty() || !right.isNullOrEmpty() || !top.isNullOrEmpty() || !bottom.isNullOrEmpty()) {
            return Rect(
                GXSize.create(left ?: "").valueDimension,
                GXSize.create(right ?: "").valueDimension,
                GXSize.create(top ?: "").valueDimension,
                GXSize.create(bottom ?: "").valueDimension
            )
        }

        return null
    }

    fun position(cssJson: JSONObject): Rect<Dimension>? {
        // 增加判断会导致databinding的时候无法执行过去
        // if (positionType(cssJson) == PositionType.Absolute) {
        val start = cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_LEFT)
        val end = cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_RIGHT)
        val top = cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_TOP)
        val bottom = cssJson.getString(GXTemplateKey.FLEXBOX_POSITION_BOTTOM)
        if (!start.isNullOrEmpty() || !end.isNullOrEmpty() || !top.isNullOrEmpty() || !bottom.isNullOrEmpty()) {
            return Rect(
                GXSize.create(start ?: "").valueDimension,
                GXSize.create(end ?: "").valueDimension,
                GXSize.create(top ?: "").valueDimension,
                GXSize.create(bottom ?: "").valueDimension
            )
        }
        // }
        return null
    }
}