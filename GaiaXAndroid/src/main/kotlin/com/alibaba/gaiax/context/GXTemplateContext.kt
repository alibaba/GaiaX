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
import app.visly.stretch.Layout
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.render.node.GXTemplateNode
import com.alibaba.gaiax.render.node.text.GXDirtyText
import com.alibaba.gaiax.render.view.GXIContainer
import com.alibaba.gaiax.render.view.GXIRootView
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


    var isReuseRootNode: Boolean = false

    /**
     * 数据绑定计数
     */
    var bindDataCount: Int = 0

    var isAppear: Boolean = false

    var manualTrackMap: MutableMap<String, GXTemplateEngine.GXTrack>? = null

    var dirtyTexts: MutableSet<GXDirtyText>? = null

    /**
     * item layout cache for item data.
     *
     * the cache will used to surely calculate once item layout at scroll container.
     *
     * if the cache be created at computeScrollSize, it will be used at createViewHolder and bindViewHolder.
     * if the cache be created at createViewHolder, it will used at bindViewHolder.
     *
     * key is itemCacheKey ${itemPosition}-${itemData.hashCode()}.
     * value is item layout.
     */
    var scrollItemLayoutCache: MutableMap<Any, Layout>? = null

    var sliderItemLayoutCache: Layout? = null

    var gridItemLayoutCache: Layout? = null

    var scrollNodeCache: MutableMap<Any, GXNode>? = null

    /**
     * Is dirty
     */
    var isDirty: Boolean = false

    var dirtyFlag: Int = DIRTY_FLAG_DEFAULT

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

    var containers: CopyOnWriteArraySet<GXIContainer>? = null

    fun release() {
        scrollItemLayoutCache?.clear()
        containers?.clear()
        dirtyFlag = DIRTY_FLAG_DEFAULT
        dirtyTexts?.clear()
        dirtyTexts = null
        templateData = null
        rootView = null
        visualTemplateNode = null
        rootNode?.release()
        rootNode = null
        isReuseRootNode = false
    }

    fun manualExposure() {
        manualTrackMap?.forEach {
            templateData?.trackListener?.onManualExposureTrackEvent(it.value)
        }
    }

    fun initContainers() {
        if (containers == null) {
            containers = CopyOnWriteArraySet()
        }
    }

    /**
     * 重置所有缓存计算的内容
     */
    fun resetFromResize() {
        templateInfo.reset()
        rootNode?.resetFromResize(this)
    }

    fun dirtyForCompute() {
        dirtyFlag = dirtyFlag.or(DIRTY_FLAG_COMPUTE)
    }

    fun dirtyForStyle() {
        dirtyFlag = dirtyFlag.or(DIRTY_FLAG_STYLE)
    }

    fun dirtyForText() {
        dirtyFlag = dirtyFlag.or(DIRTY_FLAG_TEXT)
    }

    fun isDirtyForText(): Boolean {
        return dirtyFlag.and(DIRTY_FLAG_TEXT) == DIRTY_FLAG_TEXT
    }

    fun isDirtyForCompute(): Boolean {
        return dirtyFlag.and(DIRTY_FLAG_COMPUTE) == DIRTY_FLAG_COMPUTE
    }

    fun isDirtyForStyle(): Boolean {
        return dirtyFlag.and(DIRTY_FLAG_STYLE) == DIRTY_FLAG_STYLE
    }

    companion object {

        const val DIRTY_FLAG_DEFAULT = 0x00000000
        const val DIRTY_FLAG_COMPUTE = 0x00000001
        const val DIRTY_FLAG_STYLE = 0x00000010
        const val DIRTY_FLAG_TEXT = 0x00000100

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
