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
    private val position: Rect<GXSize>? = null,
    private val margin: Rect<GXSize>? = null,
    private val padding: Rect<GXSize>? = null,
    private val border: Rect<GXSize>? = null,
    val flexGrow: Float? = null,
    val flexShrink: Float? = null,
    private val flexBasis: GXSize? = null,
    private val size: Size<GXSize>? = null,
    private val minSize: Size<GXSize>? = null,
    private val maxSize: Size<GXSize>? = null,
    val aspectRatio: Float? = null
) {
    private var _maxSizeForStyle: Size<Dimension>? = null

    private var _minSizeForStyle: Size<Dimension>? = null

    private var _sizeForStyle: Size<Dimension>? = null

    private var _flexBasisForStyle: Dimension? = null

    private var _borderForStyle: Rect<Dimension>? = null

    private var _paddingForStyle: Rect<Dimension>? = null

    private var _marginForStyle: Rect<Dimension>? = null

    private var _positionForStyle: Rect<Dimension>? = null

    fun reset() {
        _maxSizeForStyle = null
        _minSizeForStyle = null
        _sizeForStyle = null
        _flexBasisForStyle = null
        _borderForStyle = null
        _paddingForStyle = null
        _marginForStyle = null
        _positionForStyle = null
    }

    val maxSizeForStyle: Size<Dimension>?
        get() = if (maxSize != null) {
            if (_maxSizeForStyle == null) {
                _maxSizeForStyle = Size(
                    maxSize.width.valueDimension,
                    maxSize.height.valueDimension,
                )
                _maxSizeForStyle
            } else {
                _maxSizeForStyle
            }
        } else {
            null
        }

    val minSizeForStyle: Size<Dimension>?
        get() = if (minSize != null) {
            if (_minSizeForStyle == null) {
                _minSizeForStyle = Size(
                    minSize.width.valueDimension,
                    minSize.height.valueDimension,
                )
                _minSizeForStyle
            } else {
                _minSizeForStyle
            }
        } else {
            null
        }

    val sizeForStyle: Size<Dimension>?
        get() = if (size != null) {
            if (_sizeForStyle == null) {
                _sizeForStyle = Size(
                    size.width.valueDimension,
                    size.height.valueDimension,
                )
                _sizeForStyle
            } else {
                _sizeForStyle
            }
        } else {
            null
        }

    val flexBasisForStyle: Dimension?
        get() = if (flexBasis != null) {
            if (_flexBasisForStyle == null) {
                _flexBasisForStyle = flexBasis.valueDimension
                _flexBasisForStyle
            } else {
                _flexBasisForStyle
            }
        } else {
            null
        }

    val borderForStyle: Rect<Dimension>?
        get() = if (border != null) {
            if (_borderForStyle == null) {
                _borderForStyle = Rect(
                    border.start.valueDimension,
                    border.end.valueDimension,
                    border.top.valueDimension,
                    border.bottom.valueDimension,
                )
                _borderForStyle
            } else {
                _borderForStyle
            }
        } else {
            null
        }

    val paddingForStyle: Rect<Dimension>?
        get() = if (padding != null) {
            if (_paddingForStyle == null) {
                _paddingForStyle = Rect(
                    padding.start.valueDimension,
                    padding.end.valueDimension,
                    padding.top.valueDimension,
                    padding.bottom.valueDimension,
                )
                _paddingForStyle
            } else {
                _paddingForStyle
            }
        } else {
            null
        }

    val marginForStyle: Rect<Dimension>?
        get() = if (margin != null) {
            if (_marginForStyle == null) {
                _marginForStyle = Rect(
                    margin.start.valueDimension,
                    margin.end.valueDimension,
                    margin.top.valueDimension,
                    margin.bottom.valueDimension,
                )
                _marginForStyle
            } else {
                _marginForStyle
            }
        } else {
            null
        }

    val positionForStyle: Rect<Dimension>?
        get() = if (position != null) {
            if (_positionForStyle == null) {
                _positionForStyle = Rect(
                    position.start.valueDimension,
                    position.end.valueDimension,
                    position.top.valueDimension,
                    position.bottom.valueDimension,
                )
                _positionForStyle
            } else {
                _positionForStyle
            }
        } else {
            null
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
                    heightPriorityStyle.position, lowPriorityStyle.position
                ),
                margin = GXTemplateUtils.createRectDimensionByPriority(
                    heightPriorityStyle.margin, lowPriorityStyle.margin
                ),
                padding = GXTemplateUtils.createRectDimensionByPriority(
                    heightPriorityStyle.padding, lowPriorityStyle.padding
                ),
                border = GXTemplateUtils.createRectDimensionByPriority(
                    heightPriorityStyle.border, lowPriorityStyle.border
                ),
                flexGrow = heightPriorityStyle.flexGrow ?: lowPriorityStyle.flexGrow,
                flexShrink = heightPriorityStyle.flexShrink ?: lowPriorityStyle.flexShrink,
                flexBasis = heightPriorityStyle.flexBasis ?: lowPriorityStyle.flexBasis,
                size = GXTemplateUtils.createSizeDimensionByPriority(
                    heightPriorityStyle.size, lowPriorityStyle.size
                ),
                minSize = GXTemplateUtils.createSizeDimensionByPriority(
                    heightPriorityStyle.minSize, lowPriorityStyle.minSize
                ),
                maxSize = GXTemplateUtils.createSizeDimensionByPriority(
                    heightPriorityStyle.maxSize, lowPriorityStyle.maxSize
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