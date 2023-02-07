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

    fun createRectGXSizeByPriority(
        heightPriority: Rect<GXSize>?,
        lowPriority: Rect<GXSize>?
    ): Rect<GXSize>? {
        return if (lowPriority != null && heightPriority != null) {
            Rect(
                start = if (heightPriority.start !is GXSize.Undefined && heightPriority.start !is GXSize.Auto)
                    heightPriority.start.copy()
                else
                    lowPriority.start.copy(),
                end = if (heightPriority.end !is GXSize.Undefined && heightPriority.end !is GXSize.Auto)
                    heightPriority.end.copy()
                else
                    lowPriority.end.copy(),
                top = if (heightPriority.top !is GXSize.Undefined && heightPriority.top !is GXSize.Auto)
                    heightPriority.top.copy()
                else
                    lowPriority.top.copy(),
                bottom = if (heightPriority.bottom !is GXSize.Undefined && heightPriority.bottom !is GXSize.Auto)
                    heightPriority.bottom.copy()
                else
                    lowPriority.bottom.copy()
            )
        } else if (heightPriority == null && lowPriority != null) {
            Rect(
                lowPriority.start.copy(),
                lowPriority.end.copy(),
                lowPriority.top.copy(),
                lowPriority.bottom.copy()
            )
        } else if (lowPriority == null && heightPriority != null) {
            Rect(
                heightPriority.start.copy(),
                heightPriority.end.copy(),
                heightPriority.top.copy(),
                heightPriority.bottom.copy()
            )
        } else {
            null
        }
    }

    fun createRectDimensionByPriority(
        heightPriority: Rect<GXSize>?,
        lowPriority: Rect<GXSize>?
    ): Rect<GXSize>? {
        return if (lowPriority != null && heightPriority != null) {
            Rect(
                start = if (heightPriority.start !is GXSize.Undefined && heightPriority.start !is GXSize.Auto)
                    heightPriority.start.copy()
                else
                    lowPriority.start.copy(),
                end = if (heightPriority.end !is GXSize.Undefined && heightPriority.end !is GXSize.Auto)
                    heightPriority.end.copy()
                else
                    lowPriority.end.copy(),
                top = if (heightPriority.top !is GXSize.Undefined && heightPriority.top !is GXSize.Auto)
                    heightPriority.top.copy()
                else
                    lowPriority.top.copy(),
                bottom = if (heightPriority.bottom !is GXSize.Undefined && heightPriority.bottom !is GXSize.Auto)
                    heightPriority.bottom.copy()
                else
                    lowPriority.bottom.copy()
            )
        } else if (heightPriority == null && lowPriority != null) {
            Rect(
                lowPriority.start.copy(),
                lowPriority.end.copy(),
                lowPriority.top.copy(),
                lowPriority.bottom.copy()
            )
        } else if (lowPriority == null && heightPriority != null) {
            Rect(
                heightPriority.start.copy(),
                heightPriority.end.copy(),
                heightPriority.top.copy(),
                heightPriority.bottom.copy()
            )
        } else {
            null
        }
    }


    fun createRectDimensionByPriority2(
        heightPriority: Rect<GXSize?>?,
        lowPriority: Rect<GXSize?>?
    ): Rect<GXSize?>? {
        return if (lowPriority != null && heightPriority != null) {
            Rect(
                start = heightPriority.start?.copy() ?: lowPriority.start.copy(),
                end = heightPriority.end?.copy() ?: lowPriority.end.copy(),
                top = heightPriority.top?.copy() ?: lowPriority.top.copy(),
                bottom = heightPriority.bottom?.copy() ?: lowPriority.bottom.copy()
            )
        } else if (heightPriority == null && lowPriority != null) {
            Rect(
                lowPriority.start?.copy(),
                lowPriority.end?.copy(),
                lowPriority.top?.copy(),
                lowPriority.bottom?.copy()
            )
        } else if (lowPriority == null && heightPriority != null) {
            Rect(
                heightPriority.start?.copy(),
                heightPriority.end?.copy(),
                heightPriority.top?.copy(),
                heightPriority.bottom?.copy()
            )
        } else {
            null
        }
    }


    fun createSizeDimensionByPriority2(
        heightPriority: Size<GXSize?>?,
        lowPriority: Size<GXSize?>?
    ): Size<GXSize?>? {
        return if (lowPriority != null && heightPriority != null) {
            Size(
                width = heightPriority.width?.copy()?:lowPriority.width?.copy(),
                height = heightPriority.height?.copy()?: lowPriority.height?.copy()
            )
        } else if (heightPriority == null && lowPriority != null) {
            Size(
                width = lowPriority.width?.copy(),
                height = lowPriority.height?.copy()
            )
        } else if (lowPriority == null && heightPriority != null) {
            Size(
                width = heightPriority.width?.copy(),
                height = heightPriority.height?.copy()
            )
        } else {
            null
        }
    }

    fun createSizeDimensionByPriority(
        heightPriority: Size<GXSize>?,
        lowPriority: Size<GXSize>?
    ): Size<GXSize>? {
        return if (lowPriority != null && heightPriority != null) {
            Size(
                width = if (heightPriority.width !is GXSize.Undefined && heightPriority.width !is GXSize.Auto)
                    heightPriority.width.copy()
                else
                    lowPriority.width.copy(),
                height = if (heightPriority.height !is GXSize.Undefined && heightPriority.height !is GXSize.Auto)
                    heightPriority.height.copy()
                else
                    lowPriority.height.copy()
            )
        } else if (heightPriority == null && lowPriority != null) {
            Size(
                width = lowPriority.width.copy(),
                height = lowPriority.height.copy()
            )
        } else if (lowPriority == null && heightPriority != null) {
            Size(
                width = heightPriority.width.copy(),
                height = heightPriority.height.copy()
            )
        } else {
            null
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


