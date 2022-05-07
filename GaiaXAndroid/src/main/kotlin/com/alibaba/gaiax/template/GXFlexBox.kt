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
import com.alibaba.gaiax.template.utils.GXTemplateUtils

/**
 * @suppress
 */
data class GXFlexBox(
    val display: Display? = null,
    val positionType: PositionType? = null,
    val direction: Direction? = null,
    val flexDirection: FlexDirection? = null,
    val flexWrap: FlexWrap? = null,
    val overflow: Overflow? = null,
    val alignItems: AlignItems? = null,
    val alignSelf: AlignSelf? = null,
    val alignContent: AlignContent? = null,
    val justifyContent: JustifyContent? = null,
    val position: Rect<Dimension>? = null,
    val margin: Rect<Dimension>? = null,
    val padding: Rect<Dimension>? = null,
    val border: Rect<Dimension>? = null,
    val flexGrow: Float? = null,
    val flexShrink: Float? = null,
    val flexBasis: Dimension? = null,
    val size: Size<Dimension>? = null,
    val minSize: Size<Dimension>? = null,
    val maxSize: Size<Dimension>? = null,
    val aspectRatio: Float? = null
) {

    fun isEmpty(): Boolean {
        return display == null && positionType == null && direction == null && flexDirection == null && flexWrap == null && overflow == null && alignItems == null && alignSelf == null && alignContent == null && justifyContent == null
                && position == null && margin == null && padding == null && border == null && flexGrow == null && flexShrink == null && flexBasis == null && size == null && minSize == null && maxSize == null && aspectRatio == null
    }

    companion object {

        fun create(lowPriorityStyle: GXFlexBox, heightPriorityStyle: GXFlexBox): GXFlexBox {
            return GXFlexBox(
                display = heightPriorityStyle.display ?: lowPriorityStyle.display,
                positionType = heightPriorityStyle.positionType ?: lowPriorityStyle.positionType,
                direction = heightPriorityStyle.direction ?: lowPriorityStyle.direction,
                flexDirection = heightPriorityStyle.flexDirection ?: lowPriorityStyle.flexDirection,
                flexWrap = heightPriorityStyle.flexWrap ?: lowPriorityStyle.flexWrap,
                overflow = heightPriorityStyle.overflow ?: lowPriorityStyle.overflow,
                alignItems = heightPriorityStyle.alignItems ?: lowPriorityStyle.alignItems,
                alignSelf = heightPriorityStyle.alignSelf ?: lowPriorityStyle.alignSelf,
                alignContent = heightPriorityStyle.alignContent ?: lowPriorityStyle.alignContent,
                justifyContent = heightPriorityStyle.justifyContent
                    ?: lowPriorityStyle.justifyContent,
                position = GXTemplateUtils.createRectDimensionByPriority(
                    heightPriorityStyle.position,
                    lowPriorityStyle.position
                ),
                margin = GXTemplateUtils.createRectDimensionByPriority(
                    heightPriorityStyle.margin,
                    lowPriorityStyle.margin
                ),
                padding = GXTemplateUtils.createRectDimensionByPriority(
                    heightPriorityStyle.padding,
                    lowPriorityStyle.padding
                ),
                border = GXTemplateUtils.createRectDimensionByPriority(
                    heightPriorityStyle.border,
                    lowPriorityStyle.border
                ),
                flexGrow = heightPriorityStyle.flexGrow ?: lowPriorityStyle.flexGrow,
                flexShrink = heightPriorityStyle.flexShrink ?: lowPriorityStyle.flexShrink,
                flexBasis = heightPriorityStyle.flexBasis ?: lowPriorityStyle.flexBasis,
                size = GXTemplateUtils.createSizeDimensionByPriority(
                    heightPriorityStyle.size,
                    lowPriorityStyle.size
                ),
                minSize = GXTemplateUtils.createSizeDimensionByPriority(
                    heightPriorityStyle.minSize,
                    lowPriorityStyle.minSize
                ),
                maxSize = GXTemplateUtils.createSizeDimensionByPriority(
                    heightPriorityStyle.maxSize,
                    lowPriorityStyle.maxSize
                ),
                aspectRatio = heightPriorityStyle.aspectRatio ?: lowPriorityStyle.aspectRatio

            )
        }


        fun create(css: JSONObject): GXFlexBox {
            if (css.isEmpty()) {
                return GXFlexBox()
            }
            return GXFlexBox(
                display = GXFlexBoxConvert.display(css),
                positionType = GXFlexBoxConvert.positionType(css),
                direction = GXFlexBoxConvert.direction(css),
                flexDirection = GXFlexBoxConvert.flexDirection(css),
                flexWrap = GXFlexBoxConvert.flexWrap(css),
                overflow = GXFlexBoxConvert.overflow(css),
                alignItems = GXFlexBoxConvert.alignItems(css),
                alignSelf = GXFlexBoxConvert.alignSelf(css),
                alignContent = GXFlexBoxConvert.alignContent(css),
                justifyContent = GXFlexBoxConvert.justifyContent(css),
                position = GXFlexBoxConvert.position(css),
                margin = GXFlexBoxConvert.margin(css),
                padding = GXFlexBoxConvert.padding(css),
                border = GXFlexBoxConvert.border(css),
                flexBasis = GXFlexBoxConvert.flexBasis(css),
                size = GXFlexBoxConvert.size(css),
                minSize = GXFlexBoxConvert.minSize(css),
                maxSize = GXFlexBoxConvert.maxSize(css),
                aspectRatio = GXFlexBoxConvert.aspectRatio(css),
                flexGrow = GXFlexBoxConvert.flexGrow(css),
                flexShrink = GXFlexBoxConvert.flexShrink(css)
            )
        }

        fun createByExtend(css: JSONObject): GXFlexBox {
            if (css.isEmpty()) {
                return GXFlexBox()
            }
            return GXFlexBox(
                display = GXFlexBoxConvert.display(css),
                positionType = GXFlexBoxConvert.positionType(css),
                direction = GXFlexBoxConvert.direction(css),
                flexDirection = GXFlexBoxConvert.flexDirection(css),
                flexWrap = GXFlexBoxConvert.flexWrap(css),
                overflow = GXFlexBoxConvert.overflow(css),
                alignItems = GXFlexBoxConvert.alignItems(css),
                alignSelf = GXFlexBoxConvert.alignSelf(css),
                alignContent = GXFlexBoxConvert.alignContent(css),
                justifyContent = GXFlexBoxConvert.justifyContent(css),
                position = GXFlexBoxConvert.positionByExtend(css),
                margin = GXFlexBoxConvert.margin(css),
                padding = GXFlexBoxConvert.padding(css),
                border = GXFlexBoxConvert.border(css),
                flexBasis = GXFlexBoxConvert.flexBasis(css),
                size = GXFlexBoxConvert.size(css),
                minSize = GXFlexBoxConvert.minSize(css),
                maxSize = GXFlexBoxConvert.maxSize(css),
                aspectRatio = GXFlexBoxConvert.aspectRatio(css),
                flexGrow = GXFlexBoxConvert.flexGrow(css),
                flexShrink = GXFlexBoxConvert.flexShrink(css)
            )
        }
    }
}