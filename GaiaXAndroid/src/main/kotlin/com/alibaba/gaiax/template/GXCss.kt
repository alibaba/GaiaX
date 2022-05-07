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
data class GXCss(
    val style: GXStyle,
    val flexBox: GXFlexBox
) {


    companion object {
        fun create(lowPriorityCss: GXCss, highPriorityCss: GXCss?): GXCss {
            if (highPriorityCss == null) {
                return lowPriorityCss
            }
            return GXCss(
                GXStyle.create(lowPriorityCss.style, highPriorityCss.style),
                GXFlexBox.create(lowPriorityCss.flexBox, highPriorityCss.flexBox)
            )
        }

        fun create(data: JSONObject = JSONObject()): GXCss {
            return GXCss(GXStyle.create(data), GXFlexBox.create(data))
        }

        fun createByExtend(data: JSONObject = JSONObject()): GXCss {
            return GXCss(GXStyle.create(data), GXFlexBox.createByExtend(data))
        }
    }
}