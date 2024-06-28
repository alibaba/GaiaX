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


package com.alibaba.gaiax.render.utils

import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller

class GXGravitySmoothScroller(context: Context, private val align: Int) : LinearSmoothScroller(context) {

    companion object {
        const val ALIGN_LEFT = 0
        const val ALIGN_CENTER = 1
        const val ALIGN_RIGHT = 2
        const val ALIGN_ANY = 3
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        return super.computeScrollVectorForPosition(targetPosition)
    }

    override fun getHorizontalSnapPreference(): Int {
        return when (align) {
            ALIGN_LEFT -> SNAP_TO_START
            ALIGN_CENTER -> SNAP_TO_ANY
            ALIGN_RIGHT -> SNAP_TO_END
            ALIGN_ANY -> SNAP_TO_ANY
            else -> SNAP_TO_ANY
        }
    }

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
        return super.calculateSpeedPerPixel(displayMetrics) * 2F
    }

    override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
        if (align == ALIGN_CENTER) {
            // Calculate the distance to center the item
            val midpoint = (boxStart + (boxEnd - boxStart) / 2.0).toInt()
            val targetMidpoint = (viewStart + (viewEnd - viewStart) / 2.0).toInt()
            return midpoint - targetMidpoint
        }
        return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference)
    }
}