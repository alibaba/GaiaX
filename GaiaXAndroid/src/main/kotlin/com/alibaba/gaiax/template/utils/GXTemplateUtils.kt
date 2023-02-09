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

package com.alibaba.gaiax.template.utils

import app.visly.stretch.Dimension
import app.visly.stretch.Rect
import app.visly.stretch.Size
import com.alibaba.gaiax.template.GXSize

/**
 * @suppress
 */
object GXTemplateUtils {

    fun updateDimension(it: Rect<Dimension>, targetDimension: Rect<Dimension>) {
        if (it.start !is Dimension.Undefined) {
            targetDimension.start = it.start
        }
        if (it.end !is Dimension.Undefined) {
            targetDimension.end = it.end
        }
        if (it.top !is Dimension.Undefined) {
            targetDimension.top = it.top
        }
        if (it.bottom !is Dimension.Undefined) {
            targetDimension.bottom = it.bottom
        }
    }

    fun updateSize(src: Size<Dimension>, target: Size<Dimension>) {
        if (src.width !is Dimension.Undefined) {
            target.width = src.width
        }
        if (src.height !is Dimension.Undefined) {
            target.height = src.height
        }
    }
}

internal fun Dimension?.copy(): Dimension {
    return when (this) {
        is Dimension.Points -> Dimension.Points(this.points)
        is Dimension.Percent -> Dimension.Percent(this.percentage)
        is Dimension.Undefined -> Dimension.Undefined
        is Dimension.Auto -> Dimension.Auto
        else -> Dimension.Undefined
    }
}

internal fun GXSize?.copy(): GXSize {
    return when (this) {
        is GXSize.PX -> GXSize.PX(this.targetName, this.targetValue)
        is GXSize.PT -> GXSize.PT(this.targetName, this.targetValue)
        is GXSize.PE -> GXSize.PE(this.targetName, this.targetValue)
        is GXSize.Auto -> GXSize.Auto
        is GXSize.Undefined -> GXSize.Undefined
        else -> GXSize.Undefined
    }
}


