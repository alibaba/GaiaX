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
    internal val strokeColorForTemplate: GXColor,
    internal val trailColorForTemplate: GXColor,
    internal val progressTypeForTemplate: String,
    internal val animatedForTemplate: Boolean
) {

    private var strokeColorForExtend: GXColor? = null
    private var trailColorForExtend: GXColor? = null
    private var progressTypeForExtend: String? = null
    private var animatedForExtend: Boolean? = null

    fun reset() {
        strokeColorForExtend = null
        trailColorForExtend = null
        progressTypeForExtend = null
        animatedForExtend = null
    }

    val strokeColor: GXColor
        get() {
            return strokeColorForExtend ?: strokeColorForTemplate
        }
    val trailColor: GXColor
        get() {
            return trailColorForExtend ?: trailColorForTemplate
        }
    val progressType: String
        get() {
            return progressTypeForExtend ?: progressTypeForTemplate
        }
    val animated: Boolean
        get() {
            return animatedForExtend ?: animatedForTemplate
        }

    fun updateByExtend(extendCssData: JSONObject) {
        val strokeColor =
            extendCssData.getString(GXTemplateKey.GAIAX_LAYER_PROGRESS_STROKE_COLOR)?.let {
                GXColor.create(it)
            }
        val trailColor =
            extendCssData.getString(GXTemplateKey.GAIAX_LAYER_PROGRESS_TRAIL_COLOR)?.let {
                GXColor.create(it)
            }
        val progressType = extendCssData.getString(GXTemplateKey.GAIAX_LAYER_PROGRESS_TYPE)
        val animated = extendCssData.getBoolean(GXTemplateKey.GAIAX_LAYER_PROGRESS_ANIMATED)
        if (strokeColor != null) {
            strokeColorForExtend = strokeColor
        }
        if (trailColor != null) {
            trailColorForExtend = trailColor
        }
        if (progressType != null) {
            progressTypeForExtend = progressType
        }
        if (animated != null) {
            animatedForExtend = animated
        }
    }

    companion object {
        fun create(data: JSONObject): GXProgressConfig {
            val strokeColor = data.getString(GXTemplateKey.GAIAX_LAYER_PROGRESS_STROKE_COLOR)?.let {
                GXColor.create(it)
            } ?: GXColor.createHex("#0000FF")
            val trailColor = data.getString(GXTemplateKey.GAIAX_LAYER_PROGRESS_TRAIL_COLOR)?.let {
                GXColor.create(it)
            } ?: GXColor.createHex("#BBBBBB")
            val progressType = data.getString(GXTemplateKey.GAIAX_LAYER_PROGRESS_TYPE) ?: "line"
            val animated = data.getBoolean(GXTemplateKey.GAIAX_LAYER_PROGRESS_ANIMATED) ?: true
            return GXProgressConfig(strokeColor, trailColor, progressType, animated)
        }
    }
}
