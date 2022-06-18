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

import android.content.Context
import android.graphics.Color
import com.alibaba.gaiax.GXRegisterCenter

/**
 * The color wrapper class, all colors should be converted to GColor using the create method
 * @suppress
 */
class GXColor private constructor(val type: Int, private val value: Any) {

    fun valueCanNull(context: Context? = null): Int? {
        if (type == COLOR_TYPE_STATIC) {
            return value as Int
        } else if (type == COLOR_TYPE_DYNAMIC) {
            return GXRegisterCenter.instance.extensionColor?.convert(context, value as String)
                ?.let { return it }
        }
        return null
    }

    fun value(context: Context? = null): Int {
        return valueCanNull(context) ?: UNDEFINE_COLOR
    }

    override fun toString(): String {
        return "GXColor(type=$type, value=$value)"
    }

    companion object {

        const val COLOR_TYPE_STATIC: Int = 0
        const val COLOR_TYPE_DYNAMIC: Int = 1

        private const val UNDEFINE_COLOR = Color.TRANSPARENT

        private val UNDEFINE = GXColor(COLOR_TYPE_STATIC, UNDEFINE_COLOR)

        fun createUndefine(): GXColor {
            return UNDEFINE
        }

        fun createHex(color: String): GXColor {
            parseHexColor(color)?.let { return GXColor(COLOR_TYPE_STATIC, it) }
            throw IllegalArgumentException("Create hex color error")
        }

        fun create(targetColor: String): GXColor? {
            var color = targetColor.trim()
            if (color.contains("%")) {
                val list = color.split(" ")
                if (list.size == 2) {
                    color = list[0]
                }
            }
            parseHexColor(color)?.let { return GXColor(COLOR_TYPE_STATIC, it) }
            parseRGBAColor(color)?.let { return GXColor(COLOR_TYPE_STATIC, it) }
            parseRGBColor(color)?.let { return GXColor(COLOR_TYPE_STATIC, it) }
            parseSimpleColor(color)?.let { return GXColor(COLOR_TYPE_STATIC, it) }
            parseExtendColor(color)?.let {
                return GXColor(COLOR_TYPE_DYNAMIC, it)
            }
            return null
        }

        private fun parseExtendColor(color: String): String? {
            if (color.isNotBlank()) {
                return color
            }
            return null
        }

        private fun parseRGBAColor(color: String): Int? {
            if (color.startsWith("rgba(") && color.endsWith(")")) {
                val colors = color.substring("rgba(".length, color.lastIndexOf(")")).split(",")
                return Color.argb(
                    (colors[3].trim().toFloat() * 255).toInt(),
                    colors[0].trim().toInt(),
                    colors[1].trim().toInt(),
                    colors[2].trim().toInt()
                )
            }
            return null
        }

        private fun parseRGBColor(color: String): Int? {
            if (color.startsWith("rgb(") && color.endsWith(")")) {
                val colors = color.substring("rgb(".length, color.lastIndexOf(")")).split(",")
                return Color.rgb(
                    colors[0].trim().toInt(),
                    colors[1].trim().toInt(),
                    colors[2].trim().toInt()
                )
            }
            return null
        }

        private fun parseHexColor(color: String): Int? {
            if (color.startsWith("#")) {
                return if (color.length == 9) {
                    val alpha = color.substring(7, color.length)
                    val rgb = color.substring(1, color.length - 2)
                    val argb = "#$alpha$rgb"
                    Color.parseColor(argb)
                } else {
                    Color.parseColor(color)
                }
            }
            return null
        }

        private fun parseHexPositionColor(color: String): Int? {
            if (color.startsWith("#") && color.contains("%")) {
                val list = color.split(" ")
                if (list.size == 2) {
                    val hexColor = list[0]
                    val hexPosition = list[1]
                    return parseHexColor(hexColor)
                }
            }
            return null
        }

        fun hexColorRGBAToARGB(rgba: String): String? {
            if (rgba.startsWith("#")) {
                return if (rgba.length == 9) {
                    val alpha = rgba.substring(7, rgba.length)
                    val rgb = rgba.substring(1, rgba.length - 2)
                    "#$alpha$rgb"
                } else {
                    rgba
                }
            }
            return null
        }

        fun hexColorARGBToRGBA(argb: String): String? {
            if (argb.startsWith("#")) {
                return if (argb.length == 9) {
                    val alpha = argb.substring(1, 3)
                    val rgb = argb.substring(3, argb.length)
                    "#$rgb$alpha"
                } else {
                    argb
                }
            }
            return null
        }

        private fun parseSimpleColor(color: String): Int? = when {
            color.equals("BLACK", true) -> Color.BLACK
            color.equals("DKGRAY", true) -> Color.DKGRAY
            color.equals("GRAY", true) -> Color.GRAY
            color.equals("LTGRAY", true) -> Color.LTGRAY
            color.equals("WHITE", true) -> Color.WHITE
            color.equals("RED", true) -> Color.RED
            color.equals("GREEN", true) -> Color.GREEN
            color.equals("BLUE", true) -> Color.BLUE
            color.equals("YELLOW", true) -> Color.YELLOW
            color.equals("CYAN", true) -> Color.CYAN
            color.equals("MAGENTA", true) -> Color.MAGENTA
            color.equals("TRANSPARENT", true) -> Color.TRANSPARENT
            else -> null
        }
    }
}
