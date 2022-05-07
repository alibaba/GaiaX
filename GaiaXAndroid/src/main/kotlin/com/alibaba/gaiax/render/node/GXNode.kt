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
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.template.GXLayer
import java.lang.ref.SoftReference

/**
 * @suppress
 */
class GXNode {

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
    var viewRef: SoftReference<View>? = null

    /**
     * 同级阴影View引用
     */
    var boxLayoutViewRef: SoftReference<View>? = null

    /**
     * 节点的模板数据
     */
    lateinit var templateNode: GXTemplateNode

    /**
     * 节点虚拟数据
     */
    lateinit var stretchNode: GXStretchNode

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
        viewRef?.clear()
        viewRef = null
        boxLayoutViewRef?.clear()
        boxLayoutViewRef = null
        stretchNode.free()
        children?.forEach {
            it.release()
        }
        children?.clear()
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

    fun isNeedShadow(): Boolean {
        return (isViewType() || isImageType()) && templateNode.css.style.boxShadow != null
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
}