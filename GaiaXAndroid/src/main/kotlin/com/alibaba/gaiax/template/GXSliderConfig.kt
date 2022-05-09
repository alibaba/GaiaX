package com.alibaba.gaiax.template;

import com.alibaba.fastjson.JSONObject

/**
 * @author guaiyu
 * @date 2022/5/9 10:09
 */
data class GXSliderConfig(val scrollTimeInterval: Long?) {
    companion object {

        fun create(data: JSONObject): GXSliderConfig {
            val scrollTimeInterval =
                data.getLong(GXTemplateKey.GAIAX_LAYER_SCROLL_TIME_INTERVAL)
            return GXSliderConfig(
                scrollTimeInterval
            )
        }

        fun create(srcConfig: GXSliderConfig, data: JSONObject): GXSliderConfig {
            val scrollTimeInterval =
                data.getLong(GXTemplateKey.GAIAX_LAYER_SCROLL_TIME_INTERVAL)
                    ?: srcConfig.scrollTimeInterval
            return GXSliderConfig(
                scrollTimeInterval
            )
        }
    }
}
