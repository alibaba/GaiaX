package com.alibaba.gaiax.js.module

import android.app.AlertDialog
import android.widget.Toast
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.IGXCallback
import com.alibaba.gaiax.js.api.annotation.GaiaXAsyncMethod
import com.alibaba.gaiax.js.utils.GXJSUiExecutor
import com.alibaba.gaiax.js.utils.Log


/**
 *  @author: shisan.lms
 *  @date: 2023-03-24
 *  Description:
 */
class GXJSBuildInTipsModule : GXJSBaseModule() {
    override val name: String
        get() = "BuildIn"

    @GaiaXAsyncMethod
    fun showToast(data: JSONObject, callback: IGXCallback) {
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
            GXJSUiExecutor.action {
                GXJSEngine.instance.context.let { appContext ->
                    Toast.makeText(appContext, title, durationType).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @GaiaXAsyncMethod
    fun showAlert(data: JSONObject, callback: IGXCallback) {
        if (Log.isLog()) {
            Log.d("showAlert() called with: data = $data, callback = $callback")
        }
        try {
            val title = data.getString("title") ?: ""
            val message = data.getString("message") ?: ""
            GXJSUiExecutor.action {
                GXJSEngine.Proxy.instance.renderDelegate.getActivityForDialog().let { context ->
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