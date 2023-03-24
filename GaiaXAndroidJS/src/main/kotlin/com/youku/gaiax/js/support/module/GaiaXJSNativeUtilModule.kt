package com.youku.gaiax.js.support.module

import android.util.Base64
import androidx.annotation.Keep
import com.youku.gaiax.js.api.GaiaXJSBaseModule
import com.youku.gaiax.js.api.annotation.GaiaXSyncMethod
import com.youku.gaiax.js.utils.Log
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset

@Keep
class GaiaXJSNativeUtilModule : GaiaXJSBaseModule() {

    @GaiaXSyncMethod
    fun base64Decode(content: String): String {
        val result =
            String(Base64.decode(content.toByteArray(), Base64.DEFAULT), Charset.forName("utf-8"))
        if (Log.isLog()) {
            Log.d("base64Decode() called with: content = $content, result = $result")
        }
        return result
    }

    @GaiaXSyncMethod
    fun base64Encode(content: String): String {
        val result = Base64.encodeToString(content.toByteArray(), Base64.DEFAULT)
        if (Log.isLog()) {
            Log.d("base64Encode() called with: content = $content, result = $result")
        }
        return result
    }

    @GaiaXSyncMethod
    fun urlDecode(content: String): String {
        val result = URLDecoder.decode(content, "utf-8")
        if (Log.isLog()) {
            Log.d("urlDecode() called with: content = $content, result = $result")
        }
        return result
    }

    @GaiaXSyncMethod
    fun urlEncode(content: String): String {
        val result = URLEncoder.encode(content, "utf-8")
        if (Log.isLog()) {
            Log.d("urlEncode() called with: content = $content, result = $result")
        }
        return result
    }

    @GaiaXSyncMethod
    fun md5(content: String): String {
        // TODO: 这块依赖orange的md5先移除
//        val result = MD5Util.md5(content)
        val result = "TODO: 这块依赖orange的md5先移除 "
        if (Log.isLog()) {
            Log.d("md5() called with: content = $content, result = $result")
        }
        return result
    }

//    @GaiaXAsyncMethod
//    fun showToast(data: JSONObject, callback: IGaiaXCallback) {
//        if (Log.isLog()) {
//            Log.d("showToast() called with: data = $data, callback = $callback")
//        }
//        try {
//            val title = data.getString("title") ?: ""
//            val duration = data.getInteger("duration") ?: 3
//            val durationType = if (duration >= 3) {
//                Toast.LENGTH_LONG
//            } else {
//                Toast.LENGTH_SHORT
//            }
//            android.util.Log.d("lms-13",title)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    override val name: String
        get() = "NativeUtil"

}