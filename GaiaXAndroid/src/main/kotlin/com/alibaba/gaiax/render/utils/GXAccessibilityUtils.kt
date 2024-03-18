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

import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.template.GXTemplateKey

object GXAccessibilityUtils {

    fun accessibilityOfImage(view: View, data: JSONObject?) {
        try {
            val accessibilityDesc = data?.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC)
            if (accessibilityDesc != null) {
                view.contentDescription = accessibilityDesc
                view.importantForAccessibility = AppCompatImageView.IMPORTANT_FOR_ACCESSIBILITY_YES
            } else {
                view.importantForAccessibility = AppCompatImageView.IMPORTANT_FOR_ACCESSIBILITY_NO
            }

            data?.getBoolean(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE)?.let { enable ->
                view.importantForAccessibility = if (enable) {
                    AppCompatImageView.IMPORTANT_FOR_ACCESSIBILITY_YES
                } else {
                    AppCompatImageView.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            }

            data?.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_TRAITS)?.let { traits ->
                ViewCompat.setAccessibilityDelegate(view, object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(
                        host: View, info: AccessibilityNodeInfoCompat
                    ) {
                        super.onInitializeAccessibilityNodeInfo(host, info)
                        info.className = getClassNameByTraits(traits)
                    }
                })
            }
        } catch (e: Exception) {
            if (GXRegisterCenter.instance.extensionCompatibilityConfig?.isPreventAccessibilityThrowException == false) {
                throw e
            }
        }
    }

    fun getClassNameByTraits(traits: String): String {
        return when (traits) {
            "button" -> Button::class.java.name
            "image" -> ImageView::class.java.name
            "text" -> TextView::class.java.name
            "none" -> ""
            else -> View::class.java.name
        }
    }

    fun accessibilityOfText(view: View, data: JSONObject?, content: CharSequence) {
        try {
            val desc = data?.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC)
            if (desc != null) {
                view.contentDescription = desc
                view.importantForAccessibility = AppCompatTextView.IMPORTANT_FOR_ACCESSIBILITY_YES
            } else {
                view.contentDescription = null
                if (content.isNotEmpty()) {
                    view.importantForAccessibility = AppCompatTextView.IMPORTANT_FOR_ACCESSIBILITY_YES
                } else {
                    view.importantForAccessibility = AppCompatTextView.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            }

            data?.getBoolean(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE)?.let { enable ->
                view.importantForAccessibility = if (enable) {
                    AppCompatTextView.IMPORTANT_FOR_ACCESSIBILITY_YES
                } else {
                    AppCompatTextView.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            }

            data?.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_TRAITS)?.let { traits ->
                ViewCompat.setAccessibilityDelegate(view, object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(
                        host: View, info: AccessibilityNodeInfoCompat
                    ) {
                        super.onInitializeAccessibilityNodeInfo(host, info)
                        info.className = getClassNameByTraits(traits)
                    }
                })
            }
        } catch (e: Exception) {
            if (GXRegisterCenter.instance.extensionCompatibilityConfig?.isPreventAccessibilityThrowException == false) {
                throw e
            }
        }
    }

    fun accessibilityOfView(view: View, data: JSONObject?) {
        try {
            val accessibilityDesc = data?.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC)
            if (accessibilityDesc != null) {
                view.contentDescription = accessibilityDesc
                view.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            } else {
                view.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }

            data?.getBoolean(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE)?.let { enable ->
                view.importantForAccessibility = if (enable) {
                    View.IMPORTANT_FOR_ACCESSIBILITY_YES
                } else {
                    View.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            }

            data?.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_TRAITS)?.let { traits ->

                // 某些应用使用标题总结屏幕上显示的多组文字。如果特定的 View 元素表示一个标题，您可以通过将该元素的 android:accessibilityHeading 属性设为 true，表明它的无障碍服务用途。
                // 无障碍服务的用户可以选择浏览标题，而不是浏览段落或字词。这种灵活性可改善文字浏览体验。
                if ("header" == traits) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        view.isAccessibilityHeading = true
                    }
                } else {
                    ViewCompat.setAccessibilityDelegate(view, object : AccessibilityDelegateCompat() {
                        override fun onInitializeAccessibilityNodeInfo(
                            host: View, info: AccessibilityNodeInfoCompat
                        ) {
                            super.onInitializeAccessibilityNodeInfo(host, info)
                            info.className = getClassNameByTraits(traits)
                        }
                    })
                }
            }
        } catch (e: Exception) {
            if (GXRegisterCenter.instance.extensionCompatibilityConfig?.isPreventAccessibilityThrowException == false) {
                throw e
            }
        }
    }
}