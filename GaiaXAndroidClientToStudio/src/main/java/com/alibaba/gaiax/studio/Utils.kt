package com.alibaba.gaiax.studio

import android.content.Context

const val GX_SP_NAME = "gx_sp_file"
// 连接GaiaStudio的url
const val GX_CONNECT_URL = "gx_connect_url"


fun saveInLocal(context: Context, key: String, value: String) {
    val editor = context.getSharedPreferences(GX_SP_NAME, Context.MODE_PRIVATE).edit()
    editor.putString(key, value)
    editor.apply()
}


fun loadInLocal(context: Context, key: String): String? {
    val getter = context.getSharedPreferences(GX_SP_NAME, Context.MODE_PRIVATE)
    return getter.getString(key, "")
}