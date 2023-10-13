package com.alibaba.gaiax.js.adapter.modules

import android.app.Dialog
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.IGXCallback
import com.alibaba.gaiax.js.api.annotation.GXAsyncMethod
import com.alibaba.gaiax.js.utils.GXJSUiExecutor
import com.alibaba.gaiax.js.utils.Log


/**
 *  @author: shisan.lms
 *  @date: 2023-03-27
 *  Description:
 */
class GaiaXJSBuildInCustomDialogModule : GXJSBaseModule() {
    override val name: String
        get() = "BuildIn"

    var mDialogList: Map<String, Dialog>? = HashMap()

    @GXAsyncMethod
    fun showCustomDialog(data: JSONObject, dismissCallback: IGXCallback) {
        if (Log.isLog()) {
            Log.d("showCustomDialog() called with data" + data.toString())
        }
        try {
            val identifierId: String = data.getString("identifierId")
            GXJSUiExecutor.action(Runnable {
//                val topActivity: Activity? = GaiaXJSManager.instance.renderEngineDelegate?.getActivityForDialog()
//                if (topActivity != null) {
////                    val dialog: Dialog = GaiaXCustomDialogView(topActivity, data, null, null)
////                    mDialogList.put(identifierId, dialog)
////                    dialog.setOnDismissListener(object : DialogInterface.OnDismissListener {
////                        override fun onDismiss(dialog: DialogInterface?) {
////                            dismissCallback.invoke(null)
////                            mDialogList.remove(identifierId)
////                        }
////                    })
////                    dialog.show()
//                }
            }
            )
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    @GXAsyncMethod
    fun dismissCustomDialog(identifierId: String?, dismissCallback: IGXCallback) {
        val dialog: Dialog? = mDialogList?.get(identifierId)
        if (dialog != null) {
            dialog.dismiss()
//            mDialogList.remove(identifierId)
            dismissCallback.invoke(null)
        }
    }
}