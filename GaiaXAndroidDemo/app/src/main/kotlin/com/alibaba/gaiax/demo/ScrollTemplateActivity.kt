package com.alibaba.gaiax.demo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.render.utils.GXGravitySmoothScroller
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.render.view.container.GXContainerViewAdapter
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.utils.GXScreenUtils
import com.alibaba.gaiax.utils.getStringExtCanNull


class ScrollTemplateActivity : AppCompatActivity() {


    /**
     * 有些偏业务的对比逻辑，交给业务处理不够友好，在这里特殊处理一下
     */
    open class GaiaXDefaultDiffCallBack(
        protected val oldDatas: JSONArray,
        protected val newDatas: JSONArray
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldDatas.size
        }

        override fun getNewListSize(): Int {
            return newDatas.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            if (oldItemPosition == newItemPosition) {
                val oldData = oldDatas.getJSONObject(oldItemPosition)
                val newData = newDatas.getJSONObject(newItemPosition)
                if (oldData != null && newData != null) {

                    // 若是普通的柏拉图坑位结构，尝试判断唯一标识
                    var oldId = oldData.getString("id")
                    var newId = newData.getString("id")
                    if (oldId != null && newId != null && oldId == newId) {
                        return true
                    }

                    oldId = oldData.getString("contentId")
                    newId = newData.getString("contentId")
                    if (oldId != null && newId != null && oldId == newId) {
                        return true
                    }

                    // 若是自定义数据结构，判断标题
                    var oldTitle = oldData.getString("title")
                    var newTitle = newData.getString("title")
                    if (oldTitle != null && newTitle != null && oldTitle == newTitle) {
                        return true
                    }

                    // 若是普通的柏拉图坑位结构，尝试判断标题
                    oldTitle = oldData.getStringExtCanNull("data.title")
                    newTitle = newData.getStringExtCanNull("data.title")
                    if (oldTitle != null && newTitle != null && oldTitle == newTitle) {
                        return true
                    }

                    // 若是指定的的柏拉图坑位结构，尝试判断标题
                    oldTitle = oldData.getStringExtCanNull("text.title")
                    newTitle = newData.getStringExtCanNull("text.title")
                    if (oldTitle != null && newTitle != null && oldTitle == newTitle) {
                        return true
                    }

                    // 增加一个对于位置的判定，如果位置相同，认为是同一个item
                    // 在优酷中，坑位不会变化、移动，所以同一位置的position相同，可以认为是同一个item
                    val old_gx_position = oldData.getString("gaiax_scroll_position")
                    val new_gx_position = newData.getString("gaiax_scroll_position")
                    if (old_gx_position != null && new_gx_position != null && old_gx_position == new_gx_position) {
                        return true
                    }
                }
            }
            return false
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // 在相同坑位的状态下，认为数据都不相同，需要进行坑位级别的刷新
            return false
        }

    }

    class GXExtensionContainerDataUpdate : GXRegisterCenter.GXIExtensionContainerDataUpdate {
        override fun update(
            gxTemplateContext: GXTemplateContext,
            gxContainerViewAdapter: GXContainerViewAdapter,
            old: JSONArray,
            new: JSONArray
        ) {
            val diffCallBack = GaiaXDefaultDiffCallBack(old, new)
            val diffResult = DiffUtil.calculateDiff(diffCallBack, true)
            diffResult.dispatchUpdatesTo(gxContainerViewAdapter)
        }
    }

    companion object {
        private const val TAG = "[GaiaX][Demo]"
    }

    class GXExtensionScroll : GXRegisterCenter.GXIExtensionScroll {

        override fun scrollIndex(
            gxTemplateContext: GXTemplateContext,
            container: GXContainer,
            extend: JSONObject?
        ) {
            Log.d(TAG, "scrollIndex() called with: extend = $extend")

            val recyclerView = container as RecyclerView

            // holding offset
            val holdingOffset = extend?.getBooleanValue(GXTemplateKey.GAIAX_DATABINDING_HOLDING_OFFSET) ?: false
            if (holdingOffset) {
                val scrollIndex = extend?.getInteger(GXTemplateKey.GAIAX_SCROLL_INDEX) ?: -1
                if (scrollIndex != -1) {
                    val scrollGravity = extend?.getString(GXTemplateKey.GAIAX_SCROLL_POSITION)
                    if (scrollGravity != null) {
                        // 默认是平滑滚动的
                        val scroller = when (scrollGravity) {
                            "left" -> GXGravitySmoothScroller(recyclerView.context, GXGravitySmoothScroller.ALIGN_LEFT)
                            "right" -> GXGravitySmoothScroller(recyclerView.context, GXGravitySmoothScroller.ALIGN_RIGHT)
                            "center" -> GXGravitySmoothScroller(recyclerView.context, GXGravitySmoothScroller.ALIGN_CENTER)
                            else -> GXGravitySmoothScroller(recyclerView.context, GXGravitySmoothScroller.ALIGN_ANY)
                        }
                        scroller.targetPosition = scrollIndex
                        recyclerView.layoutManager?.startSmoothScroll(scroller)
                    } else {
                        val smooth = extend?.getBooleanValue(GXTemplateKey.GAIAX_SCROLL_ANIMATED) ?: false
                        if (smooth) {
                            recyclerView.smoothScrollToPosition(scrollIndex)
                        } else {
                            recyclerView.scrollToPosition(scrollIndex)
                        }
                    }
                } else {
                    // no process
                }
                return
            }

            // scroll item to position
            gxTemplateContext.templateData?.scrollIndex?.let {
                if (it != -1) {
                    recyclerView.scrollToPosition(it)
                }
                return
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll_template)
        GXRegisterCenter.instance.registerExtensionItemViewLifecycleListener(object : GXRegisterCenter.GXIItemViewLifecycleListener {
            override fun onCreate(gxView: View?) {
                Log.d(TAG, "onCreate() called with: gxView = $gxView")
            }

            override fun onVisible(gxView: View?) {
                Log.d(TAG, "onVisible() called with: gxView = $gxView")
            }

            override fun onInvisible(gxView: View?) {
                Log.d(TAG, "onInvisible() called with: gxView = $gxView")
            }

            override fun onReuse(gxView: View?) {
                Log.d(TAG, "onReuse() called with: gxView = $gxView")
            }

            override fun onStart(gxView: View?, gxTemplateData: GXTemplateEngine.GXTemplateData) {

            }

            override fun onStarted(gxView: View?) {
            }

            override fun onDestroy(gxView: View?) {
                Log.d(TAG, "onDestroy() called with: gxView = $gxView")
            }

        })
        GXRegisterCenter.instance.registerExtensionScroll(GXExtensionScroll())
        renderTemplate1(this)
    }

    var templateView: View? = null

    private fun renderTemplate1(activity: ScrollTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)


        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "assets_data_source/templates", "gx-content-uper-scroll2")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val data = AssetsUtils.parseAssets(activity, "assets_data_source/data/scroll-uper.json")
        val templateData = GXTemplateEngine.GXTemplateData(data).apply {
            this.scrollIndex = 1
        }

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)!!

        templateView = view

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gxGesture.index?.let {
                    Log.d(TAG, "onGestureEvent() called with: gxGesture = ${gxGesture.index}")
                    if (it >= 0) {
                        val extend = templateData.data.getJSONObject("extend")
                        extend["scroll-index"] = it
                        extend["holding-offset"] = true
                        extend["scroll-position"] = "center"
                        GXTemplateEngine.instance.bindData(view, templateData)
                    }
                }
            }
        }

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)


        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)
        findViewById<AppCompatButton>(R.id.btn_scroll_index).setOnClickListener {

            findViewById<AppCompatEditText>(R.id.et_scroll_index).text.toString().toIntOrNull()?.let {
                val extend = templateData.data.getJSONObject("extend")
                extend["scroll-index"] = it
                GXTemplateEngine.instance.bindData(view, templateData)
            }
        }


    }

    override fun onDestroy() {
        GXTemplateEngine.instance.destroyView(templateView)
        super.onDestroy()
    }

}
