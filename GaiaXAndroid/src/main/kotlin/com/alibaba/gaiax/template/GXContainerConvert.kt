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

import androidx.recyclerview.widget.LinearLayoutManager
import app.visly.stretch.Rect

/**
 * @suppress
 */
object GXContainerConvert {

    fun direction(direction: String): Int = when (direction) {
        GXTemplateKey.GAIAX_HORIZONTAL -> LinearLayoutManager.HORIZONTAL
        GXTemplateKey.GAIAX_VERTICAL -> LinearLayoutManager.VERTICAL
        else -> LinearLayoutManager.VERTICAL
    }

    fun edgeInsets(edgeInsets: String?): Rect<GXSize>? = if (edgeInsets?.isNotEmpty() == true) {
        val edge = edgeInsets.replace("{", "").replace("}", "").split(",")
        val top = GXSize.create(edge[0])
        val left = GXSize.create(edge[1])
        val bottom = GXSize.create(edge[2])
        val right = GXSize.create(edge[3])
        Rect(left, right, top, bottom)
    } else {
        null
    }

    fun edgeInsets2(edgeInsets: String?): android.graphics.Rect? = if (edgeInsets?.isNotEmpty() == true) {
        val edge = edgeInsets.replace("{", "").replace("}", "").split(",")
        val top = GXSize.create(edge[0]).valueInt
        val left = GXSize.create(edge[1]).valueInt
        val bottom = GXSize.create(edge[2]).valueInt
        val right = GXSize.create(edge[3]).valueInt
        android.graphics.Rect(left, right, top, bottom)
    } else {
        null
    }

    fun spacing(target: String?): Int {
        return target?.let { GXSize.create(it).valueInt } ?: 0
    }
}
