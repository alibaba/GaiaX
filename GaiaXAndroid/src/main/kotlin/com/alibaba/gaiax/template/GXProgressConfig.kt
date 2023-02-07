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
    private val strokeColor: GXColor,
    private val trailColor: GXColor,
    private val progressType: String,
    private val animated: Boolean
) {
    private var _strokeColor: GXColor? = null
    private var _trailColor: GXColor? = null
    private var _progressType: String? = null
    private var _animated: Boolean? = null

    fun reset() {
        _strokeColor = null
        _trailColor = null
        _progressType = null
        _animated = null
    }

    val strokeColorFinal: GXColor
        get() {
            return _strokeColor ?: strokeColor
        }
    val trailColorFinal: GXColor
        get() {
            return _trailColor ?: trailColor
        }
    val progressTypeFinal: String
        get() {
            return _progressType ?: progressType
        }
    val animatedFinal: Boolean
        get() {
            return _animated ?: animated
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
            _strokeColor = strokeColor
        }
        if (trailColor != null) {
            _trailColor = trailColor
        }
        if (progressType != null) {
            _progressType = progressType
        }
        if (animated != null) {
            _animated = animated
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
            return GXProgressConfig(
                strokeColor, trailColor, progressType, animated
            )
        }
    }
}
