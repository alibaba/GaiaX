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
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.render.node.GXTemplateNode
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.template.GXTemplateInfo
import java.lang.ref.SoftReference

/**
 * @suppress
 */
class GXTemplateContext(val context: Context) {

    var lifeStatus: Int = LIFE_ON_NONE

    var visibleStatus: Int = LIFE_ON_NONE

    /**
     * Data listener
     */
    var dataListener: GXTemplateEngine.GXIDataListener? = null

    /**
     * Track listener
     */
    var trackListener: GXTemplateEngine.GXITrackListener? = null

    /**
     * Event listener
     */
    var eventListener: GXTemplateEngine.GXIEventListener? = null

    /**
     * Is dirty
     */
    var isDirty: Boolean = false

    /**
     * ViewPort size
     */
    lateinit var size: GXTemplateEngine.GXMeasureSize

    /**
     * Template information
     */
    lateinit var templateItem: GXTemplateEngine.GXTemplateItem

    /**
     * Raw data of the template associated with the current view: root template, nested template, and child template
     */
    lateinit var templateInfo: GXTemplateInfo

    /**
     * A soft or weak reference to a view
     */
    var rootView: SoftReference<View>? = null

    /**
     * View Information about the virtual node tree associated with the template
     */
    var rootNode: GXNode? = null

    /**
     * Hold render data
     */
    var data: JSONObject? = null

    /**
     * Container-indexed position
     */
    var indexPosition: Int = -1

    /**
     * A virtual node for nested templates
     */
    var visualTemplateNode: GXTemplateNode? = null

    fun updateContext(templateData: GXTemplateEngine.GXTemplateData) {
        this.data = templateData.data
        this.dataListener = templateData.dataListener
        this.eventListener = templateData.eventListener
        this.trackListener = templateData.trackListener
    }

    override fun toString(): String {
        return "GXTemplateContext(context=$context, isDirty=$isDirty, size=$size, templateItem='$templateItem' rootView=$rootView)"
    }

    fun release() {
        dataListener = null
        trackListener = null
        eventListener = null
        rootView?.clear()
        rootView = null
        data = null
        visualTemplateNode = null
        rootNode?.release()
        rootNode = null
    }

    companion object {

        const val LIFE_ON_NONE = 0
        const val LIFE_ON_CREATE = 1
        const val LIFE_ON_READY = 2
        const val LIFE_ON_REUSE = 3
        const val LIFE_ON_VISIBLE = 4
        const val LIFE_ON_INVISIBLE = 5
        const val LIFE_ON_DESTROY = 6

        fun createContext(
            templateItem: GXTemplateEngine.GXTemplateItem,
            size: GXTemplateEngine.GXMeasureSize,
            templateInfo: GXTemplateInfo,
            visualTemplateNode: GXTemplateNode? = null
        ): GXTemplateContext {
            val context = GXTemplateContext(templateItem.context)
            context.size = size
            context.templateItem = templateItem
            context.templateInfo = templateInfo
            context.visualTemplateNode = visualTemplateNode
            return context
        }

        fun getContext(targetView: View): GXTemplateContext? {
            if (targetView is GXIRootView) {
                return targetView.getTemplateContext()
            }
            return null
        }
    }
}