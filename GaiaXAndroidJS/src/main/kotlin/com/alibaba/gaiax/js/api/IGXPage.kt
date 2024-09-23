package com.alibaba.gaiax.js.api

import android.content.Context
import android.view.View
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.support.script.GXScriptBuilder

interface IGXPage {

    /**
     * Java组件引用创建时调用
     */
    fun initPage()

    /**
     * 页面js加载完成时回调
     */
    fun onLoad(data: JSONObject)

    /**
     * 页面可见（回前台）时回调
     */
    fun onShow()

    /**
     * 列表首次渲染完成时回调
     * 整个生命周期内只会回调一次
     */
    fun onReady()

    /**
     * 页面不可见（压后台）时回调
     */
    fun onHide()

    /**
     * 页面销毁时回调
     */
    fun onUnload()

    /**
     * 页面滑动回调
     */
    fun onPageScroll(data: JSONObject)

    /**
     * 页面触底回调
     */
    fun onReachBottom()

    /**
     * 页面jsapi相关：https://aliyuque.antfin.com/gaia/document/vir8g0795gs31tz6
     */

    /**
     * 创建 Component
     * 通过传入的参数去判断是否能够找到对应的组件模板，如果找到分配组件实例 id，比将分配的组件实例 id 和组件模板版本号回传给 js
     */
    fun createComponentByData(data: JSONObject, promise: IGXPromise)

    /**
     * 添加 Component
     * 根据入参创建 Component 实例，并执行 Component 的生命周期
     */
    fun addComponentByData(data: JSONObject, promise: IGXPromise)

    /**
     * 更新 Component
     * 根据入参 更新 Component 实例,重新走一遍 Component 的 databinding
     */
    fun updateComponentByData(data: JSONObject, promise: IGXPromise)

    /**
     * 移除 Component
     * 根据入参移除已存在的 Component 实例
     */
    fun removeComponentByData(data: JSONObject, promise: IGXPromise)

    /**
     * 批量创建 Component
     */
    fun batchCreateComponentByData(dataList: JSONArray, promise: IGXPromise)

    /**
     * 批量添加 Component
     */
    fun batchAddComponentByData(dataList: JSONArray, promise: IGXPromise)

    /**
     * 批量更新 Component
     */
    fun batchUpdateComponentByData(dataList: JSONArray, promise: IGXPromise)

    /**
     * 批量移除 Component
     */
    fun batchRemoveComponentByData(dataList: JSONArray, promise: IGXPromise)

    /**
     * 批量替换Component（移除+添加）
     */
    fun batchReplaceComponentByData(data: JSONArray, promise: IGXPromise)

    /**
     * 弹出一个悬浮view
     */
    fun showFloatingView(data: JSONObject, callback: IGXCallback)

    /**
     * 隐藏一个悬浮view
     */
    fun hideFloatingView(data: JSONObject, callback: IGXCallback)

    /**
     * 创建页面ActionBar
     */
    fun setupActionBar(data: JSONObject, callback: IGXCallback)

    /**
     * 设置页面背景颜色
     */
    fun setBackgroundColor(data: JSONObject, callback: IGXCallback)

    /**
     * 设置状态栏文字颜色深浅
     */
    fun setStatusBarBlack(data: JSONObject, callback: IGXCallback)

    /**
     * 创建全屏窗口，可在任意指定坐标加载GaiaX模板
     */
    fun presentModal(data: JSONObject, callback: IGXCallback)

    /**
     * 关闭全屏窗口
     */
    fun dismissModal(data: JSONObject, callback: IGXCallback)

    /**
     * 关闭页面
     */
    fun closePage(data: JSONObject, callback: IGXCallback)

    /**
     * 获取GaiaXPage上下文
     */
    fun getContext(): Context

    /**
     * 页面状态管理
     */
    interface IPageStateManager {
        fun setLoadingView(view: View)
        fun setErrorView(view: View)
        fun setRootView(view: View)
    }

    /**
     * 获取页面状态管理
     */
    fun getPageStateManager(): IPageStateManager

    /**
     * 由native发送的事件
     */
    fun onNativeEvent(data: JSONObject) {

    }
}
