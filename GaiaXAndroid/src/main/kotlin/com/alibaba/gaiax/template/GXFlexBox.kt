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

import app.visly.stretch.AlignContent
import app.visly.stretch.AlignItems
import app.visly.stretch.AlignSelf
import app.visly.stretch.Dimension
import app.visly.stretch.Direction
import app.visly.stretch.Display
import app.visly.stretch.FlexDirection
import app.visly.stretch.FlexWrap
import app.visly.stretch.JustifyContent
import app.visly.stretch.Overflow
import app.visly.stretch.PositionType
import app.visly.stretch.Rect
import app.visly.stretch.Size
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.utils.Log

/**
 * @suppress
 */
data class GXFlexBox(
    internal var displayForTemplate: Display? = null,
    internal var positionTypeForTemplate: PositionType? = null,
    internal var directionForTemplate: Direction? = null,
    internal var flexDirectionForTemplate: FlexDirection? = null,
    internal var flexWrapForTemplate: FlexWrap? = null,
    internal var overflowForTemplate: Overflow? = null,
    internal var alignItemsForTemplate: AlignItems? = null,
    internal var alignSelfForTemplate: AlignSelf? = null,
    internal var alignContentForTemplate: AlignContent? = null,
    internal var justifyContentForTemplate: JustifyContent? = null,
    internal var positionForTemplate: Rect<GXSize?>? = null,
    internal var marginForTemplate: Rect<GXSize?>? = null,
    internal var paddingForTemplate: Rect<GXSize?>? = null,
    internal var borderForTemplate: Rect<GXSize?>? = null,
    internal var flexGrowForTemplate: Float? = null,
    internal var flexShrinkForTemplate: Float? = null,
    internal var flexBasisForTemplate: GXSize? = null,
    internal var sizeForTemplate: Size<GXSize?>? = null,
    internal var minSizeForTemplate: Size<GXSize?>? = null,
    internal var maxSizeForTemplate: Size<GXSize?>? = null,
    internal var aspectRatioForTemplate: Float? = null
) {

    private var displayForExtend: Display? = null
    private var positionTypeForExtend: PositionType? = null
    private var directionForExtend: Direction? = null
    private var flexDirectionForExtend: FlexDirection? = null
    private var flexWrapForExtend: FlexWrap? = null
    private var overflowForExtend: Overflow? = null
    private var alignItemsForExtend: AlignItems? = null
    private var alignSelfForExtend: AlignSelf? = null
    private var alignContentForExtend: AlignContent? = null
    private var justifyContentForExtend: JustifyContent? = null
    private var positionForExtend: Rect<GXSize?>? = null
    private var marginForExtend: Rect<GXSize?>? = null
    private var paddingForExtend: Rect<GXSize?>? = null
    private var borderForExtend: Rect<GXSize?>? = null
    private var flexGrowForExtend: Float? = null
    private var flexShrinkForExtend: Float? = null
    private var flexBasisForExtend: GXSize? = null
    private var sizeForExtend: Size<GXSize?>? = null
    private var minSizeForExtend: Size<GXSize?>? = null
    private var maxSizeForExtend: Size<GXSize?>? = null
    private var aspectRatioForExtend: Float? = null
    private var maxSizeForFinal: Size<Dimension>? = null
    private var minSizeForFinal: Size<Dimension>? = null
    private var sizeForFinal: Size<Dimension>? = null
    private var borderForFinal: Rect<Dimension>? = null
    private var paddingForFinal: Rect<Dimension>? = null
    private var marginForFinal: Rect<Dimension>? = null
    private var positionForFinal: Rect<Dimension>? = null

    fun reset() {
        displayForExtend = null
        positionTypeForExtend = null
        directionForExtend = null
        flexDirectionForExtend = null
        flexWrapForExtend = null
        overflowForExtend = null
        alignItemsForExtend = null
        alignSelfForExtend = null
        alignContentForExtend = null
        justifyContentForExtend = null
        positionForExtend = null
        marginForExtend = null
        paddingForExtend = null
        borderForExtend = null
        flexGrowForExtend = null
        flexShrinkForExtend = null
        flexBasisForExtend = null
        sizeForExtend = null
        minSizeForExtend = null
        maxSizeForExtend = null
        aspectRatioForExtend = null

        maxSizeForFinal = null
        minSizeForFinal = null
        sizeForFinal = null
        borderForFinal = null
        paddingForFinal = null
        marginForFinal = null
        positionForFinal = null

        _maxSize = null
        _minSize = null
        _size = null
        _border = null
        _border = null
        _padding = null
        _margin = null
        _position = null
    }

    fun updateByExtend(context: GXTemplateContext, extendCssData: JSONObject) {
        val flexBox = this

        extendCssData.forEach {
            val key: String = it.key
            val value = it.value
            if (value == null) {
                if (Log.isLog()) {
                    Log.e("GXFlexBox.updateByExtend @forEach, key=$key, value=$value")
                }
                return@forEach
            }
            when (key) {
                GXTemplateKey.FLEXBOX_DISPLAY -> {
                    flexBox.displayForExtend = GXFlexBoxConvert.display(value.toString())
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_POSITION_TYPE -> {
                    flexBox.positionTypeForExtend =
                        GXFlexBoxConvert.positionType(value.toString())
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_DIRECTION -> {
                    flexBox.directionForExtend = GXFlexBoxConvert.direction(value.toString())
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_FLEX_DIRECTION -> {
                    flexBox.flexDirectionForExtend =
                        GXFlexBoxConvert.flexDirection(value.toString())
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_FLEX_WRAP -> {
                    flexBox.flexWrapForExtend = GXFlexBoxConvert.flexWrap(value.toString())
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_OVERFLOW -> {
                    flexBox.overflowForExtend = GXFlexBoxConvert.overflow(value.toString())
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_ALIGN_ITEMS -> {
                    flexBox.alignItemsForExtend = GXFlexBoxConvert.alignItems(value.toString())
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_ALIGN_SELF -> {
                    flexBox.alignSelfForExtend =
                        GXFlexBoxConvert.alignSelf(value.toString())
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_ALIGN_CONTENT -> {
                    flexBox.alignContentForExtend =
                        GXFlexBoxConvert.alignContent(value.toString())
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_JUSTIFY_CONTENT -> {
                    flexBox.justifyContentForExtend =
                        GXFlexBoxConvert.justifyContent(value.toString())
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_POSITION_LEFT, GXTemplateKey.FLEXBOX_POSITION_RIGHT, GXTemplateKey.FLEXBOX_POSITION_TOP, GXTemplateKey.FLEXBOX_POSITION_BOTTOM -> if (flexBox.positionType == PositionType.Absolute && flexBox.positionForExtend == null) {
                    positionForFinal = null
                    flexBox.positionForExtend = GXFlexBoxConvert.position2(extendCssData)
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_MARGIN, GXTemplateKey.FLEXBOX_MARGIN_LEFT, GXTemplateKey.FLEXBOX_MARGIN_RIGHT, GXTemplateKey.FLEXBOX_MARGIN_TOP, GXTemplateKey.FLEXBOX_MARGIN_BOTTOM -> if (flexBox.marginForExtend == null) {
                    marginForFinal = null
                    flexBox.marginForExtend = GXFlexBoxConvert.margin(extendCssData)
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_PADDING, GXTemplateKey.FLEXBOX_PADDING_LEFT, GXTemplateKey.FLEXBOX_PADDING_RIGHT, GXTemplateKey.FLEXBOX_PADDING_TOP, GXTemplateKey.FLEXBOX_PADDING_BOTTOM -> if (flexBox.paddingForExtend == null) {
                    paddingForFinal = null
                    flexBox.paddingForExtend = GXFlexBoxConvert.padding(extendCssData)
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_BORDER, GXTemplateKey.FLEXBOX_BORDER_LEFT, GXTemplateKey.FLEXBOX_BORDER_RIGHT, GXTemplateKey.FLEXBOX_BORDER_TOP, GXTemplateKey.FLEXBOX_BORDER_BOTTOM -> if (flexBox.borderForExtend == null) {
                    borderForFinal = null
                    flexBox.borderForExtend = GXFlexBoxConvert.border(extendCssData)
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_FLEX_BASIS -> {
                    flexBox.flexBasisForExtend = GXFlexBoxConvert.flexBasis(value.toString())
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_SIZE_WIDTH, GXTemplateKey.FLEXBOX_SIZE_HEIGHT -> if (flexBox.sizeForExtend == null) {
                    sizeForFinal = null
                    flexBox.sizeForExtend = GXFlexBoxConvert.size2(extendCssData)
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_MIN_WIDTH, GXTemplateKey.FLEXBOX_MIN_HEIGHT -> if (flexBox.minSizeForExtend == null) {
                    minSizeForFinal = null
                    flexBox.minSizeForExtend = GXFlexBoxConvert.minSize2(extendCssData)
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_MAX_WIDTH, GXTemplateKey.FLEXBOX_MAX_HEIGHT -> if (flexBox.maxSizeForExtend == null) {
                    maxSizeForFinal = null
                    flexBox.maxSizeForExtend = GXFlexBoxConvert.maxSize2(extendCssData)
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_ASPECT_RATIO -> {
                    flexBox.aspectRatioForExtend = GXFlexBoxConvert.aspectRatio(value.toString())
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_FLEX_GROW -> {
                    flexBox.flexGrowForExtend = GXFlexBoxConvert.flexGrow(value.toString())
                    context.flagExtendFlexbox()
                }

                GXTemplateKey.FLEXBOX_FLEX_SHRINK -> {
                    flexBox.flexShrinkForExtend = GXFlexBoxConvert.flexShrink(value.toString())
                    context.flagExtendFlexbox()
                }
            }
        }
    }

    fun updateByVisual(visual: GXFlexBox) {
        visual.display?.let {
            displayForExtend = it
        }
        visual.positionType?.let {
            positionTypeForExtend = it
        }
        visual.direction?.let {
            directionForExtend = it
        }
        visual.flexDirection?.let {
            flexDirectionForExtend = it
        }
        visual.flexWrap?.let {
            flexWrapForExtend = it
        }
        visual.overflow?.let {
            overflowForExtend = it
        }
        visual.alignItems?.let {
            alignItemsForExtend = it
        }
        visual.alignSelf?.let {
            alignSelfForExtend = it
        }
        visual.alignContent?.let {
            alignContentForExtend = it
        }
        visual.justifyContent?.let {
            justifyContentForExtend = it
        }
        visual.flexGrow?.let {
            flexGrowForExtend = it
        }
        visual.flexShrink?.let {
            flexShrinkForExtend = it
        }
        visual.flexBasis?.let {
            flexBasisForExtend = it
        }
        visual.aspectRatio?.let {
            aspectRatioForExtend = it
        }
        visual.position?.let {
            if (positionForExtend != null) {
                if (it.start != null) {
                    positionForExtend?.start = it.start
                }
                if (it.end != null) {
                    positionForExtend?.end = it.end
                }
                if (it.top != null) {
                    positionForExtend?.top = it.top
                }
                if (it.bottom != null) {
                    positionForExtend?.bottom = it.bottom
                }
            } else {
                positionForExtend = it
            }
            positionForFinal = null
        }
        visual.margin?.let {
            if (marginForExtend != null) {
                if (it.start != null) {
                    marginForExtend?.start = it.start
                }
                if (it.end != null) {
                    marginForExtend?.end = it.end
                }
                if (it.top != null) {
                    marginForExtend?.top = it.top
                }
                if (it.bottom != null) {
                    marginForExtend?.bottom = it.bottom
                }
            } else {
                marginForExtend = it
            }
            marginForFinal = null
        }
        visual.padding?.let {
            if (paddingForExtend != null) {
                if (it.start != null) {
                    paddingForExtend?.start = it.start
                }
                if (it.end != null) {
                    paddingForExtend?.end = it.end
                }
                if (it.top != null) {
                    paddingForExtend?.top = it.top
                }
                if (it.bottom != null) {
                    paddingForExtend?.bottom = it.bottom
                }
            } else {
                paddingForExtend = it
            }
            paddingForFinal = null
        }
        visual.border?.let {
            if (borderForExtend != null) {
                if (it.start != null) {
                    borderForExtend?.start = it.start
                }
                if (it.end != null) {
                    borderForExtend?.end = it.end
                }
                if (it.top != null) {
                    borderForExtend?.top = it.top
                }
                if (it.bottom != null) {
                    borderForExtend?.bottom = it.bottom
                }
            } else {
                borderForExtend = it
            }
            borderForFinal = null
        }
        visual.size?.let {
            if (sizeForExtend != null) {
                if (it.width != null) {
                    sizeForExtend?.width = it.width
                }
                if (it.height != null) {
                    sizeForExtend?.height = it.height
                }
            } else {
                sizeForExtend = it
            }
            sizeForFinal = null
        }
        visual.minSize?.let {
            if (minSizeForExtend != null) {
                if (it.width != null) {
                    minSizeForExtend?.width = it.width
                }
                if (it.height != null) {
                    minSizeForExtend?.height = it.height
                }
            } else {
                minSizeForExtend = it
            }
            minSizeForFinal = null
        }
        visual.maxSize?.let {
            if (maxSizeForExtend != null) {
                if (it.width != null) {
                    maxSizeForExtend?.width = it.width
                }
                if (it.height != null) {
                    maxSizeForExtend?.height = it.height
                }
            } else {
                maxSizeForExtend = it
            }
            maxSizeForFinal = null
        }
    }

    override fun toString(): String {
        return "GXFlexBox(" +
                "alignItemsForTemplate=$alignItemsForTemplate, " +
                "alignSelfForTemplate=$alignSelfForTemplate, " +
                "alignContentForTemplate=$alignContentForTemplate, " +
                "justifyContentForTemplate=$justifyContentForTemplate, " +
                "positionForTemplate=$positionForTemplate, " +
                "marginForTemplate=$marginForTemplate, " +
                "paddingForTemplate=$paddingForTemplate, " +
                "borderForTemplate=$borderForTemplate, " +
                "flexGrowForTemplate=$flexGrowForTemplate, " +
                "flexShrinkForTemplate=$flexShrinkForTemplate, " +
                "flexBasisForTemplate=$flexBasisForTemplate, " +
                "sizeForTemplate=$sizeForTemplate, " +
                "minSizeForTemplate=$minSizeForTemplate, " +
                "maxSizeForTemplate=$maxSizeForTemplate, " +
                "aspectRatioForTemplate=$aspectRatioForTemplate, " +
                "displayForExtend=$displayForExtend, " +
                "positionTypeForExtend=$positionTypeForExtend, " +
                "directionForExtend=$directionForExtend, " +
                "flexDirectionForExtend=$flexDirectionForExtend, " +
                "flexWrapForExtend=$flexWrapForExtend, " +
                "overflowForExtend=$overflowForExtend, " +
                "alignItemsForExtend=$alignItemsForExtend, " +
                "alignSelfForExtend=$alignSelfForExtend, " +
                "alignContentForExtend=$alignContentForExtend, " +
                "justifyContentForExtend=$justifyContentForExtend, " +
                "positionForExtend=$positionForExtend, " +
                "marginForExtend=$marginForExtend, " +
                "paddingForExtend=$paddingForExtend, " +
                "borderForExtend=$borderForExtend, " +
                "flexGrowForExtend=$flexGrowForExtend, " +
                "flexShrinkForExtend=$flexShrinkForExtend, " +
                "flexBasisForExtend=$flexBasisForExtend, " +
                "sizeForExtend=$sizeForExtend, " +
                "minSizeForExtend=$minSizeForExtend, " +
                "maxSizeForExtend=$maxSizeForExtend, " +
                "aspectRatioForExtend=$aspectRatioForExtend, " +
                "maxSizeForFinal=$maxSizeForFinal, " +
                "minSizeForFinal=$minSizeForFinal, " +
                "sizeForFinal=$sizeForFinal, " +
                "borderForFinal=$borderForFinal, " +
                "paddingForFinal=$paddingForFinal, " +
                "marginForFinal=$marginForFinal, " +
                "positionForFinal=$positionForFinal, " +
                "display=$display, " +
                "positionType=$positionType, " +
                "direction=$direction, " +
                "flexDirection=$flexDirection, " +
                "flexWrap=$flexWrap, " +
                "overflow=$overflow, " +
                "alignItems=$alignItems, " +
                "alignSelf=$alignSelf, " +
                "alignContent=$alignContent, " +
                "justifyContent=$justifyContent, " +
                "flexGrow=$flexGrow, " +
                "flexShrink=$flexShrink, " +
                "aspectRatio=$aspectRatio, " +
                "flexBasis=$flexBasis, " +
                "maxSize=$maxSize, " +
                "maxSizeForDimension=$maxSizeForDimension, " +
                "minSize=$minSize, " +
                "minSizeForDimension=$minSizeForDimension, " +
                "size=$size, " +
                "sizeForDimension=$sizeForDimension, " +
                "border=$border, " +
                "borderForDimension=$borderForDimension, " +
                "padding=$padding, " +
                "paddingForDimension=$paddingForDimension, " +
                "margin=$margin, " +
                "marginForDimension=$marginForDimension, " +
                "position=$position, " +
                "positionForDimension=$positionForDimension" +
                ")"
    }

    val display: Display?
        get() {
            return displayForExtend ?: displayForTemplate
        }

    val positionType: PositionType?
        get() {
            return positionTypeForExtend ?: positionTypeForTemplate
        }

    val direction: Direction?
        get() {
            return directionForExtend ?: directionForTemplate
        }

    val flexDirection: FlexDirection?
        get() {
            return flexDirectionForExtend ?: flexDirectionForTemplate
        }

    val flexWrap: FlexWrap?
        get() {
            return flexWrapForExtend ?: flexWrapForTemplate
        }

    val overflow: Overflow?
        get() {
            return overflowForExtend ?: overflowForTemplate
        }

    val alignItems: AlignItems?
        get() {
            return alignItemsForExtend ?: alignItemsForTemplate
        }

    val alignSelf: AlignSelf?
        get() {
            return alignSelfForExtend ?: alignSelfForTemplate
        }

    val alignContent: AlignContent?
        get() {
            return alignContentForExtend ?: alignContentForTemplate
        }

    val justifyContent: JustifyContent?
        get() {
            return justifyContentForExtend ?: justifyContentForTemplate
        }

    val flexGrow: Float?
        get() {
            return flexGrowForExtend ?: flexGrowForTemplate
        }

    val flexShrink: Float?
        get() {
            return flexShrinkForExtend ?: flexShrinkForTemplate
        }

    val aspectRatio: Float?
        get() {
            return aspectRatioForExtend ?: aspectRatioForTemplate
        }

    val flexBasis: GXSize?
        get() {
            return flexBasisForExtend ?: flexBasisForTemplate
        }

    var _maxSize: Size<GXSize?>? = null
    val maxSize: Size<GXSize?>?
        get() {
            val forExtend = maxSizeForExtend
            val forTemplate = maxSizeForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (_maxSize == null) {
                    _maxSize = Size(
                        forExtend?.width ?: forTemplate?.width,
                        forExtend?.height ?: forTemplate?.height
                    )
                    _maxSize
                } else {
                    _maxSize
                }
            } else {
                null
            }
        }

    val maxSizeForDimension: Size<Dimension>?
        get() {
            val forExtend = maxSizeForExtend
            val forTemplate = maxSizeForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (maxSizeForFinal == null) {
                    maxSizeForFinal = Size(
                        forExtend?.width?.valueDimension ?: forTemplate?.width?.valueDimension
                        ?: Dimension.Auto,
                        forExtend?.height?.valueDimension ?: forTemplate?.height?.valueDimension
                        ?: Dimension.Auto
                    )
                    maxSizeForFinal
                } else {
                    maxSizeForFinal
                }
            } else {
                null
            }
        }

    var _minSize: Size<GXSize?>? = null
    val minSize: Size<GXSize?>?
        get() {
            val forExtend = minSizeForExtend
            val forTemplate = minSizeForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (_minSize == null) {
                    _minSize = Size(
                        forExtend?.width ?: forTemplate?.width,
                        forExtend?.height ?: forTemplate?.height
                    )
                    _minSize
                } else {
                    _minSize
                }
            } else {
                null
            }
        }

    val minSizeForDimension: Size<Dimension>?
        get() {
            val forExtend = minSizeForExtend
            val forTemplate = minSizeForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (minSizeForFinal == null) {
                    minSizeForFinal = Size(
                        forExtend?.width?.valueDimension ?: forTemplate?.width?.valueDimension
                        ?: Dimension.Auto,
                        forExtend?.height?.valueDimension ?: forTemplate?.height?.valueDimension
                        ?: Dimension.Auto
                    )
                    minSizeForFinal
                } else {
                    minSizeForFinal
                }
            } else {
                null
            }
        }

    var _size: Size<GXSize?>? = null
    val size: Size<GXSize?>?
        get() {
            val forExtend = sizeForExtend
            val forTemplate = sizeForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (_size == null) {
                    _size = Size(
                        forExtend?.width ?: forTemplate?.width,
                        forExtend?.height ?: forTemplate?.height
                    )
                    _size
                } else {
                    _size
                }
            } else {
                null
            }
        }

    val sizeForDimension: Size<Dimension>?
        get() {
            val forExtend = sizeForExtend
            val forTemplate = sizeForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (sizeForFinal == null) {
                    sizeForFinal = Size(
                        forExtend?.width?.valueDimension ?: forTemplate?.width?.valueDimension
                        ?: Dimension.Auto,
                        forExtend?.height?.valueDimension ?: forTemplate?.height?.valueDimension
                        ?: Dimension.Auto
                    )
                    sizeForFinal
                } else {
                    sizeForFinal
                }
            } else {
                null
            }
        }

    private var _border: Rect<GXSize?>? = null
    val border: Rect<GXSize?>?
        get() {
            val forExtend = borderForExtend
            val forTemplate = borderForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (_border == null) {
                    _border = Rect(
                        forExtend?.start ?: forTemplate?.start,
                        forExtend?.end ?: forTemplate?.end,
                        forExtend?.top ?: forTemplate?.top,
                        forExtend?.bottom ?: forTemplate?.bottom
                    )
                    _border
                } else {
                    _border
                }
            } else {
                null
            }
        }


    val borderForDimension: Rect<Dimension>?
        get() {
            val forExtend = borderForExtend
            val forTemplate = borderForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (borderForFinal == null) {
                    borderForFinal = Rect(
                        forExtend?.start?.valueDimension ?: forTemplate?.start?.valueDimension
                        ?: Dimension.Undefined,
                        forExtend?.end?.valueDimension ?: forTemplate?.end?.valueDimension
                        ?: Dimension.Undefined,
                        forExtend?.top?.valueDimension ?: forTemplate?.top?.valueDimension
                        ?: Dimension.Undefined,
                        forExtend?.bottom?.valueDimension ?: forTemplate?.bottom?.valueDimension
                        ?: Dimension.Undefined
                    )
                    borderForFinal
                } else {
                    borderForFinal
                }
            } else {
                null
            }
        }

    private var _padding: Rect<GXSize?>? = null
    val padding: Rect<GXSize?>?
        get() {
            val forExtend = paddingForExtend
            val forTemplate = paddingForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (_padding == null) {
                    _padding = Rect(
                        forExtend?.start ?: forTemplate?.start,
                        forExtend?.end ?: forTemplate?.end,
                        forExtend?.top ?: forTemplate?.top,
                        forExtend?.bottom ?: forTemplate?.bottom
                    )
                    _padding
                } else {
                    _padding
                }
            } else {
                null
            }
        }

    val paddingForDimension: Rect<Dimension>?
        get() {
            val forExtend = paddingForExtend
            val forTemplate = paddingForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (paddingForFinal == null) {
                    paddingForFinal = Rect(
                        forExtend?.start?.valueDimension ?: forTemplate?.start?.valueDimension
                        ?: Dimension.Undefined,
                        forExtend?.end?.valueDimension ?: forTemplate?.end?.valueDimension
                        ?: Dimension.Undefined,
                        forExtend?.top?.valueDimension ?: forTemplate?.top?.valueDimension
                        ?: Dimension.Undefined,
                        forExtend?.bottom?.valueDimension ?: forTemplate?.bottom?.valueDimension
                        ?: Dimension.Undefined
                    )
                    paddingForFinal
                } else {
                    paddingForFinal
                }
            } else {
                null
            }
        }

    private var _margin: Rect<GXSize?>? = null
    val margin: Rect<GXSize?>?
        get() {
            val forExtend = positionForExtend
            val forTemplate = positionForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (_margin == null) {
                    _margin = Rect(
                        forExtend?.start ?: forTemplate?.start,
                        forExtend?.end ?: forTemplate?.end,
                        forExtend?.top ?: forTemplate?.top,
                        forExtend?.bottom ?: forTemplate?.bottom
                    )
                    _margin
                } else {
                    _margin
                }
            } else {
                null
            }
        }

    val marginForDimension: Rect<Dimension>?
        get() {
            val forExtend = marginForExtend
            val forTemplate = marginForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (marginForFinal == null) {
                    marginForFinal = Rect(
                        forExtend?.start?.valueDimension ?: forTemplate?.start?.valueDimension
                        ?: Dimension.Undefined,
                        forExtend?.end?.valueDimension ?: forTemplate?.end?.valueDimension
                        ?: Dimension.Undefined,
                        forExtend?.top?.valueDimension ?: forTemplate?.top?.valueDimension
                        ?: Dimension.Undefined,
                        forExtend?.bottom?.valueDimension ?: forTemplate?.bottom?.valueDimension
                        ?: Dimension.Undefined
                    )
                    marginForFinal
                } else {
                    marginForFinal
                }
            } else {
                null
            }
        }

    private var _position: Rect<GXSize?>? = null
    val position: Rect<GXSize?>?
        get() {
            val forExtend = positionForExtend
            val forTemplate = positionForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (_position == null) {
                    _position = Rect(
                        forExtend?.start ?: forTemplate?.start,
                        forExtend?.end ?: forTemplate?.end,
                        forExtend?.top ?: forTemplate?.top,
                        forExtend?.bottom ?: forTemplate?.bottom
                    )
                    _position
                } else {
                    _position
                }
            } else {
                null
            }
        }

    val positionForDimension: Rect<Dimension>?
        get() {
            val forExtend = positionForExtend
            val forTemplate = positionForTemplate
            return if (forExtend != null || forTemplate != null) {
                if (positionForFinal == null) {
                    positionForFinal = Rect(
                        forExtend?.start?.valueDimension ?: forTemplate?.start?.valueDimension
                        ?: Dimension.Undefined,
                        forExtend?.end?.valueDimension ?: forTemplate?.end?.valueDimension
                        ?: Dimension.Undefined,
                        forExtend?.top?.valueDimension ?: forTemplate?.top?.valueDimension
                        ?: Dimension.Undefined,
                        forExtend?.bottom?.valueDimension ?: forTemplate?.bottom?.valueDimension
                        ?: Dimension.Undefined
                    )
                    positionForFinal
                } else {
                    positionForFinal
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
                val value = it.value
                if (value == null) {
                    if (Log.isLog()) {
                        Log.e("GXFlexBox.create @forEach, key=$key, value=$value")
                    }
                    return@forEach
                }
                when (key) {
                    GXTemplateKey.FLEXBOX_DISPLAY -> gxFlexBox.displayForTemplate =
                        GXFlexBoxConvert.display(value.toString())

                    GXTemplateKey.FLEXBOX_POSITION_TYPE -> gxFlexBox.positionTypeForTemplate =
                        GXFlexBoxConvert.positionType(value.toString())

                    GXTemplateKey.FLEXBOX_DIRECTION -> gxFlexBox.directionForTemplate =
                        GXFlexBoxConvert.direction(value.toString())

                    GXTemplateKey.FLEXBOX_FLEX_DIRECTION -> gxFlexBox.flexDirectionForTemplate =
                        GXFlexBoxConvert.flexDirection(value.toString())

                    GXTemplateKey.FLEXBOX_FLEX_WRAP -> gxFlexBox.flexWrapForTemplate =
                        GXFlexBoxConvert.flexWrap(value.toString())

                    GXTemplateKey.FLEXBOX_OVERFLOW -> gxFlexBox.overflowForTemplate =
                        GXFlexBoxConvert.overflow(value.toString())

                    GXTemplateKey.FLEXBOX_ALIGN_ITEMS -> gxFlexBox.alignItemsForTemplate =
                        GXFlexBoxConvert.alignItems(value.toString())

                    GXTemplateKey.FLEXBOX_ALIGN_SELF -> gxFlexBox.alignSelfForTemplate =
                        GXFlexBoxConvert.alignSelf(value.toString())

                    GXTemplateKey.FLEXBOX_ALIGN_CONTENT -> gxFlexBox.alignContentForTemplate =
                        GXFlexBoxConvert.alignContent(value.toString())

                    GXTemplateKey.FLEXBOX_JUSTIFY_CONTENT -> gxFlexBox.justifyContentForTemplate =
                        GXFlexBoxConvert.justifyContent(value.toString())

                    GXTemplateKey.FLEXBOX_POSITION_LEFT, GXTemplateKey.FLEXBOX_POSITION_RIGHT, GXTemplateKey.FLEXBOX_POSITION_TOP, GXTemplateKey.FLEXBOX_POSITION_BOTTOM -> if (gxFlexBox.positionForTemplate == null) gxFlexBox.positionForTemplate =
                        GXFlexBoxConvert.position(css)

                    GXTemplateKey.FLEXBOX_MARGIN, GXTemplateKey.FLEXBOX_MARGIN_LEFT, GXTemplateKey.FLEXBOX_MARGIN_RIGHT, GXTemplateKey.FLEXBOX_MARGIN_TOP, GXTemplateKey.FLEXBOX_MARGIN_BOTTOM -> if (gxFlexBox.marginForTemplate == null) gxFlexBox.marginForTemplate =
                        GXFlexBoxConvert.margin(css)

                    GXTemplateKey.FLEXBOX_PADDING, GXTemplateKey.FLEXBOX_PADDING_LEFT, GXTemplateKey.FLEXBOX_PADDING_RIGHT, GXTemplateKey.FLEXBOX_PADDING_TOP, GXTemplateKey.FLEXBOX_PADDING_BOTTOM -> if (gxFlexBox.paddingForTemplate == null) gxFlexBox.paddingForTemplate =
                        GXFlexBoxConvert.padding(css)

                    GXTemplateKey.FLEXBOX_BORDER, GXTemplateKey.FLEXBOX_BORDER_LEFT, GXTemplateKey.FLEXBOX_BORDER_RIGHT, GXTemplateKey.FLEXBOX_BORDER_TOP, GXTemplateKey.FLEXBOX_BORDER_BOTTOM -> if (gxFlexBox.borderForTemplate == null) gxFlexBox.borderForTemplate =
                        GXFlexBoxConvert.border(css)

                    GXTemplateKey.FLEXBOX_FLEX_BASIS -> gxFlexBox.flexBasisForTemplate =
                        GXFlexBoxConvert.flexBasis(value.toString())

                    GXTemplateKey.FLEXBOX_SIZE_WIDTH, GXTemplateKey.FLEXBOX_SIZE_HEIGHT -> if (gxFlexBox.sizeForTemplate == null) gxFlexBox.sizeForTemplate =
                        GXFlexBoxConvert.size2(css)

                    GXTemplateKey.FLEXBOX_MIN_WIDTH, GXTemplateKey.FLEXBOX_MIN_HEIGHT -> if (gxFlexBox.minSizeForTemplate == null) gxFlexBox.minSizeForTemplate =
                        GXFlexBoxConvert.minSize2(css)

                    GXTemplateKey.FLEXBOX_MAX_WIDTH, GXTemplateKey.FLEXBOX_MAX_HEIGHT -> if (gxFlexBox.maxSizeForTemplate == null) gxFlexBox.maxSizeForTemplate =
                        GXFlexBoxConvert.maxSize2(css)

                    GXTemplateKey.FLEXBOX_ASPECT_RATIO -> gxFlexBox.aspectRatioForTemplate =
                        GXFlexBoxConvert.aspectRatio(value.toString())

                    GXTemplateKey.FLEXBOX_FLEX_GROW -> gxFlexBox.flexGrowForTemplate =
                        GXFlexBoxConvert.flexGrow(value.toString())

                    GXTemplateKey.FLEXBOX_FLEX_SHRINK -> gxFlexBox.flexShrinkForTemplate =
                        GXFlexBoxConvert.flexShrink(value.toString())
                }
            }

            return gxFlexBox
        }

    }
}