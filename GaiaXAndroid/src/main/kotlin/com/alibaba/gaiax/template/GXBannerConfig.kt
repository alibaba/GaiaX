package com.alibaba.gaiax.template;

import com.alibaba.fastjson.JSONObject

/**
 * @author guaiyu
 * @date 2022/5/9 10:09
 */
data class GXBannerConfig(val scrollTimeInterval: Long?) {
    companion object {

        fun create(data: JSONObject): GXBannerConfig {
            val scrollTimeInterval =
                data.getLong(GXTemplateKey.GAIAX_LAYER_SCROLL_TIME_INTERVAL)
            return GXBannerConfig(
                scrollTimeInterval
            )
        }

        fun create(srcConfig: GXBannerConfig, data: JSONObject): GXBannerConfig {
            val scrollTimeInterval =
                data.getLong(GXTemplateKey.GAIAX_LAYER_SCROLL_TIME_INTERVAL)
                    ?: srcConfig.scrollTimeInterval
            return GXBannerConfig(
                scrollTimeInterval
            )
        }
    }
}
