package com.alibaba.gaiax.demo

import android.app.Application
import com.alibaba.gaiax.demo.list.util.ClickTrace

class GaiaXDemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ClickTrace.install()
    }
}