package com.alibaba.gaiax.utils

import android.content.Context
import android.content.res.Configuration

object GXDarkUtils {

    fun isDarkMode(context: Context?): Boolean {
        context?.let {
            val darkModeFlag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
        }
        return false
    }
}