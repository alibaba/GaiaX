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

@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.alibaba.gaiax

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import app.visly.stretch.Size
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.data.GXDataImpl
import com.alibaba.gaiax.data.assets.GXAssetsBinaryWithoutSuffixTemplate
import com.alibaba.gaiax.data.assets.GXAssetsTemplate
import com.alibaba.gaiax.data.cache.GXTemplateInfoSource
import com.alibaba.gaiax.render.GXRenderImpl
import com.alibaba.gaiax.render.node.*
import com.alibaba.gaiax.render.utils.GXContainerUtils
import com.alibaba.gaiax.render.utils.GXIManualExposureEventListener
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.template.GXCss
import com.alibaba.gaiax.template.GXStyleConvert
import com.alibaba.gaiax.template.GXTemplateKey

/**
 * GaiaX engine class.
 *
 * GaiaX dynamic template engine is a lightweight cross-platform solution of pure native dynamic card, developed by Alibaba YouKu technology team.
 * Except client SDK, we provide the template visual build tool - GaiaStudio, and Demo Project - template sample and real time preview, it support to create template, edit template, real machine debug and real time preview.
 * Dynamic template engine aims to ensure that the native experience and performance at the same time, help the client achieve low code.
 *
 * Basic usage:
 * ```
 * // Init GXTemplateEngine
 * GXTemplateEngine.instance.init(activity)
 * // Build template params
 * val params = GXTemplateEngine.GXTemplateItem(activity, "template biz", "template id")
 * // Build measure size
 * val size = GXTemplateEngine.GXMeasureSize(screenWidth, null)
 * // Build template data
 * val templateData = GXTemplateEngine.GXTemplateData(jsonData)
 * // Create view
 * val view = GXTemplateEngine.instance.createView(params, size)
 * // Update data
 * GXTemplateEngine.instance.bindData(view, templateData)
 * // Inject view to container
 *  findViewById<LinearLayoutCompat>(R.id.xxx).addView(view, 0)
 * ```
 */
class GXTemplateEngine {

    /**
     * GaiaX Engine adapter.
     * The Adapter can use to register some extendable function, For example: lottie, expression, view-support
     * ```
     * GXRegisterCenter.instance
     *  // lottie
     *  .registerExtensionLottieAnimation(GXExtensionLottieAnimation())
     *  // expression
     *  .registerExtensionExpression(GXExtensionExpression())
     *  // image view support
     *  .registerExtensionViewSupport(
     *      GXViewKey.VIEW_TYPE_IMAGE,
     *      GXAdapterImageView::class.java
     *  )
     * ```
     */
    interface GXIAdapter {
        fun init(context: Context)
    }

    /**
     * Data processing parameter
     */
    abstract class GXData {

        /**
         * View index
         */
        var index: Int? = null

        /**
         * Target view
         */
        var view: View? = null

        /**
         * Node id
         */
        var nodeId: String? = null

        /**
         * Template information
         */
        var templateItem: GXTemplateItem? = null
    }

    /**
     * Event parameters
     */
    open class GXGesture {

        /**
         * Event types：tap, longpress
         */
        var gestureType = GXTemplateKey.GAIAX_GESTURE_TYPE_TAP

        /**
         * Target view
         */
        var view: View? = null

        /**
         * Node id
         */
        var nodeId: String? = null

        /**
         * Template information
         */
        var templateItem: GXTemplateItem? = null

        /**
         * View index
         */
        var index: Int? = null

        /**
         * Event data
         */
        var eventParams: JSONObject? = null

        override fun toString(): String {
            return "GXGesture(gestureType='$gestureType', view=$view, nodeId=$nodeId, index=$index, eventParams=$eventParams)"
        }
    }

    /**
     * Track event parameters
     */
    class GXTrack {

        /**
         * Target view
         */
        var view: View? = null

        /**
         * Node id
         */
        var nodeId: String? = null

        /**
         * View index
         */
        var index: Int? = null

        /**
         * Template information
         */
        var templateItem: GXTemplateItem? = null

        /**
         * Buried data
         */
        var trackParams: JSONObject? = null

        override fun toString(): String {
            return "GXTrack(view=$view, nodeId=$nodeId, index=$index, trackParams=$trackParams)"
        }
    }

