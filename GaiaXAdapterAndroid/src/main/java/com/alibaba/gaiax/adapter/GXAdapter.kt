package com.alibaba.gaiax.adapter

import android.content.Context
import androidx.annotation.Keep
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.render.view.GXViewKey

@Keep
class GXAdapter : GXTemplateEngine.GXIAdapter {

    override fun init(context: Context) {
        GXRegisterCenter.instance
            .registerExtensionLottieAnimation(GXExtensionLottieAnimation())
            .registerExtensionExpression(GXExtensionExpression())
            .registerExtensionViewSupport(
                GXViewKey.VIEW_TYPE_IMAGE,
                GXAdapterImageView::class.java
            )
    }
}