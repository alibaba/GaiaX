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
    val gxTemplateContext: GXTemplateContext,
    val gxNode: GXNode
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

    private fun getMeasureSize(childItemViewPort: Size<Float?>) =
        GXTemplateEngine.GXMeasureSize(
            childItemViewPort.width, childItemViewPort.height
        )

    private fun getChildContainerSize(): Layout? =
        GXNodeUtils.computeItemContainerSize(
            gxTemplateContext,
            gxNode,
            data.firstOrNull() as? JSONObject ?: JSONObject()
        )

    private fun getChildItemContainerSize(childContainerSize: Layout?): ViewPager.LayoutParams {
        val itemContainerWidth = childContainerSize?.width?.toInt()
            ?: FrameLayout.LayoutParams.WRAP_CONTENT

        val itemContainerHeight =
            childContainerSize?.height?.toInt() ?: FrameLayout.LayoutParams.WRAP_CONTENT

        return ViewPager.LayoutParams().apply {
            this.width = itemContainerWidth
            this.height = itemContainerHeight
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val childItemPosition = if (data.size > 0) {
            position % data.size
        } else {
            position
        }

        val childTemplateItem = getTemplateItem()
            ?: throw IllegalArgumentException("GXTemplateItem not exist, gxNode = $gxNode")

        val childItemData = data.getJSONObject(childItemPosition) ?: JSONObject()

        val childVisualNestTemplateNode = getVisualNestTemplateNode(childTemplateItem)

        val childItemViewPort = GXNodeUtils.computeItemViewPort(gxTemplateContext, gxNode)

        val childItemMeasureSize = getMeasureSize(childItemViewPort)

        val childItemContainerSize = getChildContainerSize()

        val childItemContainerLayoutParams = getChildItemContainerSize(childItemContainerSize)

        val childItemContainer = GXItemContainer(container.context)

        childItemContainer.layoutParams = childItemContainerLayoutParams

        val processContainerItemBind = GXRegisterCenter.instance.extensionContainerItemBind
        if (processContainerItemBind != null) {
            processContainerItemBind.bindViewHolder(
                gxTemplateContext.templateData?.tag,
                childItemContainer,
                childItemMeasureSize,
                childTemplateItem,
                childItemPosition,
                childVisualNestTemplateNode,
                childItemData
            )
        } else {

            val childView = if (childItemContainer.childCount != 0) {
                childItemContainer.getChildAt(0)
            } else {
                val childView = GXTemplateEngine.instance.createView(
                    childTemplateItem,
                    childItemMeasureSize,
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
                        gxGesture.index = childItemPosition
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
                        gxTrack.index = childItemPosition
                        gxTemplateContext.templateData?.trackListener?.onTrackEvent(gxTrack)
                    }

                    override fun onManualClickTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                        gxTrack.index = childItemPosition
                        gxTemplateContext.templateData?.trackListener?.onManualClickTrackEvent(
                            gxTrack
                        )
                    }

                    override fun onManualExposureTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                        gxTrack.index = childItemPosition
                        gxTemplateContext.templateData?.trackListener?.onManualExposureTrackEvent(
                            gxTrack
                        )
                    }
                }

                this.dataListener = object : GXTemplateEngine.GXIDataListener {
                    override fun onTextProcess(gxTextData: GXTemplateEngine.GXTextData): CharSequence? {
                        return gxTemplateContext.templateData?.dataListener
                            ?.onTextProcess(gxTextData)
                    }
                }
            }
            if (childView != null) {
                GXTemplateEngine.instance.bindData(
                    childView,
                    childTemplateData,
                    childItemMeasureSize
                )

                // FIX: 重置容器的宽度，防止预计算和实际的宽度不相符
                childItemContainer.layoutParams.width = childView.layoutParams.width
            }
        }

        container.addView(childItemContainer)

        itemViewMap[getItemViewKey(position)] = childItemContainer

        return childItemContainer
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
