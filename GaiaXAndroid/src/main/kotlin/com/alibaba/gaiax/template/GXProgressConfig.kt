package com.alibaba.gaiax.template

import com.alibaba.fastjson.JSONObject

/**
 * @author guaiyu
 * @date 2022/7/7 16:49
 */
data class GXProgressConfig(
    val strokeColor: GXColor,
    val trailColor: GXColor,
    val progressType: String,
    val animated: Boolean
) {
    companion object {
        fun create(data: JSONObject): GXProgressConfig {
            val strokeColor =
                data.getString(GXTemplateKey.GAIAX_LAYER_PROGRESS_STROKE_COLOR)?.let {
                    GXColor.create(it)
                } ?: GXColor.createHex("#0000FF")
            val trailColor =
                data.getString(GXTemplateKey.GAIAX_LAYER_PROGRESS_TRAIL_COLOR)?.let {
                    GXColor.create(it)
                } ?: GXColor.createHex("#BBBBBB")
            val progressType = data.getString(GXTemplateKey.GAIAX_LAYER_PROGRESS_TYPE) ?: "line"
            val animated = data.getBoolean(GXTemplateKey.GAIAX_LAYER_PROGRESS_ANIMATED) ?: true
            return GXProgressConfig(
                strokeColor,
                trailColor,
                progressType,
                animated
            )
        }

        fun create(srcConfig: GXProgressConfig, data: JSONObject): GXProgressConfig {
            val strokeColor =
                data.getString(GXTemplateKey.GAIAX_LAYER_PROGRESS_STROKE_COLOR)?.let {
                    GXColor.create(it)
                } ?: srcConfig.strokeColor
            val trailColor =
                data.getString(GXTemplateKey.GAIAX_LAYER_PROGRESS_TRAIL_COLOR)?.let {
                    GXColor.create(it)
                } ?: srcConfig.trailColor
            val progressType =
                data.getString(GXTemplateKey.GAIAX_LAYER_PROGRESS_TYPE) ?: srcConfig.progressType
            val animated =
                data.getBoolean(GXTemplateKey.GAIAX_LAYER_PROGRESS_ANIMATED) ?: srcConfig.animated
            return GXProgressConfig(
                strokeColor,
                trailColor,
                progressType,
                animated
            )
        }
    }
}