    /**
     * Text data processing parameters
     */
    class GXTextData : GXData() {

        /**
         * Text content
         */
        var text: CharSequence? = null

        /**
         * Node style
         */
        var nodeCss: GXCss? = null

        /**
         * Node data
         */
        var nodeData: JSONObject? = null

        var extendData: JSONObject? = null
    }

    /**
     * Scroll parameters
     */
    class GXScroll {
        companion object {
            const val TYPE_ON_SCROLL_STATE_CHANGED = "onScrollStateChanged"
            const val TYPE_ON_SCROLLED = "onScrolled"
        }

        var type: String = ""

        /**
         * Target view
         */
        var view: View? = null

        /**
         * Offset x
         */
        var dx: Int = 0

        /**
         * Offset y
         */
        var dy: Int = 0

        /**
         * Scroll state
         */
        var state: Int = 0

        override fun toString(): String {
            return "GXScroll(view=$view, dx=$dx, dy=$dy, state=$state)"
        }
    }

    /**
     * Animation event parameters
     */
    class GXAnimation {

        companion object {
            const val STATE_START = "START"
            const val STATE_END = "END"
        }

        /**
         * Animation state
         * START/END
         */
        var state: String? = null

        /**
         * Node id
         */
        var nodeId: String? = null

        /**
         * Lottie target view
         */
        var view: View? = null

        /**
         * Animation params
         */
        var animationParams: JSONObject? = null

        override fun toString(): String {
            return "GXAnimation(type=$state, nodeId=$nodeId, targetView=$view)"
        }
    }

    /**
     * Custom view bind data interface
     */
    interface GXICustomViewBindData : GXIViewBindData {
        override fun onBindData(data: JSONObject?) {}
    }

    /**
     * Event listener
     */
    interface GXIEventListener {

        /**
         * Gesture event
         */
        fun onGestureEvent(gxGesture: GXGesture) {}

        /**
         * Scroll event
         */
        fun onScrollEvent(gxScroll: GXScroll) {}

        /**
         * Animation event
         */
        fun onAnimationEvent(gxAnimation: GXAnimation) {}
    }

    /**
     * Track listener
     */
    interface GXITrackListener {

        /**
         * Track event
         */
        fun onTrackEvent(gxTrack: GXTrack) {}

        /**
         * 会在事件执行时，SDK内部会回调手动点击事件。
         * https://www.yuque.com/biezhihua/gaiax/ld6iie
         */
        fun onManualClickTrackEvent(gxTrack: GXTrack) {}

        /**
         * 会在业务手动调用onAppear后，SDK内部会回调手动曝光事件
         * https://www.yuque.com/biezhihua/gaiax/ld6iie
         */
        fun onManualExposureTrackEvent(gxTrack: GXTrack) {}
    }

    /**
     * Data processing listeners
     */
    interface GXIDataListener {

        /**
         * Text processing event
         */
        fun onTextProcess(gxTextData: GXTextData): CharSequence? = null
    }

    /**
     *
     * GXMeasureSize是一个限制性的概念，它规定的是GaiaX模板内容的最大显示区域，并且是非必传的。
     *
     * 一般来说我们有三种使用情况。
     *
     * 1. 固定的MeasureSize-Width和固定的MeasureSize-Height。
     *    如果内部的模板超出了宽度和高度，那么内容不会被显示，最终产物View就是MeasureSize-Width和MeasureSize-height。
     * 2. 固定的MeasureSize-Width，不确定的MeasureSize-Height（不传入）。
     *    如果内部的模板实际宽度超出了MeasureSize-width，那么超出的部分不会被显示；
     *    如果内部模板的高度能够自我撑开（具备高度）；
     *    最终产物View的宽度就是MeasureSize-Width，而高度就是模板通过FlexBox计算后撑开的高度。
     * 3. 不确定的MeasureSize-Width（不传入），确定的MeasureSize-Height。
     *    这种情况下，如果内部的模板实际高度超出了MeasureSize-height，那么超出的部分不会被显示；
     *    如果内部模板的宽度能够自我撑开（具备宽度）；
     *    最终产物View的宽度就是是模板通过FlexBox计算后撑开的宽度，而高度就是Measure-Height。
     */
    data class GXMeasureSize(var width: Float?, var height: Float?) {

