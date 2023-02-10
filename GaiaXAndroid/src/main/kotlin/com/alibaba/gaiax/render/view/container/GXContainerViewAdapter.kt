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
import androidx.recyclerview.widget.RecyclerView
import app.visly.stretch.Layout
import app.visly.stretch.Size
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

/**
 * @suppress
 */
class GXContainerViewAdapter(
    val gxTemplateContext: GXTemplateContext, private val gxContainer: GXContainer
) : RecyclerView.Adapter<GXViewHolder>() {

    private var position: Int = 0

    lateinit var gxNode: GXNode

    private var containerData: JSONArray = JSONArray()

    private var footerTemplateItem: GXTemplateEngine.GXTemplateItem? = null

    private var footerTypeHasMore: Boolean = false

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
        viewType: Int, parent: ViewGroup
    ): GXViewHolder {
        // 准备构建坑位容器的参数
        val templateItem = viewTypeMap[viewType]
            ?: throw IllegalArgumentException("GXTemplateItem not exist, viewType = $viewType, viewTypeMap = $viewTypeMap")

        val isFooterItem = templateItem == footerTemplateItem

        val visualNestTemplateNode = getVisualNestTemplateNode(templateItem)

        val itemViewPort = getItemViewPort(isFooterItem)

        val itemContainerSize = getItemContainerSize(
            isFooterItem, templateItem, visualNestTemplateNode, itemViewPort
        )

        val itemContainerLayoutParams = getItemContainerSize(itemContainerSize)

        // 构建坑位的容器
        val itemContainer = GXItemContainer(parent.context)

        gxNode.templateNode.layer.scrollConfig?.let {
            if (it.isHorizontal) {
                // 如果是scroll，并且是横向，那么可以设定gravity，需要让自己撑满容器
                itemContainer.gravity = it.gravity
            }
        }

        itemContainer.layoutParams = itemContainerLayoutParams

        // 返回ViewHolder
        return GXViewHolder(itemContainer).apply {
            this.templateItem = templateItem
        }
    }

    private fun getItemContainerSize(containerSize: Layout?): FrameLayout.LayoutParams {
        val itemContainerWidth =
            containerSize?.width?.toInt() ?: FrameLayout.LayoutParams.WRAP_CONTENT

        val itemContainerHeight = gxNode.templateNode.layer.scrollConfig?.let {
            if (it.isHorizontal) {
                // 如果容器高度小于坑位高度，那么需要重新设置容器高度
                if (containerSize != null && gxContainer.layoutParams.height < containerSize.height) {
                    containerSize.height.toInt()
                }
                // 如果容器高度大于于坑位高度,并且是横向，那么可以设定gravity，需要让自己撑满容器
                else {
                    gxContainer.layoutParams.height
                }
            } else {
                containerSize?.height?.toInt() ?: FrameLayout.LayoutParams.WRAP_CONTENT
            }
        } ?: run {
            containerSize?.height?.toInt() ?: FrameLayout.LayoutParams.WRAP_CONTENT
        }

        return FrameLayout.LayoutParams(
            itemContainerWidth, itemContainerHeight
        )
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

        val templateItem =
            holder.templateItem ?: throw IllegalArgumentException("templateItem is null")

        val isFooterItem = templateItem == footerTemplateItem

        val visualNestTemplateNode = getVisualNestTemplateNode(templateItem)

        val itemViewPort = getItemViewPort(isFooterItem)

        val itemMeasureSize = getMeasureSize(itemViewPort)

        val itemContainerSize = getItemContainerSize(
            isFooterItem, templateItem, visualNestTemplateNode, itemViewPort
        )

        val itemContainerLayoutParams = getItemContainerSize(itemContainerSize)

        val itemContainer = holder.itemView as GXItemContainer

        gxNode.templateNode.layer.scrollConfig?.let {
            if (it.isHorizontal) {
                // 如果是scroll，并且是横向，那么可以设定gravity，需要让自己撑满容器
                itemContainer.gravity = it.gravity
            }
        }

        itemContainer.layoutParams = itemContainerLayoutParams

        val itemPosition = holder.adapterPosition

        val itemData = if (itemPosition < containerData.size) {
            containerData.getJSONObject(itemPosition) ?: JSONObject()
        } else {
            JSONObject()
        }

        val processContainerItemBind = GXRegisterCenter.instance.extensionContainerItemBind
        if (processContainerItemBind != null) {
            holder.childTag = processContainerItemBind.bindViewHolder(
                gxTemplateContext.templateData?.tag,
                itemContainer,
                itemMeasureSize,
                templateItem,
                GXTemplateEngine.GXExtendParams().apply {
                    this.gxItemPosition = itemPosition
                    this.gxItemData = itemData
                    this.gxHostTemplateContext = gxTemplateContext
                    this.gxVisualTemplateNode = visualNestTemplateNode
                }
            )
        } else {

            // 获取坑位View
            val gxView = if (itemContainer.childCount != 0) {
                itemContainer.getChildAt(0)
            } else {

                GXTemplateEngine.instance.prepareView(templateItem, itemMeasureSize)

                val templateContext = GXTemplateEngine.instance.createViewOnlyNodeTree(
                    templateItem,
                    itemMeasureSize,
                    GXTemplateEngine.GXExtendParams().apply {
                        this.gxItemPosition = itemPosition
                        this.gxItemData = itemData
                        this.gxHostTemplateContext = gxTemplateContext
                        this.gxVisualTemplateNode = visualNestTemplateNode
                    }
                ) ?: throw IllegalArgumentException("Create GXTemplateContext fail, please check")

                val itemView = GXTemplateEngine.instance.createViewOnlyViewTree(templateContext)

                itemContainer.addView(itemView)
                itemView
            }

            // 为坑位View绑定数据
            val gxTemplateData = GXTemplateEngine.GXTemplateData(itemData).apply {
                this.eventListener = object : GXTemplateEngine.GXIEventListener {
                    override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                        super.onGestureEvent(gxGesture)
                        gxGesture.index = itemPosition
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
                        gxTrack.index = itemPosition
                        gxTemplateContext.templateData?.trackListener?.onTrackEvent(gxTrack)
                    }

                    override fun onManualClickTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                        gxTrack.index = itemPosition
                        gxTemplateContext.templateData?.trackListener?.onManualClickTrackEvent(
                            gxTrack
                        )
                    }

                    override fun onManualExposureTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                        gxTrack.index = itemPosition
                        gxTemplateContext.templateData?.trackListener?.onManualExposureTrackEvent(
                            gxTrack
                        )
                    }
                }

                this.dataListener = object : GXTemplateEngine.GXIDataListener {
                    override fun onTextProcess(gxTextData: GXTemplateEngine.GXTextData): CharSequence? {
                        return gxTemplateContext.templateData?.dataListener?.onTextProcess(
                            gxTextData
                        )
                    }
                }
            }

            if (gxView != null) {

                GXTemplateEngine.instance.bindDataOnlyNodeTree(
                    gxView, gxTemplateData, itemMeasureSize
                )

                GXTemplateEngine.instance.bindDataOnlyViewTree(
                    gxView, gxTemplateData, itemMeasureSize
                )

                // FIX: 重置容器的宽度，防止预计算和实际的宽度不相符
                itemContainer.layoutParams.width = gxView.layoutParams.width
            }
        }
    }

    private fun getItemContainerSize(
        isFooterItem: Boolean,
        gxTemplateItem: GXTemplateEngine.GXTemplateItem,
        gxVisualNestTemplateNode: GXTemplateNode?,
        itemViewPort: Size<Float?>
    ): Layout? {
        val itemData = containerData[position] as? JSONObject ?: JSONObject()
        return if (isFooterItem) {
            GXNodeUtils.computeScrollAndGridFooterItemContainerSize(
                gxTemplateContext,
                itemViewPort,
                gxTemplateItem,
                gxVisualNestTemplateNode,
                itemData,
                position
            )
        } else {
            GXNodeUtils.computeScrollAndGridItemContainerSize(
                gxTemplateContext, gxNode, itemData, position
            )
        }
    }

    private fun getMeasureSize(itemViewPort: Size<Float?>) = GXTemplateEngine.GXMeasureSize(
        itemViewPort.width, itemViewPort.height
    )

    private fun getItemViewPort(isFooterItem: Boolean) =
        if (isFooterItem) GXNodeUtils.computeScrollAndGridFooterItemViewPort(
            gxTemplateContext, gxNode
        )
        else GXNodeUtils.computeScrollAndGridItemViewPort(gxTemplateContext, gxNode)

    override fun getItemViewType(position: Int): Int {

        // footer type
        val footerTemplateItem = footerTemplateItem
        if (footerTypeHasMore && footerTemplateItem != null && position == containerData.size) {
            val viewType: Int = footerTemplateItem.hashCode()
            viewTypeMap[viewType] = footerTemplateItem
            positionMap[position] = footerTemplateItem
            return viewType
        }

        // update position
        this.position = position

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
                gxNode.templateNode.resetDataCache()
                val typeData = gxNode.templateNode.getExtend(itemData)
                if (typeData != null) {
                    val itemConfig =
                        "${GXNodeUtils.ITEM_CONFIG}.${typeData.getStringExt(GXNodeUtils.ITEM_PATH)}"
                    val templateId = typeData.getStringExt(itemConfig)
                    return items.firstOrNull { it.first.templateId == templateId }?.first
                }
            } else {
                return items.firstOrNull()?.first
            }
        }
        return null
    }

    override fun getItemCount(): Int {
        return if (hasFooter()) {
            containerData.size + 1
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
        position = 0
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
                gxTemplateContext.context, gxTemplateContext.templateItem.bizId, templateId
            )
            footerTypeHasMore = footer.getBoolean(GXTemplateKey.GAIAX_CONTAINER_HAS_MORE) ?: false
        }
    }

    fun isNeedForceRefresh(targetWidth: Float): Boolean {
        return gxNode.stretchNode.layoutByBind?.width != targetWidth
    }

}