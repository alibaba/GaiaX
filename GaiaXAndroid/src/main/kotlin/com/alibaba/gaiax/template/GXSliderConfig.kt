package com.alibaba.gaiax.template;

import com.alibaba.fastjson.JSONObject

/**
 * @author guaiyu
 * @date 2022/5/9 10:09
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
