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
import app.visly.stretch.Layout
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.template.GXLayer
import com.alibaba.gaiax.template.GXTemplateKey

/**
 * @suppress
 */
class GXNode {

    /**
     * TODO:
     * 此处的缓存可能会导致GXItemContainer高度不正确。
     * 在有些情况下，业务会根据数据动态修改高度，在首次的时候没有这些数据，
     * 从而计算出一个高度并缓存，当第二次再刷新数据时带有这些数据，就不会再去计算新的高度了，导致问题。
     */
    var multiTypeItemComputeCache: MutableMap<GXTemplateEngine.GXTemplateItem, Layout>? =
        null

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
     * ID路径
     */
    var idPath = ""

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
    var boxLayoutView: View? = null

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
        templateItem: GXTemplateEngine.GXTemplateItem,
        visualTemplateNode: GXTemplateNode
    ) {
        if (childTemplateItems == null) {
            childTemplateItems = mutableListOf()
        }
        childTemplateItems?.add(Pair(templateItem, visualTemplateNode))
    }

    fun release() {
        isAnimating = false
        idPath = ""
        view = null
        boxLayoutView = null
        stretchNode.free()
        children?.forEach {
            it.release()
        }
        children?.clear()
        parentNode = null
    }

    fun getType() = templateNode.getNodeType()

    fun getCustomViewClass() = templateNode.getCustomViewClass()

    fun isTextType(): Boolean = templateNode.isTextType()

    fun isRichTextType(): Boolean = templateNode.isRichTextType()

    fun isGaiaTemplateType(): Boolean = templateNode.isGaiaTemplateType()

    fun isViewType(): Boolean = templateNode.isViewType()

    fun isIconFontType(): Boolean = templateNode.isIconFontType()

    fun isImageType(): Boolean = templateNode.isImageType()

    fun isContainerType(): Boolean = templateNode.isContainerType()

    fun isCustomViewType(): Boolean = templateNode.isCustomType()

    fun isGridType(): Boolean = templateNode.isGridType()

    fun isScrollType(): Boolean = templateNode.isScrollType()

    fun isSliderType(): Boolean = templateNode.isSliderType()

    fun isProgressType(): Boolean = templateNode.isProgressType()

    fun isNeedShadow(): Boolean {
        return (isViewType() || isImageType()) && templateNode.css.style.boxShadow != null
    }

    fun isNeedLottie(): Boolean {
        return templateNode.animationBinding?.type?.equals(GXTemplateKey.GAIAX_ANIMATION_TYPE_LOTTIE,true) == true
    }

    fun setIdPath(parent: GXNode?, layer: GXLayer) {
        id = layer.id
        idPath = if (parent != null) {
            if (idPath.isNotEmpty()) {
                "${parent.idPath}@${idPath}@${layer.id}"
            } else {
                "${parent.idPath}@${layer.id}"
            }
        } else {
            if (idPath.isNotEmpty()) {
                "${idPath}@${layer.id}"
            } else {
                layer.id
            }
        }
    }

    override fun toString(): String {
        return "GXNode(id='$id', idPath='$idPath', isRoot=$isRoot, isNestRoot=$isNestRoot, templateNode=$templateNode, stretchNode=$stretchNode, children=${children?.size})"
    }

    fun initEventByRegisterCenter() {
        if (event == null) {
            event = GXRegisterCenter.instance.extensionNodeEvent?.create()
        }
    }

    fun resetTree(gxTemplateContext: GXTemplateContext) {
        templateNode.reset()
        stretchNode.reset(gxTemplateContext, this.templateNode)
        children?.forEach {
            it.resetTree(gxTemplateContext)
        }
    }
}
