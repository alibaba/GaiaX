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

package com.alibaba.gaiax.render.node.text

import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.node.GXTemplateNode
import com.alibaba.gaiax.template.GXColor
import com.alibaba.gaiax.template.GXSize
import com.alibaba.gaiax.template.GXStyleConvert
import com.alibaba.gaiax.template.GXTemplateKey
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

/**
 * @suppress
 */
object GXHighLightUtil {
    private fun convertTag(tag: String): String = when (tag) {
        "$" -> "\\$"
        "?" -> "\\?"
        "^" -> "\\^"
        else -> tag
    }

    private var regexCache: ConcurrentHashMap<String, Pattern>? = null

    fun getHighLightContent(
        view: View,
        gxTemplateNode: GXTemplateNode,
        templateData: JSONObject,
        data: String
    ): CharSequence? {
        val extend = gxTemplateNode.getExtend(templateData) ?: return null
        val highlightTag = extend.getString(GXTemplateKey.GAIAX_HIGHLIGHT_TAG)
        val highlightColor = extend.getString(GXTemplateKey.GAIAX_HIGHLIGHT_COLOR)
        val highlightFontSize = extend.getString(GXTemplateKey.GAIAX_HIGHLIGHT_FONT_SIZE)
        val highlightFontWeight = extend.getString(GXTemplateKey.GAIAX_HIGHLIGHT_FONT_WEIGHT)
        val highlightFontFamily = extend.getString(GXTemplateKey.GAIAX_HIGHLIGHT_FONT_FAMILY)
        if (highlightTag != null && highlightTag.isNotBlank()) {
            val convertTag = convertTag(highlightTag)

            // generate cache pool
            if (regexCache == null) {
                if (regexCache == null) {
                    regexCache = ConcurrentHashMap()
                }
            }

            // get pattern from cache
            val pattern = if (regexCache != null && regexCache!!.containsKey(convertTag)) {
                regexCache!![convertTag]!!
            } else {
                regexCache!![convertTag] =
                    Pattern.compile("$convertTag${GXTemplateKey.GAIAX_HIGHLIGHT_REGEX}${convertTag}")
                regexCache!![convertTag]!!
            }

            val matcher = pattern.matcher(data)
            val spannableString = SpannableString(data.replace(Regex(convertTag), ""))
            var count = 0
            while (matcher.find()) {
                count++
                // 主演: $克里斯汀$·贝尔/$伊迪娜$·$门泽尔$/$乔什$·盖德/
                // 主演: 克里斯汀·贝尔/伊迪娜·门泽尔/乔什·盖德/
                val startIndex = matcher.start() - (2 * (count - 1))
                val endIndex = matcher.end() - 2 * count
                if (startIndex < endIndex) {

                    // https://blog.csdn.net/chimpan/article/details/80240942
                    // https://yuque.antfin-inc.com/gaia/document/ais32k

                    if (highlightColor != null && highlightColor.isNotBlank()) {
                        GXColor.create(highlightColor)?.value(view.context)?.let {
                            spannableString.setSpan(
                                ForegroundColorSpan(it),
                                startIndex,
                                endIndex,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                    if (highlightFontSize != null && highlightFontSize.isNotBlank()) {
                        GXSize.create(highlightFontSize).let {
                            spannableString.setSpan(
                                AbsoluteSizeSpan(it.valueInt),
                                startIndex,
                                endIndex,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                    if (highlightFontFamily != null && highlightFontFamily.isNotBlank()) {
                        GXStyleConvert.instance.fontFamily(highlightFontFamily)?.let {
                            spannableString.setSpan(
                                TypefaceSpan(highlightFontFamily),
                                startIndex,
                                endIndex,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    } else if (highlightFontWeight != null && highlightFontWeight.isNotBlank()) {
                        GXStyleConvert.instance.fontWeight(highlightFontWeight)?.let {
                            spannableString.setSpan(
                                StyleSpan(it.style),
                                startIndex,
                                endIndex,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                }
            }
            return spannableString
        }
        return null
    }

}