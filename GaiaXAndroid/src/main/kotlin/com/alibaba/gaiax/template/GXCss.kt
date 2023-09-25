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
import com.alibaba.gaiax.context.GXTemplateContext

/**
 * @suppress
 */
data class GXCss(
    val style: GXStyle, val flexBox: GXFlexBox
) {

    fun updateByExtend(gxTemplateContext: GXTemplateContext, extendCssData: JSONObject) {
        style.updateByExtend(extendCssData)
        flexBox.updateByExtend(gxTemplateContext, extendCssData)
    }

    fun updateByVisual(visual: GXCss?) {
        if (visual != null) {
            style.updateByVisual(visual.style)
            flexBox.updateByVisual(visual.flexBox)
        }
    }

    override fun toString(): String {
        return "GXCss(style=$style, flexBox=$flexBox)"
    }

    companion object {

        fun create(data: JSONObject = JSONObject()): GXCss {
            return GXCss(GXStyle.create(data), GXFlexBox.create(data))
        }
    }
}