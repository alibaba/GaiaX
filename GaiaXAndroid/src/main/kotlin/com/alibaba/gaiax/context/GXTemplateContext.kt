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
import android.os.SystemClock
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

    var isAppear: Boolean = false

    /**
     * 用于存储JS组件ID
     */
    var jsComponentIds: MutableList<Long>? = null

    /**
     * 用于追踪日志
     */
    var traceId: String? = SystemClock.elapsedRealtimeNanos().toString()

    var tag: String? = ""

    /**
     * 视图的尺寸是否发生了变化，如果发生了变化，那么缓存需要被清空，UI需要被重建。
     *
     * 生命周期：
     * bindDataOnlyNodeTree(gxView, gxTemplateData, gxMeasureSize)
     * bindDataOnlyViewTree(gxView, gxTemplateData, gxMeasureSize)
     */
    var isMeasureSizeChanged: Boolean = false

    var isReuseRootNode: Boolean = false

    /**
     * 数据绑定计数
     */
    var bindDataCount: Int = 0

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
        flags = 0
        sliderItemLayoutCache = null
        gridItemLayoutCache = null
        scrollItemLayoutCache?.clear()
        containers?.clear()
        isDirty = false
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
        manualTrackMap?.clear()
    }

    fun initContainers() {
        if (containers == null) {
            containers = CopyOnWriteArraySet()
        }
    }

    /**
     * 重置所有缓存计算的内容
     */
    fun reset() {
        templateInfo.reset()
        rootNode?.resetTree(this)
    }

    private var flags = 0

    fun flagExtendFlexbox() {
        flags = flags.or(FLAG_EXTEND_FLEXBOX)
    }

    fun isFlagExtendFlexbox(): Boolean {
        return (flags and FLAG_EXTEND_FLEXBOX) != 0
    }

    fun flagFlexGrow() {
        flags = flags.or(FLAG_FLEX_GROW_UPDATE)
    }

    fun isFlagFlexGrow(): Boolean {
        return (flags and FLAG_FLEX_GROW_UPDATE) != 0
    }

    companion object {

        const val FLAG_EXTEND_FLEXBOX = 0x1
        const val FLAG_FLEX_GROW_UPDATE = 0x2

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
