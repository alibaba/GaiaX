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

package com.alibaba.gaiax.render.view.container

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.render.node.GXNodeUtils
import com.alibaba.gaiax.render.node.GXTemplateNode
import com.alibaba.gaiax.render.view.basic.GXItemContainer
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.utils.getStringExt
import com.alibaba.gaiax.utils.getStringExtCanNull

/**
 * @suppress
 */
class GXViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var childTag: Any? = null
    var childTemplateItem: GXTemplateEngine.GXTemplateItem? = null
    var childMeasureSize: GXTemplateEngine.GXMeasureSize? = null
    var childVisualNestTemplateNode: GXTemplateNode? = null
}

/**
 * @suppress
 */
class GXContainerViewAdapter(val gxTemplateContext: GXTemplateContext, val gxNode: GXNode, val container: GXContainer) : RecyclerView.Adapter<GXViewHolder>() {

    private var containerData: JSONArray = JSONArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GXViewHolder {

        // 准备构建坑位容器的参数
        val childTemplateItem = viewTypeMap[viewType] ?: throw IllegalArgumentException("GXTemplateItem not exist, viewType = $viewType, viewTypeMap = $viewTypeMap")
        val childVisualNestTemplateNode = getVisualNestTemplateNode(childTemplateItem)
        val childItemViewPort = GXNodeUtils.computeItemViewPort(gxTemplateContext, gxNode)
        val childMeasureSize = GXTemplateEngine.GXMeasureSize(childItemViewPort.width, childItemViewPort.height)
        val childTemplateContext = GXTemplateEngine.instance.createTemplateContext(childTemplateItem, childMeasureSize, childVisualNestTemplateNode)
        // TODO: 此处可能有耗时问题，可以进行优化
        val childContainerSize = GXNodeUtils.computeContainerItemSize(childTemplateContext, gxNode, childTemplateItem, childVisualNestTemplateNode, containerData)

        // 构建坑位的容器
        val childItemContainer = GXItemContainer(parent.context)
        val containerWidthLP = childContainerSize?.width?.toInt() ?: FrameLayout.LayoutParams.WRAP_CONTENT
        val containerHeightLP = childContainerSize?.height?.toInt() ?: FrameLayout.LayoutParams.WRAP_CONTENT
        childItemContainer.layoutParams = FrameLayout.LayoutParams(containerWidthLP, containerHeightLP)

        // 返回ViewHolder
        return GXViewHolder(childItemContainer).apply {
            this.childTemplateItem = childTemplateItem
            this.childVisualNestTemplateNode = childVisualNestTemplateNode
            this.childMeasureSize = childMeasureSize
        }
    }

    private fun getVisualNestTemplateNode(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplateNode? {
        gxNode.childTemplateItems?.forEach {
            if (it.first.templateId == gxTemplateItem.templateId) {
                return it.second
            }
        }
        return null
    }

    override fun onBindViewHolder(holder: GXViewHolder, position: Int) {
        val childTemplateItem = holder.childTemplateItem ?: throw IllegalArgumentException("childTemplateItem is null")
        val childVisualNestTemplateNode = holder.childVisualNestTemplateNode ?: throw IllegalArgumentException("childVisualNestTemplateNode is null")
        val childMeasureSize = holder.childMeasureSize ?: throw IllegalArgumentException("childMeasureSize is null")
        val childItemContainer = holder.itemView as ViewGroup
        val childItemPosition = holder.adapterPosition
        val childItemData = containerData.getJSONObject(childItemPosition) ?: JSONObject()

        val processContainerItemBind = GXRegisterCenter.instance.processContainerItemBind
        if (processContainerItemBind != null) {
            holder.childTag = processContainerItemBind.bindViewHolder(
                gxTemplateContext.templateData?.tag,
                childItemContainer,
                childMeasureSize,
                childTemplateItem,
                childItemPosition,
                childVisualNestTemplateNode,
                childItemData)
        } else {

            // 获取坑位View
            val childView = if (childItemContainer.childCount != 0) {
                childItemContainer.getChildAt(0)
            } else {
                val childView = GXTemplateEngine.instance.createView(childTemplateItem, childMeasureSize, childVisualNestTemplateNode)
                childItemContainer.addView(childView)
                childView
            }

            // 为坑位View绑定数据
            val childTemplateData = GXTemplateEngine.GXTemplateData(childItemData)
            GXTemplateEngine.instance.bindData(childView, childTemplateData)
        }
    }

    /**
     * key: viewType
     * value: templateItem
     */
    private val viewTypeMap: MutableMap<Int, GXTemplateEngine.GXTemplateItem> = mutableMapOf()

    /**
     * key: position
     * value: templateItem
     */
    private val positionMap: MutableMap<Int, GXTemplateEngine.GXTemplateItem> = mutableMapOf()

    override fun getItemViewType(position: Int): Int {
        val templateItem = getCurrentPositionTemplateItem(position)
        if (templateItem != null) {
            val viewType: Int = templateItem.templateId.hashCode()
            viewTypeMap[viewType] = templateItem
            positionMap[position] = templateItem
            return viewType
        }
        return super.getItemViewType(position)
    }

    /**
     * Gets the template ID of the current pit
     */
    private fun getCurrentPositionTemplateItem(position: Int): GXTemplateEngine.GXTemplateItem? {
        gxNode.childTemplateItems?.let { items ->
            if (items.size > 1) {
                val itemData = containerData.getJSONObject(position)
                val dataBinding = gxNode.templateNode.dataBinding
                dataBinding?.reset()
                dataBinding?.getExtend(itemData)?.let { typeData ->
                    val path = typeData.getStringExt("${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE}.${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE_PATH}")
                    val templateId = typeData.getStringExtCanNull("${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE}.${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE_CONFIG}.${path}")
                    if (templateId != null) {
                        return items.firstOrNull { it.first.templateId == templateId }?.first
                    }
                }
            } else {
                return items.firstOrNull()?.first
            }
        }
        return null
    }

    override fun getItemCount(): Int {
        return containerData.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setContainerData(data: JSONArray) {
        viewTypeMap.clear()
        positionMap.clear()
        val containerDataUpdate = GXRegisterCenter.instance.processContainerDataUpdate
        if (containerDataUpdate != null) {
            val oldData = containerData
            val newData = data
            containerData = data
            containerDataUpdate.update(gxTemplateContext, this, oldData, newData)
        } else {
            containerData = data
            notifyDataSetChanged()
        }
    }

    fun isNeedForceRefresh(targetWidth: Float): Boolean {
        return gxNode.stretchNode.finalLayout?.width != targetWidth
    }

}