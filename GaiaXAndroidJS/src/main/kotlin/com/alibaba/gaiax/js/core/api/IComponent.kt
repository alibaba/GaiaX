package com.alibaba.gaiax.js.core.api

import com.alibaba.fastjson.JSONObject

interface IComponent {


    /**
     * 整个生命周期内只会回调一次
     */
    fun onReady()

    /**
     * 组价复用时回调
     */
    fun onReuse()

    /**
     * 每次当组件显示时调用一次
     */
    fun onShow()

    /**
     * 每次当组件消失时调用一次
     */
    fun onHide()

    /**
     * 组件要销毁时调用
     */
    fun onDestroy()

    /**
     * 传递事件
     */
    fun onEvent(type: String, data: JSONObject)

    /**
     * 传递Native事件
     */
    fun onNativeEvent(data: JSONObject)

    /**
     * 传递LoadMore消息，需要在参数中添加才会生效
     */
    fun onLoadMore(data: JSONObject)
}
