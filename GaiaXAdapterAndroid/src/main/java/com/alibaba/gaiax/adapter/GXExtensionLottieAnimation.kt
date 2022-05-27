package com.alibaba.gaiax.adapter

import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.template.animation.GXLottieAnimation

class GXExtensionLottieAnimation : GXRegisterCenter.GXIExtensionLottieAnimation {
    override fun create(): GXLottieAnimation {
        return com.alibaba.gaiax.adapter.GXAdapterLottieAnimation()
    }
}