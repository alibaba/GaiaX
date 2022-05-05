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

/**
 * @suppress
 */
class GXRoundedCorner(
    var topLeft: GXSize?,
    var topRight: GXSize?,
    var bottomLeft: GXSize?,
    var bottomRight: GXSize?
) {

    private fun toFloatArray(): FloatArray {
        // top-left, top-right, bottom-right, bottom-left
        val result = FloatArray(8)
        val tl = topLeft?.valueFloat ?: 0F
        val tr = topRight?.valueFloat ?: 0F
        val bl = bottomLeft?.valueFloat ?: 0F
        val br = bottomRight?.valueFloat ?: 0F
        result[0] = tl
        result[1] = tl
        result[2] = tr
        result[3] = tr
        result[4] = br
        result[5] = br
        result[6] = bl
        result[7] = bl
        return result
    }

    fun isSameRadius(): Boolean {
        return topLeft?.valueFloat == topRight?.valueFloat && topRight?.valueFloat == bottomLeft?.valueFloat && bottomLeft?.valueFloat == bottomRight?.valueFloat
    }

    val value: FloatArray
        get() = toFloatArray()
}