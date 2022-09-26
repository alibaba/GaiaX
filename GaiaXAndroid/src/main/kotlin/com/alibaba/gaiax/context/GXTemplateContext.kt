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

package com.alibaba.gaiax.context

import android.content.Context
import android.view.View
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.render.node.GXTemplateNode
import com.alibaba.gaiax.render.node.text.GXDirtyText
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.basic.GXImageView
import com.alibaba.gaiax.render.view.basic.GXView
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.template.GXTemplateInfo
import java.util.concurrent.CopyOnWriteArraySet

/**
 * @suppress
 */
class GXTemplateContext private constructor(
    /**
     * context
     */
    val context: Context,
    /**
     * ViewPort size
     */
    var size: GXTemplateEngine.GXMeasureSize,

    /**
     * Template information
     */
    val templateItem: GXTemplateEngine.GXTemplateItem,

    /**
     * Raw data of the template associated with the current view: root template, nested template, and child template
     */
    val templateInfo: GXTemplateInfo,
    /**
     * A virtual node for nested templates
     */
    var visualTemplateNode: GXTemplateNode? = null
) {


    val blurViews: HashSet<GXView> by lazy {
        HashSet()
    }

    val onImageDrawableListener = object : GXImageView.GXImageViewListener {

        override fun onDrawableChanged() {
            blurViews.forEach { it.onBlurChanged(this@GXTemplateContext) }
        }
    }

    var isAppear: Boolean = false

    var manualTrackMap: MutableMap<String, GXTemplateEngine.GXTrack>? = null

    var dirtyTexts: MutableSet<GXDirtyText>? = null

    /**
     * Is dirty
     */
    var isDirty: Boolean = false

    /**
     * Is exist flexGrow logic
     */
    var isFlexGrowLayout: Boolean = false

    /**
     * A soft or weak reference to a view
     */
    var rootView: View? = null

    /**
     * View Information about the virtual node tree associated with the template
     */
    var rootNode: GXNode? = null

    /**
     * Template Data
     */
    var templateData: GXTemplateEngine.GXTemplateData? = null

    /**
     * Container-indexed position
     */
    var indexPosition: Int = -1

    val containers: CopyOnWriteArraySet<GXContainer> by lazy {
        CopyOnWriteArraySet<GXContainer>()
    }

    override fun toString(): String {
        return "GXTemplateContext(context=$context, isDirty=$isDirty, size=$size, templateItem='$templateItem' rootView=$rootView)"
    }

    fun release() {
        blurViews.clear()
        containers.clear()
        isDirty = false
        dirtyTexts?.clear()
        dirtyTexts = null
        templateData = null
        rootView = null
        visualTemplateNode = null
        rootNode?.release()
        rootNode = null
    }

    fun manualExposure() {
        manualTrackMap?.forEach {
            templateData?.trackListener?.onManualExposureTrackEvent(it.value)
        }
    }

    companion object {

        fun createContext(
            gxTemplateItem: GXTemplateEngine.GXTemplateItem,
            gxMeasureSize: GXTemplateEngine.GXMeasureSize,
            gxTemplateInfo: GXTemplateInfo,
            gxVisualTemplateNode: GXTemplateNode? = null
        ): GXTemplateContext {
            return GXTemplateContext(
                gxTemplateItem.context,
                gxMeasureSize,
                gxTemplateItem,
                gxTemplateInfo,
                gxVisualTemplateNode
            )
        }

        fun getContext(targetView: View?): GXTemplateContext? {
            if (targetView is GXIRootView) {
                return targetView.getTemplateContext()
            }
            return null
        }

        fun setContext(targetView: View?) {
            if (targetView is GXIRootView) {
                targetView.setTemplateContext(null)
            }
        }
    }
}