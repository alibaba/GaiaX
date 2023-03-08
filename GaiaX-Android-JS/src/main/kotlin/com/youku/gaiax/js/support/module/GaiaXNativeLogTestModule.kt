package com.youku.gaiax.js.support.module

import android.widget.Toast
import com.alibaba.fastjson.JSONObject
import com.youku.gaiax.js.api.GaiaXBaseModule
import com.youku.gaiax.js.api.IGaiaXCallback
import com.youku.gaiax.js.api.annotation.GaiaXAsyncMethod
import com.youku.gaiax.js.utils.Log

/**
 *  @author: shisan.lms
 *  @date: 2022-11-17
 *  Description:
 */
class GaiaXNativeLogTestModule : GaiaXBaseModule(){
    @GaiaXAsyncMethod
    fun showToast(data: JSONObject, callback: IGaiaXCallback) {
        if (Log.isLog()) {
            Log.d("showToast() called with: data = $data, callback = $callback")
        }
        try {
            val title = data.getString("title") ?: ""
            val duration = data.getInteger("duration") ?: 3
            val durationType = if (duration >= 3) {
                Toast.LENGTH_LONG
            } else {
                Toast.LENGTH_SHORT
            }
            android.util.Log.d("lms-13",title)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override val name: String
        get() = "VIP"
}