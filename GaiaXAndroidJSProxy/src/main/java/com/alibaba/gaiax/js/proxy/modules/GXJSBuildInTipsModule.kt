package com.alibaba.gaiax.js.proxy.modules

import android.app.AlertDialog
import android.widget.Toast
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.proxy.GXJSRenderProxy
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.IGXCallback
import com.alibaba.gaiax.js.api.annotation.GXAsyncMethod
import com.alibaba.gaiax.js.proxy.Log
import com.alibaba.gaiax.js.proxy.runE
import com.alibaba.gaiax.js.utils.GXJSUiExecutor


/**
 *  @author: shisan.lms
 *  @date: 2023-03-24
 *  Description:
 */
class GXJSBuildInTipsModule : GXJSBaseModule() {
    override val name: String
        get() = "BuildIn"

    @GXAsyncMethod
    fun showToast(data: JSONObject, callback: IGXCallback) {
        Log.runE { "showToast() called with: data = $data, callback = $callback" }
        try {
            val title = data.getString("title") ?: ""
            val duration = data.getInteger("duration") ?: 3
            val durationType = if (duration >= 3) {
                Toast.LENGTH_LONG
            } else {
                Toast.LENGTH_SHORT
            }
            GXJSUiExecutor.action {
                GXJSEngine.instance.context.let { appContext ->
                    Toast.makeText(appContext, title, durationType).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @GXAsyncMethod
    fun showAlert(data: JSONObject, callback: IGXCallback) {
        Log.runE { "showAlert() called with: data = $data, callback = $callback" }
        try {
            val title = data.getString("title") ?: ""
            val message = data.getString("message") ?: ""
            GXJSUiExecutor.action {
                GXJSRenderProxy.instance.getActivity()?.let { context ->
                    AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确认") { dialog, _ ->
                            callback.invoke(JSONObject().apply
                            {
                                this["canceled"] = true
                            })
                            dialog.dismiss()
                        }.setNegativeButton("取消") { dialog, _ ->
                            callback.invoke(JSONObject().apply {
                                this["canceled"] = false
                            })
                            dialog?.dismiss()
                        }
                        .create()
                        .show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}