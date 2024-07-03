/*
 * Copyright (c) 2021, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.gaiax.render.view.container

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.GXIRelease
import com.alibaba.gaiax.utils.Log

/**
 * @suppress
 */
@Keep
open class GXGridView : GXContainer, GXIRelease {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (Log.isLog()) {
            Log.e("onAttachedToWindow GXGridView $this")
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (Log.isLog()) {
            Log.e("onDetachedFromWindow GXGridView $this")
        }
    }

    override fun release() {
        if (Log.isLog()) {
            Log.e("release GXGridView $this")
        }
        if (this.layoutManager is LinearLayoutManager) {
            val layoutManager = this.layoutManager as LinearLayoutManager
            for (i in 0..layoutManager.itemCount) {
                ((this.findViewHolderForLayoutPosition(i) as? GXViewHolder)?.itemView as? ViewGroup)?.getChildAt(0)?.let { gxView ->
                    GXTemplateContext.getContext(gxView)?.let { gxTemplateContext ->
                        if (Log.isLog()) {
                            Log.e(gxTemplateContext.tag, "traceId=${gxTemplateContext.traceId} tag=GXGridView.release GXScrollView=$this gxView=$gxView")
                        }

                        if (gxTemplateContext.templateItem.isPageMode) {
                            GXRegisterCenter.instance.gxPageItemViewLifecycleListener?.onDestroy(gxView)
                        } else {
                            GXRegisterCenter.instance.gxItemViewLifecycleListener?.onDestroy(gxView)
                        }

                        GXTemplateEngine.instance.destroyView(gxView)
                    }
                }
            }
        }
    }
}