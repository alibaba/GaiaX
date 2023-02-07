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
    private val scrollTimeInterval: Long,
    private val infinityScroll: Boolean,
    private val hasIndicator: Boolean,
    private val selectedIndex: Int,
    private val indicatorSelectedColor: GXColor,
    private val indicatorUnselectedColor: GXColor,
    private val indicatorMargin: Rect<GXSize>,
    private val indicatorPosition: GXSliderView.IndicatorPosition,
    private val indicatorClass: String?
) {
    private var _scrollTimeInterval: Long? = null
    private var _infinityScroll: Boolean? = null
    private var _hasIndicator: Boolean? = null
    private var _selectedIndex: Int? = null
    private var _indicatorSelectedColor: GXColor? = null
    private var _indicatorUnselectedColor: GXColor? = null
    private var _indicatorMargin: Rect<GXSize>? = null
    private var _indicatorPosition: GXSliderView.IndicatorPosition? = null
    private var _indicatorClass: String? = null

    fun reset() {
        _scrollTimeInterval = null
        _infinityScroll = null
        _hasIndicator = null
        _selectedIndex = null
        _indicatorSelectedColor = null
        _indicatorUnselectedColor = null
        _indicatorMargin = null
        _indicatorPosition = null
        _indicatorClass = null
    }

    val scrollTimeIntervalFinal: Long
        get() {
            return _scrollTimeInterval ?: scrollTimeInterval
        }

    val infinityScrollFinal: Boolean
        get() {
            return _infinityScroll ?: infinityScroll
        }

    val hasIndicatorFinal: Boolean
        get() {
            return _hasIndicator ?: hasIndicator
        }

    val selectedIndexFinal: Int
        get() {
            return _selectedIndex ?: selectedIndex
        }

    val indicatorSelectedColorFinal: GXColor
        get() {
            return _indicatorSelectedColor ?: indicatorSelectedColor
        }

    val indicatorUnselectedColorFinal: GXColor
        get() {
            return _indicatorUnselectedColor ?: indicatorUnselectedColor
        }

    val indicatorMarginFinal: Rect<GXSize>
        get() {
            return _indicatorMargin ?: indicatorMargin
        }
    val indicatorPositionFinal: GXSliderView.IndicatorPosition
        get() {
            return _indicatorPosition ?: indicatorPosition
        }

    val indicatorClassFinal: String?
        get() {
            return _indicatorClass ?: indicatorClass
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
            _scrollTimeInterval = scrollTimeInterval
        }
        if (infinityScroll != null) {
            _infinityScroll = infinityScroll
        }
        if (hasIndicator != null) {
            _hasIndicator = hasIndicator
        }
        if (selectedIndex != null) {
            _selectedIndex = selectedIndex
        }
        if (indicatorSelectedColor != null) {
            _indicatorSelectedColor = indicatorSelectedColor
        }
        if (indicatorUnselectedColor != null) {
            _indicatorUnselectedColor = indicatorUnselectedColor
        }
        if (indicatorMargin != null) {
            _indicatorMargin = indicatorMargin
        }
        if (indicatorPosition != null) {
            _indicatorPosition = indicatorPosition
        }
        if (indicatorClass != null) {
            _indicatorClass = indicatorClass
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
