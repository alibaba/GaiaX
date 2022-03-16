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

package com.alibaba.gaiax.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.alibaba.gaiax.GXTemplateEngine

/**
 * @suppress
 */
object GXScreenUtils {

    private fun Float.convertPixelsToDp(): Float {
        return this / (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    private var screenWidth = 0F
    private var screenHeight = 0F

    private val dm = DisplayMetrics()

    fun getScreenWidthPx(context: Context): Float {
        initScreen(context)
        return screenWidth
    }

    fun getScreenHeightPx(context: Context): Float {
        initScreen(context)
        return screenHeight
    }

    fun getScreenHeightDP(context: Context): Float {
        initScreen(context)
        return screenHeight.convertPixelsToDp()
    }

    fun getScreenWidthDP(context: Context): Float {
        initScreen(context)
        return screenWidth.convertPixelsToDp()
    }

    /**
     * 如果是未初始化过，或者是响应式状态下，需要重新初始化屏幕宽高值
     *
     * 1. 先从DecorView中获取
     * 2. 从多屏幕中获取
     * 3. 从ActivityWindow中获取
     * 4. 从Application中获取
     */
    private fun initScreen(context: Context) {
        if (context is Activity) {
            // DecorView 获取
            screenWidth = context.window.decorView.measuredWidth.toFloat()
            screenHeight = context.window.decorView.measuredHeight.toFloat()
            if (screenWidth != 0F && screenHeight != 0F) {
                return
            }

            // Multi Window 获取
            if (Build.VERSION.SDK_INT >= 24 && context.isInMultiWindowMode) {
                screenWidth = Math.round((context.resources.configuration.screenWidthDp.toFloat() * context.resources.displayMetrics.density).toDouble() + 0.5).toFloat()
                screenHeight = Math.round((context.resources.configuration.screenHeightDp.toFloat() * context.resources.displayMetrics.density).toDouble() + 0.5).toFloat()
                if (screenWidth != 0F && screenHeight != 0F) {
                    return
                }
            }

            // Activity Window 获取
            context.windowManager.defaultDisplay.getMetrics(dm)
            screenWidth = dm.widthPixels.toFloat()
            screenHeight = dm.heightPixels.toFloat()
        } else {
            // Application Window 获取
            val applicationContext = GXTemplateEngine.instance.context
            val windowManager = applicationContext.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            if (windowManager != null) {
                windowManager.defaultDisplay?.getMetrics(dm)
                screenWidth = dm.widthPixels.toFloat()
                screenHeight = dm.heightPixels.toFloat()
            }
        }
    }
}