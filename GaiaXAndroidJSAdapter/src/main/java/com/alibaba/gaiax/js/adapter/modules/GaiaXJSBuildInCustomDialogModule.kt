package com.alibaba.gaiax.js.adapter.modules

import android.app.Dialog
import com.alibaba.fastjson.JSONObject
import com.youku.gaiax.js.api.GaiaXJSBaseModule
import com.youku.gaiax.js.api.IGaiaXCallback
import com.youku.gaiax.js.api.annotation.GaiaXAsyncMethod
import com.youku.gaiax.js.utils.GaiaXJSUiExecutor
import com.youku.gaiax.js.utils.Log


/**
 *  @author: shisan.lms
 *  @date: 2023-03-27
 *  Description:
 */
class GaiaXJSBuildInCustomDialogModule : GaiaXJSBaseModule() {
    override val name: String
        get() = "BuildIn"

    var mDialogList: Map<String, Dialog>? = HashMap()

    @GaiaXAsyncMethod
    fun showCustomDialog(data: JSONObject, dismissCallback: IGaiaXCallback) {
        if (Log.isLog()) {
            Log.d("showCustomDialog() called with data" + data.toString())
        }
        try {
            val identifierId: String = data.getString("identifierId")
            GaiaXJSUiExecutor.action(Runnable {
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

    @GaiaXAsyncMethod
    fun dismissCustomDialog(identifierId: String?, dismissCallback: IGaiaXCallback) {
        val dialog: Dialog? = mDialogList?.get(identifierId)
        if (dialog != null) {
            dialog.dismiss()
//            mDialogList.remove(identifierId)
            dismissCallback.invoke(null)
        }
    }
}