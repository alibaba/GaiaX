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

import com.alibaba.fastjson.JSONObject

/**
 * @suppress
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
