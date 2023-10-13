package com.alibaba.gaiax.js.module

import android.util.Base64
import androidx.annotation.Keep
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.annotation.GXSyncMethod
import com.alibaba.gaiax.js.utils.Log
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset

@Keep
class GXJSNativeUtilModule : GXJSBaseModule() {

    @GXSyncMethod
    fun base64Decode(content: String): String {
        val result =
            String(Base64.decode(content.toByteArray(), Base64.DEFAULT), Charset.forName("utf-8"))
        if (Log.isLog()) {
            Log.d("base64Decode() called with: content = $content, result = $result")
        }
        return result
    }

    @GXSyncMethod
    fun base64Encode(content: String): String {
        val result = Base64.encodeToString(content.toByteArray(), Base64.DEFAULT)
        if (Log.isLog()) {
            Log.d("base64Encode() called with: content = $content, result = $result")
        }
        return result
    }

    @GXSyncMethod
    fun urlDecode(content: String): String {
        val result = URLDecoder.decode(content, "utf-8")
        if (Log.isLog()) {
            Log.d("urlDecode() called with: content = $content, result = $result")
        }
        return result
    }

    @GXSyncMethod
    fun urlEncode(content: String): String {
        val result = URLEncoder.encode(content, "utf-8")
        if (Log.isLog()) {
            Log.d("urlEncode() called with: content = $content, result = $result")
        }
        return result
    }

    override val name: String
        get() = "NativeUtil"

}