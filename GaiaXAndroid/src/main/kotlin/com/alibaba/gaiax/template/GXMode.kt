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

import android.graphics.Matrix
import android.widget.ImageView

/**
 * @suppress
 */
class GXMode(val modeType: String, val mode: String) {

    fun getScaleType(): ImageView.ScaleType {
        return when (mode) {
            // 填充模式，不保持纵横比缩放图片，使图片的宽高完全拉伸至填满 image 元素
            "scaleToFill" -> ImageView.ScaleType.FIT_XY
            // 填充模式，保持纵横比缩放图片，使图片的长边能完全显示出来。也就是说，可以完整地将图片显示出来。
            "aspectFit" -> ImageView.ScaleType.FIT_CENTER
            // 填充模式，保持纵横比缩放图片，只保证图片的短边能完全显示出来。也就是说，图片通常只在水平或垂直方向是完整的，另一个方向将会发生截取。
            "aspectFill" -> ImageView.ScaleType.CENTER_CROP

            // 缩放 或者 裁剪
            "left" -> ImageView.ScaleType.MATRIX
            "right" -> ImageView.ScaleType.MATRIX
            "center" -> ImageView.ScaleType.CENTER
            "top" -> ImageView.ScaleType.MATRIX
            "bottom" -> ImageView.ScaleType.MATRIX

            else -> ImageView.ScaleType.FIT_XY
        }
    }

    fun getMatrix(viewWidth: Int, viewHeight: Int, drawableWidth: Int, drawableHeight: Int): Matrix? {
        return when (modeType) {
            MODE_TYPE_SCALE -> {
                getScaleMatrix(drawableWidth, drawableHeight, viewWidth, viewHeight)
            }
            MODE_TYPE_CROP -> {
                getCropMatrix(drawableWidth, drawableHeight, viewWidth, viewHeight)
            }
            else -> {
                null
            }
        }
    }

    private fun getCropMatrix(drawableWidth: Int, drawableHeight: Int, viewWidth: Int, viewHeight: Int): Matrix? {
        return when (mode) {
            // 需要区分是否缩放
            "left" -> {
                getCropLeftMatrix(drawableWidth, drawableHeight, viewWidth, viewHeight)
            }
            "right" -> {
                getCropMatrixRight(drawableWidth, drawableHeight, viewWidth, viewHeight)
            }
            "top" -> {
                getCropMatrixTop(drawableWidth, drawableHeight, viewWidth, viewHeight)
            }
            "bottom" -> {
                getCropMatrixBottom(drawableWidth, drawableHeight, viewWidth, viewHeight)
            }
            else -> null
        }
    }

    private fun getCropMatrixBottom(drawableWidth: Int, drawableHeight: Int, viewWidth: Int, viewHeight: Int): Matrix? {
        val matrix = Matrix()
        val scale: Float
        var dx = 0f
        var dy = 0f
        if (drawableWidth >= drawableHeight) {
            scale = viewHeight.toFloat() / drawableHeight.toFloat()
            dx = (viewWidth - drawableWidth * scale) * 0.5F
            dy = viewHeight - drawableHeight * scale
        } else {
            scale = viewWidth.toFloat() / drawableWidth.toFloat()
            dx = 0F
            dy = viewHeight - drawableHeight * scale
        }
        matrix.setScale(scale, scale)
        matrix.postTranslate(dx, dy)
        return matrix
    }

    private fun getCropMatrixTop(drawableWidth: Int, drawableHeight: Int, viewWidth: Int, viewHeight: Int): Matrix? {
        val matrix = Matrix()
        val scale: Float
        var dx = 0f
        var dy = 0f
        if (drawableWidth >= drawableHeight) {
            scale = viewHeight.toFloat() / drawableHeight.toFloat()
            dx = (viewWidth - drawableWidth * scale) * 0.5F
            dy = 0F
        } else {
            scale = viewWidth.toFloat() / drawableWidth.toFloat()
            dx = 0F
            dy = 0F
        }
        matrix.setScale(scale, scale)
        matrix.postTranslate(dx, dy)
        return matrix
    }

    private fun getCropMatrixRight(drawableWidth: Int, drawableHeight: Int, viewWidth: Int, viewHeight: Int): Matrix? {
        val matrix = Matrix()
        val scale: Float
        var dx = 0f
        var dy = 0f
        if (drawableWidth >= drawableHeight) {
            scale = viewHeight.toFloat() / drawableHeight.toFloat()
            dx = viewWidth - drawableWidth * scale
            dy = 0F
        } else {
            scale = viewWidth.toFloat() / drawableWidth.toFloat()
            dx = 0F
            dy = (viewHeight - drawableHeight * scale) * 0.5f
        }
        matrix.setScale(scale, scale)
        matrix.postTranslate(dx, dy)
        return matrix
    }

    private fun getCropLeftMatrix(drawableWidth: Int, drawableHeight: Int, viewWidth: Int, viewHeight: Int): Matrix? {
        val matrix = Matrix()
        val scale: Float
        var dx = 0f
        var dy = 0f
        if (drawableWidth >= drawableHeight) {
            scale = viewHeight.toFloat() / drawableHeight.toFloat()
            dx = 0F
            dy = 0F
        } else {
            scale = viewWidth.toFloat() / drawableWidth.toFloat()
            dx = 0F
            dy = (viewHeight - drawableHeight * scale) * 0.5f
        }
        matrix.setScale(scale, scale)
        matrix.postTranslate(dx, dy)
        return matrix
    }

