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

package com.alibaba.gaiax.render.view.container.slider

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import app.visly.stretch.Layout
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.render.node.GXNodeUtils
import com.alibaba.gaiax.render.view.basic.GXItemContainer
import com.alibaba.gaiax.template.GXSliderConfig

/**
 * @suppress
 */
class GXSliderViewAdapter(
    val gxTemplateContext: GXTemplateContext, val gxNode: GXNode
) : PagerAdapter() {

    private val itemViewMap: MutableMap<String, View?> = mutableMapOf()

    private var config: GXSliderConfig? = null
    private var data = JSONArray()

    override fun getCount(): Int {
        return if (config?.infinityScroll == false) {
            data.size
        } else {
            Int.MAX_VALUE
        }
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    private fun getItemContainerSize(containerSize: Layout?): ViewPager.LayoutParams {
        val itemContainerWidth =
            containerSize?.width?.toInt() ?: FrameLayout.LayoutParams.WRAP_CONTENT

        val itemContainerHeight =
            containerSize?.height?.toInt() ?: FrameLayout.LayoutParams.WRAP_CONTENT

        return ViewPager.LayoutParams().apply {
            this.width = itemContainerWidth
            this.height = itemContainerHeight
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemPosition = if (data.size > 0) {
            position % data.size
        } else {
            position
        }

        val templateItem = getTemplateItem()
            ?: throw IllegalArgumentException("GXTemplateItem not exist, gxNode = $gxNode")

        val itemData = data.getJSONObject(itemPosition) ?: JSONObject()

        val visualNestTemplateNode = gxNode.childTemplateItems?.firstOrNull()?.second

        val itemViewPort = GXNodeUtils.computeSliderItemViewPort(gxTemplateContext, gxNode)

        val itemMeasureSize = GXTemplateEngine.GXMeasureSize(
            itemViewPort.width, itemViewPort.height
        )

        val itemContainerSize = GXNodeUtils.computeSliderItemContainerSize(
            gxTemplateContext, gxNode, itemViewPort, itemData, itemPosition
        )

        val itemContainerLayoutParams = getItemContainerSize(itemContainerSize)

        val itemContainer = GXItemContainer(container.context)

        itemContainer.layoutParams = itemContainerLayoutParams

        val processContainerItemBind = GXRegisterCenter.instance.extensionContainerItemBind
        if (processContainerItemBind != null) {
            processContainerItemBind.bindViewHolder(
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

                val templateContext = GXTemplateEngine.instance.createViewOnlyNodeTree(templateItem,
                    itemMeasureSize,
                    GXTemplateEngine.GXExtendParams().apply {
                        this.gxItemPosition = itemPosition
                        this.gxItemData = itemData
                        this.gxHostTemplateContext = gxTemplateContext
                        this.gxVisualTemplateNode = visualNestTemplateNode
                    })
                    ?: throw IllegalArgumentException("Create GXTemplateContext fail, please check")

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

        container.addView(itemContainer)

        itemViewMap[getItemViewKey(position)] = itemContainer

        return itemContainer
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        if (obj is View) {
            container.removeView(obj)
        }
        itemViewMap.remove(getItemViewKey(position))
    }

    fun setData(data: JSONArray) {
        this.data = data
        notifyDataSetChanged()
    }

    fun setConfig(config: GXSliderConfig?) {
        this.config = config
    }

    private fun getTemplateItem(): GXTemplateEngine.GXTemplateItem? {
        return gxNode.childTemplateItems?.firstOrNull()?.first
    }

    private fun getItemViewKey(position: Int): String {
        return "item_$position"
    }

    fun getItemView(position: Int): View? {
        return itemViewMap[getItemViewKey(position)]
    }
}
