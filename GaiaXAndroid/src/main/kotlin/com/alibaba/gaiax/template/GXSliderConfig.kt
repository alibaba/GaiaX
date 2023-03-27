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

import app.visly.stretch.Rect
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.container.slider.GXSliderView

/**
 * @suppress
 */
data class GXSliderConfig(
    internal val scrollTimeIntervalForTemplate: Long,
    internal val infinityScrollForTemplate: Boolean,
    internal val hasIndicatorForTemplate: Boolean,
    internal val selectedIndexForTemplate: Int,
    internal val indicatorSelectedColorForTemplate: GXColor,
    internal val indicatorUnselectedColorForTemplate: GXColor,
    internal val indicatorMarginForTemplate: Rect<GXSize>,
    internal val indicatorPositionForTemplate: GXSliderView.IndicatorPosition,
    internal val indicatorClassForTemplate: String?
) {
    private var scrollTimeIntervalForExtend: Long? = null
    private var infinityScrollForExtend: Boolean? = null
    private var hasIndicatorForExtend: Boolean? = null
    private var selectedIndexForExtend: Int? = null
    private var indicatorSelectedColorForExtend: GXColor? = null
    private var indicatorUnselectedColorForExtend: GXColor? = null
    private var indicatorMarginForExtend: Rect<GXSize>? = null
    private var indicatorPositionForExtend: GXSliderView.IndicatorPosition? = null
    private var indicatorClassForExtend: String? = null

    fun reset() {
        scrollTimeIntervalForExtend = null
        infinityScrollForExtend = null
        hasIndicatorForExtend = null
        selectedIndexForExtend = null
        indicatorSelectedColorForExtend = null
        indicatorUnselectedColorForExtend = null
        indicatorMarginForExtend = null
        indicatorPositionForExtend = null
        indicatorClassForExtend = null
    }

    val scrollTimeInterval: Long
        get() {
            return scrollTimeIntervalForExtend ?: scrollTimeIntervalForTemplate
        }

    val infinityScroll: Boolean
        get() {
            return infinityScrollForExtend ?: infinityScrollForTemplate
        }

    val hasIndicator: Boolean
        get() {
            return hasIndicatorForExtend ?: hasIndicatorForTemplate
        }

    val selectedIndex: Int
        get() {
            return selectedIndexForExtend ?: selectedIndexForTemplate
        }

    val indicatorSelectedColor: GXColor
        get() {
            return indicatorSelectedColorForExtend ?: indicatorSelectedColorForTemplate
        }

    val indicatorUnselectedColor: GXColor
        get() {
            return indicatorUnselectedColorForExtend ?: indicatorUnselectedColorForTemplate
        }

    val indicatorMargin: Rect<GXSize>
        get() {
            return indicatorMarginForExtend ?: indicatorMarginForTemplate
        }

    val indicatorPosition: GXSliderView.IndicatorPosition
        get() {
            return indicatorPositionForExtend ?: indicatorPositionForTemplate
        }

    val indicatorClass: String?
        get() {
            return indicatorClassForExtend ?: indicatorClassForTemplate
        }

    fun updateByExtend(extendCssData: JSONObject) {
        val scrollTimeInterval =
            extendCssData.getLong(GXTemplateKey.GAIAX_LAYER_SLIDER_SCROLL_TIME_INTERVAL)
        val infinityScroll =
            getBoolean(extendCssData, GXTemplateKey.GAIAX_LAYER_SLIDER_INFINITY_SCROLL)
        val hasIndicator = getBoolean(extendCssData, GXTemplateKey.GAIAX_LAYER_SLIDER_HAS_INDICATOR)
        val selectedIndex =
            extendCssData.getInteger(GXTemplateKey.GAIAX_LAYER_SLIDER_SELECTED_INDEX)
        val indicatorSelectedColor =
            extendCssData.getString(GXTemplateKey.GAIAX_LAYER_SLIDER_INDICATOR_SELECTED_COLOR)
                ?.let {
                    GXColor.create(it)
                }
        val indicatorUnselectedColor =
            extendCssData.getString(GXTemplateKey.GAIAX_LAYER_SLIDER_INDICATOR_UNSELECTED_COLOR)
                ?.let {
                    GXColor.create(it)
                }
        val indicatorMargin =
            extendCssData.getString(GXTemplateKey.GAIAX_LAYER_SLIDER_INDICATOR_MARGIN)?.let {
                GXContainerConvert.edgeInsets(it)
            }
        val indicatorPosition =
            extendCssData.getString(GXTemplateKey.GAIAX_LAYER_SLIDER_INDICATOR_POSITION)?.let {
                GXSliderView.IndicatorPosition.fromValue(it)
            }
        val indicatorClass =
            extendCssData.getString(GXTemplateKey.GAIAX_LAYER_SLIDER_INDICATOR_CLASS)

        if (scrollTimeInterval != null) {
            scrollTimeIntervalForExtend = scrollTimeInterval
        }
        if (infinityScroll != null) {
            infinityScrollForExtend = infinityScroll
        }
        if (hasIndicator != null) {
            hasIndicatorForExtend = hasIndicator
        }
        if (selectedIndex != null) {
            selectedIndexForExtend = selectedIndex
        }
        if (indicatorSelectedColor != null) {
            indicatorSelectedColorForExtend = indicatorSelectedColor
        }
        if (indicatorUnselectedColor != null) {
            indicatorUnselectedColorForExtend = indicatorUnselectedColor
        }
        if (indicatorMargin != null) {
            indicatorMarginForExtend = indicatorMargin
        }
        if (indicatorPosition != null) {
            indicatorPositionForExtend = indicatorPosition
        }
        if (indicatorClass != null) {
            indicatorClassForExtend = indicatorClass
        }
    }

    companion object {

        fun create(data: JSONObject): GXSliderConfig {
            val scrollTimeInterval =
                data.getLong(GXTemplateKey.GAIAX_LAYER_SLIDER_SCROLL_TIME_INTERVAL) ?: 3000
            val infinityScroll =
                getBoolean(data, GXTemplateKey.GAIAX_LAYER_SLIDER_INFINITY_SCROLL) ?: true
            val hasIndicator =
                getBoolean(data, GXTemplateKey.GAIAX_LAYER_SLIDER_HAS_INDICATOR) ?: true
            val selectedIndex =
                data.getInteger(GXTemplateKey.GAIAX_LAYER_SLIDER_SELECTED_INDEX) ?: 0
            val indicatorSelectedColor =
                data.getString(GXTemplateKey.GAIAX_LAYER_SLIDER_INDICATOR_SELECTED_COLOR)?.let {
                    GXColor.create(it)
                } ?: GXColor.createHex("#FFFFFF")
            val indicatorUnselectedColor =
                data.getString(GXTemplateKey.GAIAX_LAYER_SLIDER_INDICATOR_UNSELECTED_COLOR)?.let {
                    GXColor.create(it)
                } ?: GXColor.createHex("#BBBBBB")
            val indicatorMargin =
                data.getString(GXTemplateKey.GAIAX_LAYER_SLIDER_INDICATOR_MARGIN)?.let {
                    GXContainerConvert.edgeInsets(it)
                } ?: Rect<GXSize>(
                    GXSize.Undefined, GXSize.Undefined, GXSize.Undefined, GXSize.Undefined
                )
            val indicatorPosition =
                GXSliderView.IndicatorPosition.fromValue(data.getString(GXTemplateKey.GAIAX_LAYER_SLIDER_INDICATOR_POSITION))
            val indicatorClass = data.getString(GXTemplateKey.GAIAX_LAYER_SLIDER_INDICATOR_CLASS)

            return GXSliderConfig(
                scrollTimeInterval,
                infinityScroll,
                hasIndicator,
                selectedIndex,
                indicatorSelectedColor,
                indicatorUnselectedColor,
                indicatorMargin,
                indicatorPosition,
                indicatorClass
            )
        }


        private fun getBoolean(data: JSONObject, key: String): Boolean? {
            if (data.containsKey(key)) {
                return data.getBoolean(key)
            }
            return null
        }
    }
}
