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
import app.visly.stretch.Size
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.render.node.GXNodeUtils
import com.alibaba.gaiax.render.node.GXTemplateNode
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

    private fun getVisualNestTemplateNode(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplateNode? {
        gxNode.childTemplateItems?.forEach {
            if (it.first.templateId == gxTemplateItem.templateId) {
                return it.second
            }
        }
        return null
    }

    private fun getMeasureSize(itemViewPort: Size<Float?>) = GXTemplateEngine.GXMeasureSize(
        itemViewPort.width, itemViewPort.height
    )

    private fun getContainerSize(itemPosition: Int, itemData: JSONObject): Layout? =
        GXNodeUtils.computeItemContainerSize(
            gxTemplateContext, gxNode, itemData, itemPosition
        )

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

        val gxTemplateItem = getTemplateItem()
            ?: throw IllegalArgumentException("GXTemplateItem not exist, gxNode = $gxNode")

        val itemData = data.getJSONObject(itemPosition) ?: JSONObject()

        val visualNestTemplateNode = getVisualNestTemplateNode(gxTemplateItem)

        val itemViewPort = GXNodeUtils.computeItemViewPort(gxTemplateContext, gxNode)

        val itemMeasureSize = getMeasureSize(itemViewPort)

        val itemContainerSize = getContainerSize(itemPosition, itemData)

        val itemContainerLayoutParams = getItemContainerSize(itemContainerSize)

        val itemContainer = GXItemContainer(container.context)

        itemContainer.layoutParams = itemContainerLayoutParams

        val processContainerItemBind = GXRegisterCenter.instance.extensionContainerItemBind
        if (processContainerItemBind != null) {
            processContainerItemBind.bindViewHolder(
                gxTemplateContext.templateData?.tag,
                itemContainer,
                itemMeasureSize,
                gxTemplateItem,
                itemPosition,
                visualNestTemplateNode,
                itemData
            )
        } else {

            val childView = if (itemContainer.childCount != 0) {
                itemContainer.getChildAt(0)
            } else {
                val childView = GXTemplateEngine.instance.createView(
                    gxTemplateItem, itemMeasureSize, visualNestTemplateNode
                )
                itemContainer.addView(childView)
                childView
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
            if (childView != null) {
                GXTemplateEngine.instance.bindData(
                    childView, gxTemplateData, itemMeasureSize
                )

                // FIX: 重置容器的宽度，防止预计算和实际的宽度不相符
                itemContainer.layoutParams.width = childView.layoutParams.width
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