        override fun toString(): String {
            return "GXMeasureSize(width=$width, height=$height)"
        }
    }

    /**
     * Template data parameters
     */
    data class GXTemplateData(
        /**
         * Template data, used to bind data to the view
         */
        val data: JSONObject
    ) {

        var tag: Any? = null

        /**
         * @suppress
         */
        var scrollIndex: Int = -1

        /**
         * Data listener
         */
        var dataListener: GXIDataListener? = null

        /**
         * Event listener
         */
        var eventListener: GXIEventListener? = null

        /**
         * Track listener
         */
        var trackListener: GXITrackListener? = null

    }

    /**
     * Template information parameters
     */
    data class GXTemplateItem(
        /**
         * Android context
         */
        val context: Context,
        /**
         * Template biz id
         */
        var bizId: String,
        /**
         * Template id
         */
        val templateId: String
    ) {

        /**
         * Used to relocate template value paths
         * @suppress
         */
        var bundle: String = ""

        /**
         * Template version, not currently in use
         * @suppress
         */
        var templateVersion: String = ""

        /**
         * Is local template
         * @suppress
         */
        var isLocal: Boolean = false

        override fun toString(): String {
            return "GXTemplateItem(context=$context, bizId='$bizId', templateId='$templateId', templateVersion='$templateVersion'"
        }
    }

    internal lateinit var context: Context

    internal val data by lazy {
        val data = GXDataImpl(context)
        data
    }

    internal val render by lazy {
        GXRenderImpl()
    }

    internal fun createTemplateContext(
        gxTemplateItem: GXTemplateItem,
        gxMeasureSize: GXMeasureSize,
        gxVisualTemplateNode: GXTemplateNode?
    ): GXTemplateContext {
        val templateInfo = data.getTemplateInfo(gxTemplateItem)
        return GXTemplateContext.createContext(
            gxTemplateItem,
            gxMeasureSize,
            templateInfo,
            gxVisualTemplateNode
        )
    }

    fun destroyView(targetView: View?) {
        GXTemplateContext.getContext(targetView)?.release()
        GXTemplateContext.setContext(null)
    }

    /**
     * To create template's view with template information and template measure size.
     *
     * @param gxTemplateItem The template information, it contains template id(templateId) and template business id(bizId).
     * @param gxMeasureSize The template measure size, it look like a viewport of draw system, use to sure a size of template' view.
     *
     * @return A view generated by a template
     */
    fun createView(
        gxTemplateItem: GXTemplateItem,
        gxMeasureSize: GXMeasureSize,
        gxVisualTemplateNode: GXTemplateNode? = null
    ): View? {
        return try {
            val gxTemplateContext = createViewOnlyNodeTree(
                gxTemplateItem,
                gxMeasureSize,
                gxVisualTemplateNode
            )
            if (gxTemplateContext != null) {
                createViewOnlyViewTree(gxTemplateContext)
            } else {
                null
            }
        } catch (e: Exception) {
            val extensionException = GXRegisterCenter.instance.extensionException
            if (extensionException != null) {
                extensionException.exception(e)
                null
            } else {
                throw e
            }
        }
    }

    /**
     * To bind template data
     *
     * @param gxView The root view
     * @param gxTemplateData The template data
     * @param gxMeasureSize The template measure size, it look like a viewport of draw system, use to sure a size of template' view.
     * @throws IllegalArgumentException
     */
    fun bindData(
        gxView: View?,
        gxTemplateData: GXTemplateData,
        gxMeasureSize: GXMeasureSize? = null
    ) {
        try {
            if (gxView == null) {
                throw IllegalArgumentException("view is null")
            }

            bindDataOnlyNodeTree(gxView, gxTemplateData, gxMeasureSize)
            bindDataOnlyViewTree(gxView, gxTemplateData, gxMeasureSize)

        } catch (e: Exception) {
            val extensionException = GXRegisterCenter.instance.extensionException
            if (extensionException != null) {
                extensionException.exception(e)
            } else {
                throw e
            }
        }
    }

