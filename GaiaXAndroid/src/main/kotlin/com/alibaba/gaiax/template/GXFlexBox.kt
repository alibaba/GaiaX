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
    var display: Display? = null,
    var positionType: PositionType? = null,
    var direction: Direction? = null,
    var flexDirection: FlexDirection? = null,
    var flexWrap: FlexWrap? = null,
    var overflow: Overflow? = null,
    var alignItems: AlignItems? = null,
    var alignSelf: AlignSelf? = null,
    var alignContent: AlignContent? = null,
    var justifyContent: JustifyContent? = null,
    private var position: Rect<GXSize>? = null,
    private var margin: Rect<GXSize>? = null,
    private var padding: Rect<GXSize>? = null,
    private var border: Rect<GXSize>? = null,
    var flexGrow: Float? = null,
    var flexShrink: Float? = null,
    private var flexBasis: GXSize? = null,
    private var size: Size<GXSize>? = null,
    private var minSize: Size<GXSize>? = null,
    private var maxSize: Size<GXSize>? = null,
    var aspectRatio: Float? = null
) {
    private var _maxSize: Size<Dimension>? = null

    private var _minSize: Size<Dimension>? = null

    private var _size: Size<Dimension>? = null

    private var _flexBasis: Dimension? = null

    private var _border: Rect<Dimension>? = null

    private var _padding: Rect<Dimension>? = null

    private var _margin: Rect<Dimension>? = null

    private var _position: Rect<Dimension>? = null

    fun reset() {
        _maxSize = null
        _minSize = null
        _size = null
        _flexBasis = null
        _border = null
        _padding = null
        _margin = null
        _position = null
    }

    val maxSizeFinal: Size<Dimension>?
        get() {
            val gxMaxSize = maxSize
            return if (gxMaxSize != null) {
                if (_maxSize == null) {
                    _maxSize = Size(
                        gxMaxSize.width.valueDimension,
                        gxMaxSize.height.valueDimension,
                    )
                    _maxSize
                } else {
                    _maxSize
                }
            } else {
                null
            }
        }

    val minSizeFinal: Size<Dimension>?
        get() {
            val gxMinSize = minSize
            return if (gxMinSize != null) {
                if (_minSize == null) {
                    _minSize = Size(
                        gxMinSize.width.valueDimension,
                        gxMinSize.height.valueDimension,
                    )
                    _minSize
                } else {
                    _minSize
                }
            } else {
                null
            }
        }

    val sizeFinal: Size<Dimension>?
        get() {
            val gxSize = size
            return if (gxSize != null) {
                if (_size == null) {
                    _size = Size(
                        gxSize.width.valueDimension,
                        gxSize.height.valueDimension,
                    )
                    _size
                } else {
                    _size
                }
            } else {
                null
            }
        }

    val flexBasisFinal: Dimension?
        get() {
            val gxFlexBasis = flexBasis
            return if (gxFlexBasis != null) {
                if (_flexBasis == null) {
                    _flexBasis = gxFlexBasis.valueDimension
                    _flexBasis
                } else {
                    _flexBasis
                }
            } else {
                null
            }
        }

    val borderFinal: Rect<Dimension>?
        get() {
            val gxBorder = border
            return if (gxBorder != null) {
                if (_border == null) {
                    _border = Rect(
                        gxBorder.start.valueDimension,
                        gxBorder.end.valueDimension,
                        gxBorder.top.valueDimension,
                        gxBorder.bottom.valueDimension,
                    )
                    _border
                } else {
                    _border
                }
            } else {
                null
            }
        }

    val paddingFinal: Rect<Dimension>?
        get() {
            val gxPadding = padding
            return if (gxPadding != null) {
                if (_padding == null) {
                    _padding = Rect(
                        gxPadding.start.valueDimension,
                        gxPadding.end.valueDimension,
                        gxPadding.top.valueDimension,
                        gxPadding.bottom.valueDimension,
                    )
                    _padding
                } else {
                    _padding
                }
            } else {
                null
            }
        }

    val marginFinal: Rect<Dimension>?
        get() {
            val gxMargin = margin
            return if (gxMargin != null) {
                if (_margin == null) {
                    _margin = Rect(
                        gxMargin.start.valueDimension,
                        gxMargin.end.valueDimension,
                        gxMargin.top.valueDimension,
                        gxMargin.bottom.valueDimension,
                    )
                    _margin
                } else {
                    _margin
                }
            } else {
                null
            }
        }

    val positionFinal: Rect<Dimension>?
        get() {
            val gxPosition = position
            return if (gxPosition != null) {
                if (_position == null) {
                    _position = Rect(
                        gxPosition.start.valueDimension,
                        gxPosition.end.valueDimension,
                        gxPosition.top.valueDimension,
                        gxPosition.bottom.valueDimension,
                    )
                    _position
                } else {
                    _position
                }
            } else {
                null
            }
        }

    companion object {

        fun create(css: JSONObject): GXFlexBox {
            if (css.isEmpty()) {
                return GXFlexBox()
            }
            val gxFlexBox = GXFlexBox()

            css.forEach {
                val key: String = it.key
                val value: String = it.value.toString()
                when (key) {
                    GXTemplateKey.FLEXBOX_DISPLAY -> gxFlexBox.display =
                        GXFlexBoxConvert.display(value)
                    GXTemplateKey.FLEXBOX_POSITION_TYPE -> gxFlexBox.positionType =
                        GXFlexBoxConvert.positionType(value)
                    GXTemplateKey.FLEXBOX_DIRECTION -> gxFlexBox.direction =
                        GXFlexBoxConvert.direction(value)
                    GXTemplateKey.FLEXBOX_FLEX_DIRECTION -> gxFlexBox.flexDirection =
                        GXFlexBoxConvert.flexDirection(value)
                    GXTemplateKey.FLEXBOX_FLEX_WRAP -> gxFlexBox.flexWrap =
                        GXFlexBoxConvert.flexWrap(value)
                    GXTemplateKey.FLEXBOX_OVERFLOW -> gxFlexBox.overflow =
                        GXFlexBoxConvert.overflow(value)
                    GXTemplateKey.FLEXBOX_ALIGN_ITEMS -> gxFlexBox.alignItems =
                        GXFlexBoxConvert.alignItems(value)
                    GXTemplateKey.FLEXBOX_ALIGN_SELF -> gxFlexBox.alignSelf =
                        GXFlexBoxConvert.alignSelf(value)
                    GXTemplateKey.FLEXBOX_ALIGN_CONTENT -> gxFlexBox.alignContent =
                        GXFlexBoxConvert.alignContent(value)
                    GXTemplateKey.FLEXBOX_JUSTIFY_CONTENT -> gxFlexBox.justifyContent =
                        GXFlexBoxConvert.justifyContent(value)
                    GXTemplateKey.FLEXBOX_POSITION_LEFT, GXTemplateKey.FLEXBOX_POSITION_RIGHT, GXTemplateKey.FLEXBOX_POSITION_TOP, GXTemplateKey.FLEXBOX_POSITION_BOTTOM -> if (gxFlexBox.position == null) gxFlexBox.position =
                        GXFlexBoxConvert.position(css)
                    GXTemplateKey.FLEXBOX_MARGIN, GXTemplateKey.FLEXBOX_MARGIN_LEFT, GXTemplateKey.FLEXBOX_MARGIN_RIGHT, GXTemplateKey.FLEXBOX_MARGIN_TOP, GXTemplateKey.FLEXBOX_MARGIN_BOTTOM -> if (gxFlexBox.margin == null) gxFlexBox.margin =
                        GXFlexBoxConvert.margin(css)
                    GXTemplateKey.FLEXBOX_PADDING, GXTemplateKey.FLEXBOX_PADDING_LEFT, GXTemplateKey.FLEXBOX_PADDING_RIGHT, GXTemplateKey.FLEXBOX_PADDING_TOP, GXTemplateKey.FLEXBOX_PADDING_BOTTOM -> if (gxFlexBox.padding == null) gxFlexBox.padding =
                        GXFlexBoxConvert.padding(css)
                    GXTemplateKey.FLEXBOX_BORDER, GXTemplateKey.FLEXBOX_BORDER_LEFT, GXTemplateKey.FLEXBOX_BORDER_RIGHT, GXTemplateKey.FLEXBOX_BORDER_TOP, GXTemplateKey.FLEXBOX_BORDER_BOTTOM -> if (gxFlexBox.border == null) gxFlexBox.border =
                        GXFlexBoxConvert.border(css)
                    GXTemplateKey.FLEXBOX_FLEX_BASIS -> gxFlexBox.flexBasis =
                        GXFlexBoxConvert.flexBasis(value)
                    GXTemplateKey.FLEXBOX_SIZE_WIDTH, GXTemplateKey.FLEXBOX_SIZE_HEIGHT -> if (gxFlexBox.size == null) gxFlexBox.size =
                        GXFlexBoxConvert.size(css)
                    GXTemplateKey.FLEXBOX_MIN_WIDTH, GXTemplateKey.FLEXBOX_MIN_HEIGHT -> if (gxFlexBox.minSize == null) gxFlexBox.minSize =
                        GXFlexBoxConvert.minSize(css)
                    GXTemplateKey.FLEXBOX_MAX_WIDTH, GXTemplateKey.FLEXBOX_MAX_HEIGHT -> if (gxFlexBox.maxSize == null) gxFlexBox.maxSize =
                        GXFlexBoxConvert.maxSize(css)
                    GXTemplateKey.FLEXBOX_ASPECT_RATIO -> gxFlexBox.aspectRatio =
                        GXFlexBoxConvert.aspectRatio(value)
                    GXTemplateKey.FLEXBOX_FLEX_GROW -> gxFlexBox.flexGrow =
                        GXFlexBoxConvert.flexGrow(value)
                    GXTemplateKey.FLEXBOX_FLEX_SHRINK -> gxFlexBox.flexShrink =
                        GXFlexBoxConvert.flexShrink(value)
                }
            }

            return gxFlexBox
        }

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