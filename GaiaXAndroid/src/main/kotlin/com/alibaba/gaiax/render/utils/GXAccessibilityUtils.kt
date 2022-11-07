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

import android.view.View
import android.widget.AbsoluteLayout
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXTemplateKey

object GXAccessibilityUtils {

    fun accessibilityOfImage(view: View, data: JSONObject?) {
        try {
            // 原有无障碍逻辑
            val accessibilityDesc = data?.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC)
            if (accessibilityDesc != null && accessibilityDesc.isNotEmpty()) {
                view.contentDescription = accessibilityDesc
                view.importantForAccessibility = AppCompatImageView.IMPORTANT_FOR_ACCESSIBILITY_YES
            } else {
                view.importantForAccessibility = AppCompatImageView.IMPORTANT_FOR_ACCESSIBILITY_NO
            }

            // 新增Enable逻辑
            data?.getBoolean(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE)?.let { enable ->
                if (enable) {
                    view.importantForAccessibility =
                        AppCompatImageView.IMPORTANT_FOR_ACCESSIBILITY_YES
                    if (accessibilityDesc == null || accessibilityDesc.isEmpty()) {
                        view.contentDescription = "图片"
                    }
                } else {
                    view.importantForAccessibility =
                        AppCompatImageView.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            }
        } catch (e: Exception) {
            if (GXRegisterCenter.instance.extensionCompatibility?.isPreventAccessibilityThrowException() == false) {
                throw e
            }
        }
    }

    fun accessibilityOfText(view: View, data: JSONObject?, content: CharSequence) {
        try {
            // 原有无障碍逻辑
            val desc = data?.get(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC) as? String
            if (desc != null && desc.isNotBlank()) {
                view.contentDescription = desc
                view.importantForAccessibility = AppCompatTextView.IMPORTANT_FOR_ACCESSIBILITY_YES
            } else {
                view.contentDescription = null
                if (content.isNotEmpty()) {
                    view.importantForAccessibility =
                        AppCompatTextView.IMPORTANT_FOR_ACCESSIBILITY_YES
                } else {
                    view.importantForAccessibility =
                        AppCompatTextView.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            }

            // 新增Enable逻辑
            data?.getBoolean(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE)?.let { enable ->
                if (enable) {
                    view.importantForAccessibility =
                        AppCompatTextView.IMPORTANT_FOR_ACCESSIBILITY_YES
                } else {
                    view.importantForAccessibility =
                        AppCompatTextView.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            }
        } catch (e: Exception) {
            if (GXRegisterCenter.instance.extensionCompatibility?.isPreventAccessibilityThrowException() == false) {
                throw e
            }
        }
    }

    fun accessibilityOfView(view: View, data: JSONObject?) {
        try {
            // 原有无障碍逻辑
            val accessibilityDesc = data?.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC)
            if (accessibilityDesc != null && accessibilityDesc.isNotEmpty()) {
                view.contentDescription = accessibilityDesc
                view.importantForAccessibility = AbsoluteLayout.IMPORTANT_FOR_ACCESSIBILITY_YES
            } else {
                view.importantForAccessibility = AbsoluteLayout.IMPORTANT_FOR_ACCESSIBILITY_NO
            }

            // 新增无障碍Enable逻辑
            data?.getBoolean(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE)?.let { enable ->
                view.importantForAccessibility = if (enable) {
                    AbsoluteLayout.IMPORTANT_FOR_ACCESSIBILITY_YES
                } else {
                    AbsoluteLayout.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            }
        } catch (e: Exception) {
            if (GXRegisterCenter.instance.extensionCompatibility?.isPreventAccessibilityThrowException() == false) {
                throw e
            }
        }
    }

    /**
     *  FIX: to fix accessibility question when view as a button view was used.
     */
    fun accessibilityOfViewAsButton(gxNode: GXNode) {
        if (gxNode.isViewType() && !gxNode.isRoot) {
            gxNode.view?.let {
                ViewCompat.setAccessibilityDelegate(it, object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(
                        host: View?, info: AccessibilityNodeInfoCompat?
                    ) {
                        super.onInitializeAccessibilityNodeInfo(host, info)
                        info?.className = Button::class.java.name
                    }
                })
            }
        } else {
            gxNode.view?.let {
                ViewCompat.setAccessibilityDelegate(it, object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(
                        host: View?, info: AccessibilityNodeInfoCompat?
                    ) {
                        super.onInitializeAccessibilityNodeInfo(host, info)
                        info?.className = View::class.java.name
                    }
                })
            }
        }
    }

}