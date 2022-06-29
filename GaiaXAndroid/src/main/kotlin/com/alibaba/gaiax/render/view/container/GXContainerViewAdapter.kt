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
import android.view.ViewGroup
import android.widget.FrameLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import com.alibaba.fastjson.JSON
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
import com.alibaba.gaiax.template.GXTemplateKey.GAIAX_CONTAINER_FOOTER
import com.alibaba.gaiax.utils.getStringExt
import com.alibaba.gaiax.utils.getStringExtCanNull

/**
 * @suppress
 */
class GXContainerViewAdapter(
    val gxTemplateContext: GXTemplateContext,
    val gxContainer: GXContainer
) : RecyclerView.Adapter<GXViewHolder>() {

    lateinit var gxNode: GXNode

    private var containerData: JSONArray = JSONArray()

    private var footerTemplateItem: GXTemplateEngine.GXTemplateItem? = null
    private var footerTypeHasMore: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GXViewHolder {
        return try {
            createGXViewHolder(viewType, parent)
        } catch (e: Exception) {
            val extensionException = GXRegisterCenter.instance.extensionException
            if (extensionException != null) {
                extensionException.exception(e)
                GXViewHolder(GXItemContainer(parent.context))
            } else {
                throw e
            }
        }
    }

    private fun createGXViewHolder(
        viewType: Int,
        parent: ViewGroup
    ): GXViewHolder {
        // 准备构建坑位容器的参数
        val childTemplateItem = viewTypeMap[viewType]
            ?: throw IllegalArgumentException("GXTemplateItem not exist, viewType = $viewType, viewTypeMap = $viewTypeMap")

        val isChildFooterItem = childTemplateItem == footerTemplateItem


        val childVisualNestTemplateNode = getVisualNestTemplateNode(childTemplateItem)

        val childItemViewPort = if (isChildFooterItem)
            GXNodeUtils.computeFooterItemViewPort(gxTemplateContext, gxNode)
        else
            GXNodeUtils.computeItemViewPort(gxTemplateContext, gxNode)

        val childMeasureSize = GXTemplateEngine.GXMeasureSize(
            childItemViewPort.width, childItemViewPort.height
        )

        val childTemplateContext = GXTemplateEngine.instance.createTemplateContext(
            childTemplateItem,
            childMeasureSize,
            childVisualNestTemplateNode
        )

        // TODO: 此处可能有耗时问题，可以进行优化
        val childContainerSize = if (isChildFooterItem)
            GXNodeUtils.computeContainerFooterItemSize(
                childTemplateContext,
                gxNode,
                childItemViewPort,
                childTemplateItem,
                childVisualNestTemplateNode,
                containerData
            )
        else
            GXNodeUtils.computeContainerItemSize(
                childTemplateContext,
                gxNode,
                childItemViewPort,
                childTemplateItem,
                childVisualNestTemplateNode,
                containerData
            )

        // 构建坑位的容器
        val childItemContainer = GXItemContainer(parent.context)

        val containerWidthLP = childContainerSize?.width?.toInt()
            ?: FrameLayout.LayoutParams.WRAP_CONTENT

        val containerHeightLP = childContainerSize?.height?.toInt()
            ?: FrameLayout.LayoutParams.WRAP_CONTENT

        childItemContainer.layoutParams = FrameLayout.LayoutParams(
            containerWidthLP, containerHeightLP
        )

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
        return try {
            bindGXViewHolder(holder)
        } catch (e: Exception) {
            val extensionException = GXRegisterCenter.instance.extensionException
            if (extensionException != null) {
                extensionException.exception(e)
            } else {
                throw e
            }
        }
    }

    private fun bindGXViewHolder(holder: GXViewHolder) {
        val childTemplateItem = holder.childTemplateItem
            ?: throw IllegalArgumentException("childTemplateItem is null")

        val childVisualNestTemplateNode = holder.childVisualNestTemplateNode

        val childMeasureSize = holder.childMeasureSize
            ?: throw IllegalArgumentException("childMeasureSize is null")

        val childItemContainer = holder.itemView as ViewGroup

        val childItemPosition = holder.adapterPosition

        val childItemData = if (childItemPosition < containerData.size)
            containerData.getJSONObject(childItemPosition) ?: JSONObject()
        else
            JSONObject()

        val processContainerItemBind = GXRegisterCenter.instance.extensionContainerItemBind
        if (processContainerItemBind != null) {
            holder.childTag = processContainerItemBind.bindViewHolder(
                gxTemplateContext.templateData?.tag,
                childItemContainer,
                childMeasureSize,
                childTemplateItem,
                childItemPosition,
                childVisualNestTemplateNode,
                childItemData
            )
        } else {

            // 获取坑位View
            val childView = if (childItemContainer.childCount != 0) {
                childItemContainer.getChildAt(0)
            } else {
                val childView = GXTemplateEngine.instance.createView(
                    childTemplateItem,
                    childMeasureSize,
                    childVisualNestTemplateNode
                )
                childItemContainer.addView(childView)
                childView
            }

            // 为坑位View绑定数据
            val childTemplateData = GXTemplateEngine.GXTemplateData(childItemData).apply {
                this.eventListener = object : GXTemplateEngine.GXIEventListener {
                    override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                        super.onGestureEvent(gxGesture)
                        gxTemplateContext.templateData?.eventListener?.onGestureEvent(gxGesture)
                    }

                    override fun onScrollEvent(gxScroll: GXTemplateEngine.GXScroll) {
                        super.onScrollEvent(gxScroll)
                        gxTemplateContext.templateData?.eventListener?.onScrollEvent(gxScroll)
                    }

                    override fun onAnimationEvent(gxAnimation: GXTemplateEngine.GXAnimation) {
                        super.onAnimationEvent(gxAnimation)
                        gxTemplateContext.templateData?.eventListener?.onAnimationEvent(gxAnimation)
                    }
                }

                this.trackListener = object : GXTemplateEngine.GXITrackListener {
                    override fun onTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                        super.onTrackEvent(gxTrack)
                        gxTemplateContext.templateData?.trackListener?.onTrackEvent(gxTrack)
                    }
                }

                this.dataListener = object : GXTemplateEngine.GXIDataListener {
                    override fun onTextProcess(gxTextData: GXTemplateEngine.GXTextData): CharSequence? {
                        return gxTemplateContext.templateData?.dataListener
                            ?.onTextProcess(gxTextData)
                    }
                }
            }
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

        // footer type
        val footerTemplateItem = footerTemplateItem
        if (footerTypeHasMore && footerTemplateItem != null && position == containerData.size) {
            val viewType: Int = footerTemplateItem.hashCode()
            viewTypeMap[viewType] = footerTemplateItem
            positionMap[position] = footerTemplateItem
            return viewType
        }

        // normal multi type
        val normalTemplateItem = getCurrentPositionTemplateItem(position)
        if (normalTemplateItem != null) {
            val viewType: Int = normalTemplateItem.templateId.hashCode()
            viewTypeMap[viewType] = normalTemplateItem
            positionMap[position] = normalTemplateItem
            return viewType
        }

        // normal type
        return super.getItemViewType(position)
    }

    /**
     * Gets the template ID of the current pit
     */
    private fun getCurrentPositionTemplateItem(position: Int): GXTemplateEngine.GXTemplateItem? {
        gxNode.childTemplateItems?.let { items ->
            if (items.size > 1) {
                val itemData = containerData.getJSONObject(position)
                gxNode.templateNode.resetData()
                gxNode.templateNode.getExtend(itemData)?.let { typeData ->
                    val path =
                        typeData.getStringExt("${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE}.${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE_PATH}")
                    val templateId =
                        typeData.getStringExtCanNull("${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE}.${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE_CONFIG}.${path}")
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
        return if (hasFooter()) {
            containerData.size + 1;
        } else {
            containerData.size
        }
    }

    fun hasFooter(): Boolean {
        return footerTemplateItem != null && footerTypeHasMore
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setContainerData(data: JSONArray) {
        viewTypeMap.clear()
        positionMap.clear()
        val containerDataUpdate = GXRegisterCenter.instance.extensionContainerDataUpdate
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

    fun initFooter() {
        val templateData: JSON = gxTemplateContext.templateData?.data ?: return
        val extend = gxNode.templateNode.getExtend(templateData)
        val footer = extend?.getJSONObject(GAIAX_CONTAINER_FOOTER)
        if (footer != null) {
            val templateId = footer.getString(GXTemplateKey.GAIAX_LAYER_ID)
            footerTemplateItem = GXTemplateEngine.GXTemplateItem(
                gxTemplateContext.context,
                gxTemplateContext.templateItem.bizId,
                templateId
            )
            footerTypeHasMore = footer.getBoolean(GXTemplateKey.GAIAX_CONTAINER_HAS_MORE) ?: false
        }
    }

    fun isNeedForceRefresh(targetWidth: Float): Boolean {
        val layoutByBind = gxNode.stretchNode.layoutByBind
        return layoutByBind?.width != targetWidth
    }

}