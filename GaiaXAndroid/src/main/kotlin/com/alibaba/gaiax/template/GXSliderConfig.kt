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

package com.alibaba.gaiax.template;

import com.alibaba.fastjson.JSONObject

/**
 * @suppress
 */
data class GXSliderConfig(
    val scrollTimeInterval: Long,
    val infinityScroll: Boolean,
    val hasIndicator: Boolean,
    val selectedIndex: Int
) {
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
            return GXSliderConfig(
                scrollTimeInterval,
                infinityScroll,
                hasIndicator,
                selectedIndex
            )
        }

        fun create(srcConfig: GXSliderConfig, data: JSONObject): GXSliderConfig {
            val scrollTimeInterval =
                data.getLong(GXTemplateKey.GAIAX_LAYER_SLIDER_SCROLL_TIME_INTERVAL)
                    ?: srcConfig.scrollTimeInterval
            val infinityScroll =
                getBoolean(data, GXTemplateKey.GAIAX_LAYER_SLIDER_INFINITY_SCROLL)
                    ?: srcConfig.infinityScroll
            val hasIndicator =
                getBoolean(data, GXTemplateKey.GAIAX_LAYER_SLIDER_HAS_INDICATOR)
                    ?: srcConfig.hasIndicator
            val selectedIndex =
                data.getInteger(GXTemplateKey.GAIAX_LAYER_SLIDER_SELECTED_INDEX) ?: 0
            return GXSliderConfig(
                scrollTimeInterval,
                infinityScroll,
                hasIndicator,
                selectedIndex
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