    private fun getScaleMatrix(drawableWidth: Int, drawableHeight: Int, viewWidth: Int, viewHeight: Int): Matrix? {
        return when (mode) {
            // 需要区分是否缩放
            "left" -> {
                getScaleMatrixLeft(drawableWidth, drawableHeight, viewWidth, viewHeight)
            }
            "right" -> {
                getScaleMatrixRight(drawableWidth, drawableHeight, viewWidth, viewHeight)
            }
            "top" -> {
                getScaleMatrixTop(drawableWidth, drawableHeight, viewWidth, viewHeight)
            }
            "bottom" -> {
                getScaleMatrixBottom(drawableWidth, drawableHeight, viewWidth, viewHeight)
            }
            else -> null
        }
    }

    private fun getScaleMatrixBottom(drawableWidth: Int, drawableHeight: Int, viewWidth: Int, viewHeight: Int): Matrix {
        val matrix = Matrix()
        val scale: Float
        var dx = 0f
        var dy = 0f

        // 图片的宽高比大于视图的宽高比
        // 说明以宽度为基准，同比缩放之后，图片高度小于视图的高度
        if (drawableWidth.toFloat() * viewHeight.toFloat() >= drawableHeight.toFloat() * viewWidth.toFloat()) {
            scale = Math.min(viewWidth.toFloat() / drawableWidth.toFloat(), viewHeight.toFloat() / drawableHeight.toFloat())
            dx = 0F
            dy = (viewHeight - drawableHeight * scale)
        }
        // 图片的宽高比小于视图的宽高比
        // 说明以宽度为基准，同比缩放之后，图片高度大于视图的高度
        else {
            scale = viewHeight.toFloat() / drawableHeight.toFloat()
            dx = (viewWidth - drawableWidth * scale) * 0.5F
            dy = (viewHeight - drawableHeight * scale)
        }
        matrix.setScale(scale, scale)
        matrix.postTranslate(dx, dy)
        return matrix
    }

    private fun getScaleMatrixTop(drawableWidth: Int, drawableHeight: Int, viewWidth: Int, viewHeight: Int): Matrix {
        val matrix = Matrix()
        val scale: Float
        var dx = 0f
        var dy = 0f

        // 图片的宽高比大于视图的宽高比
        // 说明以宽度为基准，同比缩放之后，图片高度小于视图的高度
        if (drawableWidth.toFloat() * viewHeight.toFloat() >= drawableHeight.toFloat() * viewWidth.toFloat()) {
            scale = Math.min(viewWidth.toFloat() / drawableWidth.toFloat(), viewHeight.toFloat() / drawableHeight.toFloat())
            dx = 0F
            dy = 0F
        }
        // 图片的宽高比小于视图的宽高比
        // 说明以宽度为基准，同比缩放之后，图片高度大于视图的高度
        else {
            scale = viewHeight.toFloat() / drawableHeight.toFloat()
            dx = (viewWidth - drawableWidth * scale) * 0.5F
            dy = 0F
        }
        matrix.setScale(scale, scale)
        matrix.postTranslate(dx, dy)
        return matrix
    }

    private fun getScaleMatrixRight(drawableWidth: Int, drawableHeight: Int, viewWidth: Int, viewHeight: Int): Matrix {
        val matrix = Matrix()
        val scale: Float
        var dx = 0f
        var dy = 0f

        // 图片的宽高比大于视图的宽高比
        // 说明以宽度为基准，同比缩放之后，图片高度小于视图的高度
        // 说明以高度为基准，同比缩放之后，图片宽度大于视图的宽度
        if (drawableWidth.toFloat() * viewHeight.toFloat() >= drawableHeight.toFloat() * viewWidth.toFloat()) {
            scale = Math.min(viewWidth.toFloat() / drawableWidth.toFloat(), viewHeight.toFloat() / drawableHeight.toFloat())
            dx = (viewWidth - drawableWidth * scale)
            dy = (viewHeight - drawableHeight * scale) * 0.5f
        }
        // 图片的宽高比小于视图的宽高比
        // 说明以宽度为基准，同比缩放之后，图片高度大于视图的高度
        else {
            scale = viewHeight.toFloat() / drawableHeight.toFloat()
            dx = (viewWidth - drawableWidth * scale)
            dy = 0F
        }
        matrix.setScale(scale, scale)
        matrix.postTranslate(dx, dy)
        return matrix
    }

    private fun getScaleMatrixLeft(drawableWidth: Int, drawableHeight: Int, viewWidth: Int, viewHeight: Int): Matrix {
        val matrix = Matrix()
        val scale: Float
        var dx = 0f
        var dy = 0f

        // 图片的宽高比大于视图的宽高比
        // 说明以宽度为基准，同比缩放之后，图片高度小于视图的高度
        // 说明以高度为基准，同比缩放之后，图片宽度大于视图的宽度
        if (drawableWidth.toFloat() * viewHeight.toFloat() >= drawableHeight.toFloat() * viewWidth.toFloat()) {
            scale = Math.min(viewWidth.toFloat() / drawableWidth.toFloat(), viewHeight.toFloat() / drawableHeight.toFloat())
            dx = 0F
            dy = (viewHeight - drawableHeight * scale) * 0.5f
        }
        // 图片的宽高比小于视图的宽高比
        // 说明以宽度为基准，同比缩放之后，图片高度大于视图的高度
        else {
            scale = viewHeight.toFloat() / drawableHeight.toFloat()
            dx = 0F
            dy = 0F
        }
        matrix.setScale(scale, scale)
        matrix.postTranslate(dx, dy)
        return matrix
    }

    companion object {

        const val MODE_TYPE_SCALE = "scale"
        const val MODE_TYPE_CROP = "crop"
    }
}