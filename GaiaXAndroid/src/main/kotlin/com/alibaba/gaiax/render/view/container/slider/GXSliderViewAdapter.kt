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
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXSliderConfig

/**
 * @suppress
 */
class GXSliderViewAdapter(
    val gxTemplateContext: GXTemplateContext,
    val gxNode: GXNode
) : PagerAdapter() {

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

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val realPosition = if (data.size > 0) {
            position % data.size
        } else {
            position
        }

        val templateItem = getTemplateItem()
            ?: throw IllegalArgumentException("GXTemplateItem not exist, gxNode = $gxNode")

        val itemData = data.getJSONObject(realPosition) ?: JSONObject()

        val nodeLayout = gxNode.stretchNode.layoutByBind
            ?: gxNode.stretchNode.layoutByCreate

        val itemView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(
                nodeLayout?.width,
                nodeLayout?.height
            )
        )
        if (itemView != null) {
            GXTemplateEngine.instance.bindData(
                itemView,
                GXTemplateEngine.GXTemplateData(itemData).apply {
                    this.eventListener = object : GXTemplateEngine.GXIEventListener {
                        override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                            super.onGestureEvent(gxGesture)
                            gxGesture.index = realPosition
                            gxTemplateContext.templateData?.eventListener?.onGestureEvent(gxGesture)
                        }

                        override fun onScrollEvent(gxScroll: GXTemplateEngine.GXScroll) {
                            super.onScrollEvent(gxScroll)
                            gxTemplateContext.templateData?.eventListener?.onScrollEvent(gxScroll)
                        }

                        override fun onAnimationEvent(gxAnimation: GXTemplateEngine.GXAnimation) {
                            super.onAnimationEvent(gxAnimation)
                            gxTemplateContext.templateData?.eventListener?.onAnimationEvent(
                                gxAnimation
                            )
                        }
                    }

                    this.trackListener = object : GXTemplateEngine.GXITrackListener {
                        override fun onTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                            gxTrack.index = realPosition
                            gxTemplateContext.templateData?.trackListener?.onTrackEvent(gxTrack)
                        }

                        override fun onManualClickTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                            gxTrack.index = realPosition
                            gxTemplateContext.templateData?.trackListener?.onManualClickTrackEvent(
                                gxTrack
                            )
                        }

                        override fun onManualExposureTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                            gxTrack.index = realPosition
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
                })
            container.addView(itemView)
        }
        return itemView ?: throw IllegalArgumentException("Create Item View error")
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {

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
}
