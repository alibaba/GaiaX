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

package com.alibaba.gaiax.render.node

import android.animation.AnimatorSet
import android.view.View
import app.visly.stretch.Display
import app.visly.stretch.Layout
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.GXIRelease
import com.alibaba.gaiax.render.view.basic.GXShadowLayout
import com.alibaba.gaiax.template.GXTemplateKey

/**
 * @suppress
 */
class GXNode {

    var isFitContentModify: Boolean = false

    /**
     * 属性动画
     */
    var propAnimatorSet: AnimatorSet? = null

    /**
     * 是否在执行动画中
     */
    var isAnimating = false

    /**
     * ID
     */
    var id = ""

    /**
     * 是否是根节点
     */
    var isRoot: Boolean = false

    /**
     * 是否是嵌套模板节点
     */
    var isNestRoot: Boolean = false

    /**
     * View引用
     */
    var view: View? = null

    /**
     * 同级阴影View引用
     */
    var boxLayoutView: GXShadowLayout? = null

    /**
     * 节点上覆盖的lottieView
     */
    var lottieView: View? = null

    /**
     * 节点的模板数据
     */
    lateinit var templateNode: GXTemplateNode

    /**
     * 节点虚拟数据
     */
    lateinit var stretchNode: GXStretchNode

    var layoutByPrepare: Layout? = null

    var layoutByBind: Layout? = null

    /**
     * 父节点
     */
    var parentNode: GXNode? = null

    /**
     * 子节点
     */
    var children: MutableList<GXNode>? = null

    /**
     * 事件处理器
     */
    var event: GXINodeEvent? = null

    /**
     * 容器嵌套子模板
     */
    var childTemplateItems: MutableList<Pair<GXTemplateEngine.GXTemplateItem, GXTemplateNode>>? =
        null

    fun addChildTemplateItems(
        templateItem: GXTemplateEngine.GXTemplateItem, visualTemplateNode: GXTemplateNode
    ) {
        if (childTemplateItems == null) {
            childTemplateItems = mutableListOf()
        }
        childTemplateItems?.add(Pair(templateItem, visualTemplateNode))
    }

    fun release() {
        isAnimating = false
        if (view is GXIRelease) {
            (view as GXIRelease).release()
        }
        view = null
        boxLayoutView = null
        stretchNode.free()
        children?.forEach {
            it.release()
        }
        children?.clear()
        parentNode = null
        isFitContentModify = false
    }

    fun getType() = templateNode.layer.getNodeType

    fun getCustomViewClass() = templateNode.layer.customNodeClass

    fun isTextType(): Boolean = templateNode.layer.isTextType

    fun isRichTextType(): Boolean = templateNode.layer.isRichTextType

    fun isGaiaTemplateType(): Boolean = templateNode.layer.isGaiaTemplate

    fun isViewType(): Boolean = templateNode.layer.isViewType

    fun isIconFontType(): Boolean = templateNode.layer.isIconFontType

    fun isImageType(): Boolean = templateNode.layer.isImageType

    fun isContainerType(): Boolean = templateNode.layer.isContainerType

    fun isCustomViewType(): Boolean = templateNode.layer.isCustomType

    fun isGridType(): Boolean = templateNode.layer.isGridType

    fun isScrollType(): Boolean = templateNode.layer.isScrollType

    fun isSliderType(): Boolean = templateNode.layer.isSliderType

    fun isProgressType(): Boolean = templateNode.layer.isProgressType

    fun isNeedShadow(): Boolean {
        return (isViewType() || isImageType()) && templateNode.css.style.boxShadow != null
    }

    fun isNeedLottie(): Boolean {
        return templateNode.animationBinding?.type?.equals(
            GXTemplateKey.GAIAX_ANIMATION_TYPE_LOTTIE, true
        ) == true
    }

    /**
     * 重置节点中的缓存
     */
    fun resetFromResize(gxTemplateContext: GXTemplateContext) {
        isFitContentModify = false
        layoutByBind = null
        templateNode.reset()
        stretchNode.resetFromResize(gxTemplateContext, this)
        children?.forEach {
            it.resetFromResize(gxTemplateContext)
        }
    }

    fun resetFromUpdate(gxTemplateContext: GXTemplateContext) {
        layoutByBind = null
        templateNode.reset()
        stretchNode.initStyle(gxTemplateContext, this)
    }

    fun isNodeVisibleInTree(): Boolean {
        return isNodeVisibleInTree(this)
    }

    private fun isNodeVisibleInTree(gxNode: GXNode): Boolean {
        if (gxNode.stretchNode.node?.getStyle()?.display == Display.None) {
            return false
        }
        gxNode.parentNode?.let {
            return isNodeVisibleInTree(it)
        }
        return true
    }

    fun getPaddingRect(): android.graphics.Rect {
        return templateNode.css.style.paddingForAndroid
    }

    fun initEventByRegisterCenter() {
        if (event == null) {
            event = GXRegisterCenter.instance.extensionNodeEvent?.create()
        }
    }

}
