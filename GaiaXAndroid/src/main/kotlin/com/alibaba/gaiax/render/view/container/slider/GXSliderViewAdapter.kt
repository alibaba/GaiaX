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
) :
    PagerAdapter() {

    private var mConfig: GXSliderConfig? = null
    private var mData = JSONArray()

    override fun getCount(): Int {
        return if (mConfig?.infinityScroll == false) {
            mData.size
        } else {
            Int.MAX_VALUE
        }
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val realPosition = if (mData.size > 0) {
            position % mData.size
        } else {
            position
        }

        val templateItem = getTemplateItem()
            ?: throw IllegalArgumentException("GXTemplateItem not exist, gxNode = $gxNode")
        val itemData = mData.getJSONObject(realPosition) ?: JSONObject()
        val itemView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(
                gxNode.stretchNode.layout?.width,
                gxNode.stretchNode.layout?.height
            )
        )
        GXTemplateEngine.instance.bindData(itemView, GXTemplateEngine.GXTemplateData(itemData))
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {

    }

    fun setData(data: JSONArray) {
        this.mData = data
        notifyDataSetChanged()
    }

    fun setConfig(config: GXSliderConfig?) {
        mConfig = config
    }

    private fun getTemplateItem(): GXTemplateEngine.GXTemplateItem? {
        return gxNode.childTemplateItems?.firstOrNull()?.first
    }
}