    /**
     * 当measure size发生变化的时候需要重新计算节点树，否则会导致bindData的传入数据不准确，引发布局错误
     */
    private fun recomputeWhenMeasureSizeChanged(
        gxTemplateContext: GXTemplateContext,
        isMeasureSizeChanged: Boolean
    ) {
        val gxRootNode = gxTemplateContext.rootNode
        if (gxRootNode != null && isMeasureSizeChanged) {
            gxRootNode.resetTree(gxTemplateContext)
            val size = Size(gxTemplateContext.size.width, gxTemplateContext.size.height)
            GXNodeUtils.computeNodeTreeByCreateView(gxRootNode, size)
        }
    }

    /**
     * @suppress
     * @hide
     */
    fun createViewOnlyNodeTree(
        gxTemplateItem: GXTemplateItem,
        gxMeasureSize: GXMeasureSize,
        gxVisualTemplateNode: GXTemplateNode?
    ): GXTemplateContext? {
        return try {
            internalCreateViewOnlyNodeTree(gxTemplateItem, gxMeasureSize, gxVisualTemplateNode)
        } catch (e: Exception) {
            val extensionException = GXRegisterCenter.instance.extensionException
            if (extensionException != null) {
                extensionException.exception(e)
                null
            } else {
                throw e
            }
        }
    }

    private fun internalCreateViewOnlyNodeTree(
        gxTemplateItem: GXTemplateItem,
        gxMeasureSize: GXMeasureSize,
        gxVisualTemplateNode: GXTemplateNode?
    ): GXTemplateContext {
        val templateInfo = data.getTemplateInfo(gxTemplateItem)
        val context = GXTemplateContext.createContext(
            gxTemplateItem,
            gxMeasureSize,
            templateInfo,
            gxVisualTemplateNode
        )
        render.createViewOnlyNodeTree(context)
        return context
    }

    /**
     * @suppress
     * @hide
     */
    fun createViewOnlyViewTree(
        gxTemplateContext: GXTemplateContext
    ): View? {
        return try {
            internalCreateViewOnlyViewTree(gxTemplateContext)
        } catch (e: Exception) {
            val extensionException = GXRegisterCenter.instance.extensionException
            if (extensionException != null) {
                extensionException.exception(e)
                null
            } else {
                throw e
            }
        }
    }

    private fun internalCreateViewOnlyViewTree(gxTemplateContext: GXTemplateContext): View {
        return render.createViewOnlyViewTree(gxTemplateContext)
    }

    /**
     * @suppress
     * @hide
     */
    fun bindDataOnlyNodeTree(
        view: View,
        gxTemplateData: GXTemplateData,
        gxMeasureSize: GXMeasureSize? = null
    ) {
        try {
            internalBindDataOnlyNodeTree(view, gxTemplateData, gxMeasureSize)
        } catch (e: Exception) {
            val extensionException = GXRegisterCenter.instance.extensionException
            if (extensionException != null) {
                extensionException.exception(e)
            } else {
                throw e
            }
        }
    }

    private fun internalBindDataOnlyNodeTree(
        view: View,
        gxTemplateData: GXTemplateData,
        gxMeasureSize: GXMeasureSize? = null
    ) {
        val gxTemplateContext = GXTemplateContext.getContext(view)
            ?: throw IllegalArgumentException("Not found templateContext from targetView")

        var isMeasureSizeChanged = false
        if (gxMeasureSize != null) {
            val oldMeasureSize = gxTemplateContext.size
            gxTemplateContext.size = gxMeasureSize
            isMeasureSizeChanged = oldMeasureSize.width != gxMeasureSize.width ||
                    oldMeasureSize.height != gxMeasureSize.height
        }

        gxTemplateContext.templateData = gxTemplateData

        processContainerItemManualExposureWhenScrollStateChanged(gxTemplateContext)

        recomputeWhenMeasureSizeChanged(gxTemplateContext, isMeasureSizeChanged)

        render.bindViewDataOnlyNodeTree(gxTemplateContext)
    }

    /**
     * @suppress
     * @hide
     */
    fun bindDataOnlyViewTree(
        view: View,
        gxTemplateData: GXTemplateData,
        gxMeasureSize: GXMeasureSize? = null
    ) {
        try {
            internalBindDataOnlyViewTree(view, gxTemplateData, gxMeasureSize)
        } catch (e: Exception) {
            val extensionException = GXRegisterCenter.instance.extensionException
            if (extensionException != null) {
                extensionException.exception(e)
            } else {
                throw e
            }
        }
    }

