package com.alibaba.gaiax.render.view.basic

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.Keep
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.utils.Log

@Keep
open class GXItemContainer : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    fun finalize() {
        if (Log.isLog()) {
            Log.e("finalize")
        }
        // 在ItemView被GC回收时，要通知视图被销毁，可以用于解除JS组件
        getChildAt(0)?.let { gxView ->
            GXTemplateContext.getContext(gxView)?.let {
                if (it.templateItem.isPageMode) {
                    GXRegisterCenter.instance.gxPageItemViewLifecycleListener?.onDestroy(gxView)
                } else {
                    GXRegisterCenter.instance.gxItemViewLifecycleListener?.onDestroy(gxView)
                }
            }
        }
    }
}
