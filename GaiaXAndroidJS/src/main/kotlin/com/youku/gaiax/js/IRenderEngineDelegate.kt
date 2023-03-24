package com.youku.gaiax.js

import android.app.Activity
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.youku.gaiax.js.api.IGaiaXCallback

/**
 *  @author: shisan.lms
 *  @date: 2023-03-06
 *  Description:
 */
interface IRenderEngineDelegate {

    fun setDataToGX(componentId: Long, templateId: String, data: JSONObject, callback: IGaiaXCallback)

    fun getDataFromGX(componentId: Long): JSONObject?

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