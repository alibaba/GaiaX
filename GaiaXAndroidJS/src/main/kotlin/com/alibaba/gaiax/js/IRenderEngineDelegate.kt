package com.alibaba.gaiax.js

import android.app.Activity
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.api.IGXCallback

interface IRenderEngineDelegate {

    fun bindComponentToView(view: View, jsComponentId: Long)

    fun unbindComponentAndView(jsComponentId: Long)

    fun setDataToRenderEngine(
        componentId: Long, templateId: String, data: JSONObject, callback: IGXCallback
    )

    fun getDataFromRenderEngine(componentId: Long): JSONObject?

    fun getNodeInfo(targetId: String, templateId: String, instanceId: Long): JSONObject

    fun addEventListener(
        targetId: String,
        componentId: Long,
        eventType: String,
        optionCover: Boolean,
        optionLevel: Int
    )

    fun dispatcherEvent(eventParams: JSONObject)

    fun getView(componentId: Long): View?

    fun getActivityForDialog(): Activity?

}