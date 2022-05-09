package com.alibaba.gaiax.render.view.container.banner

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode

/**
 * @author guaiyu
 * @date 2022/4/14 19:04
 */
class GXBannerViewAdapter(val gxTemplateContext: GXTemplateContext, val gxNode: GXNode) :
    PagerAdapter() {

    private var data = JSONArray()

    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val templateItem = getTemplateItem()
            ?: throw IllegalArgumentException("GXTemplateItem not exist, gxNode = $gxNode")
        val itemData = data.getJSONObject(position) ?: JSONObject()
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
        this.data = data
        notifyDataSetChanged()
    }

    private fun getTemplateItem(): GXTemplateEngine.GXTemplateItem? {
        return gxNode.childTemplateItems?.firstOrNull()?.first
    }
}
