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

import android.graphics.Rect
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * @suppress
 */
object GXContainerConvert {

    fun direction(direction: String): Int = when (direction) {
        GXTemplateKey.GAIAX_HORIZONTAL -> androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
        GXTemplateKey.GAIAX_VERTICAL -> androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        else -> androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
    }

    fun edgeInsets(edgeInsets: String?): Rect? = if (edgeInsets?.isNotEmpty() == true) {
        val edge = edgeInsets.replace("{", "").replace("}", "").split(",")
        val top = GXSize.create(edge[0] + "px").valueInt
        val left = GXSize.create(edge[1] + "px").valueInt
        val bottom = GXSize.create(edge[2] + "px").valueInt
        val right = GXSize.create(edge[3] + "px").valueInt
        Rect(left, top, right, bottom)
    } else {
        null
    }

    fun spacing(itemSpacing: String?): Int {
        return itemSpacing?.let { GXSize.create(it + "px").valueInt } ?: 0
    }
}