    private fun internalBindDataOnlyViewTree(
        view: View,
        gxTemplateData: GXTemplateData,
        gxMeasureSize: GXMeasureSize? = null
    ) {
        val gxTemplateContext = GXTemplateContext.getContext(view)
            ?: throw IllegalArgumentException("Not found templateContext from targetView")

        if (gxMeasureSize != null) {
            gxTemplateContext.size = gxMeasureSize
        }

        gxTemplateContext.templateData = gxTemplateData

        render.bindViewDataOnlyViewTree(gxTemplateContext)
    }

    /**
     * Getting the template context
     * @suppress
     * @hide
     */
    fun getGXTemplateContext(targetView: View?): GXTemplateContext? {
        return targetView?.let { GXTemplateContext.getContext(it) }
    }

    /**
     * Get template View
     * @suppress
     */
    fun getGXViewById(targetView: View?, id: String): View? {
        return GXTemplateContext.getContext(targetView)?.rootNode?.getGXViewById(id)
    }

    /**
     * Get GXNode
     * @suppress
     * @hide
     */
    fun getGXNodeById(targetView: View?, id: String): GXNode? {
        return GXTemplateContext.getContext(targetView)?.rootNode?.getGXNodeById(id)
    }

    /**
     * 当View可见时
     */
    fun onAppear(targetView: View) {
        GXTemplateContext.getContext(targetView)?.let { gxTemplateContext ->
            gxTemplateContext.isAppear = true
            gxTemplateContext.manualExposure()
            GXContainerUtils.notifyOnAppear(gxTemplateContext)
        }
    }

    /**
     * 当View不可见时
     */
    fun onDisappear(targetView: View) {
        GXTemplateContext.getContext(targetView)?.let { gxTemplateContext ->
            gxTemplateContext.isAppear = false
            GXContainerUtils.notifyOnDisappear(gxTemplateContext)
        }
    }

    /**
     * SDK initialization method
     */
    fun init(context: Context): GXTemplateEngine {
        this.context = context.applicationContext
        GXStyleConvert.instance.init(context.assets)
        GXRegisterCenter.instance
            // priority 0
            .registerExtensionTemplateInfoSource(GXTemplateInfoSource.instance, 0)
            // priority 0
            .registerExtensionTemplateSource(GXAssetsBinaryWithoutSuffixTemplate(this.context), 0)
            // priority 1
            .registerExtensionTemplateSource(GXAssetsTemplate(this.context), 1)

        // init adapter
        // 无实际的依赖关系，仅仅是帮助GXAdapter进行初始化
        // GXAdapter内的逻辑可以写在任意地方
        initGXAdapter()?.init(context)
        return this
    }

    private fun initGXAdapter(): GXIAdapter? {
        return try {
            val clazz = Class.forName("com.alibaba.gaiax.adapter.GXAdapter")
            clazz.newInstance() as GXIAdapter
        } catch (e: Exception) {
            null
        }
    }

    private fun processContainerItemManualExposureWhenScrollStateChanged(gxTemplateContext: GXTemplateContext) {
        val eventListener = gxTemplateContext.templateData?.eventListener
        if (gxTemplateContext.containers.isNotEmpty() && eventListener !is GXIManualExposureEventListener) {
            gxTemplateContext.templateData?.eventListener =
                object : GXIManualExposureEventListener {
                    override fun onGestureEvent(gxGesture: GXGesture) {
                        eventListener?.onGestureEvent(gxGesture)
                    }

                    override fun onScrollEvent(gxScroll: GXScroll) {
                        eventListener?.onScrollEvent(gxScroll)
                        if (gxTemplateContext.isAppear) {
                            if (GXScroll.TYPE_ON_SCROLL_STATE_CHANGED == gxScroll.type && gxScroll.state == RecyclerView.SCROLL_STATE_IDLE) {
                                GXContainerUtils.notifyOnAppear(gxTemplateContext)
                            }
                        }
                    }

                    override fun onAnimationEvent(gxAnimation: GXAnimation) {
                        eventListener?.onAnimationEvent(gxAnimation)
                    }
                }
        }
    }

    companion object {

        val instance by lazy {
            GXTemplateEngine()
        }
    }
}