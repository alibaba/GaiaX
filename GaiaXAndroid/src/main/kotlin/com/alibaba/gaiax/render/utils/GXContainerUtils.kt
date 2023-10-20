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


package com.alibaba.gaiax.render.utils

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.GXIContainer
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.render.view.container.GXViewHolder
import com.alibaba.gaiax.render.view.container.slider.GXSliderView
import com.alibaba.gaiax.render.view.container.slider.GXSliderViewAdapter

object GXContainerUtils {

    internal fun notifyOnResetView(gxTemplateContext: GXTemplateContext) {
        notifyView(gxTemplateContext, { container: View ->
            //
        }, { gxView: View ->
            GXTemplateEngine.instance.resetView(gxView)
        })
    }

    internal fun notifyContainerAndItemsOnAppear(gxTemplateContext: GXTemplateContext) {
        notifyView(gxTemplateContext, { container: View ->
            if (container is GXSliderView) {
                container.onVisibleChanged(true)
            }
        }, { gxView: View ->
            GXTemplateEngine.instance.onAppear(gxView)
        })
    }

    internal fun notifyContainerAndItemsOnDisappear(gxTemplateContext: GXTemplateContext) {
        notifyView(gxTemplateContext, { container: View ->
            if (container is GXSliderView) {
                container.onVisibleChanged(false)
            }
        }, { gxView: View ->
            GXTemplateEngine.instance.onDisappear(gxView)
        })
    }

    fun notifyView(
        gxTemplateContext: GXTemplateContext,
        containerCallback: ((container: View) -> Unit)? = null,
        visibleItemViewCallback: ((itemView: View) -> Unit)? = null,
    ) {
        gxTemplateContext.containers?.forEach { container ->
            if (container is View) {
                containerCallback?.invoke(container)
            }
            findVisibleItems(container) { view ->
                if (view is ViewGroup && view.childCount > 0) {
                    val gxView = view.getChildAt(0)
                    if (gxView != null) {
                        visibleItemViewCallback?.invoke(gxView)
                    }
                }
            }
        }
    }


    private fun findVisibleItems(container: GXIContainer, callBack: (holder: View) -> Unit) {
        if (container is GXContainer) {
            if (container.layoutManager is LinearLayoutManager) {
                val layoutManager = container.layoutManager as LinearLayoutManager
                val firstPos = layoutManager.findFirstVisibleItemPosition()
                val lastPos = layoutManager.findLastVisibleItemPosition() + 1
                for (index in firstPos..lastPos) {
                    (container.findViewHolderForLayoutPosition(index) as? GXViewHolder)?.let {
                        callBack(it.itemView)
                    }
                }
            } else if (container.layoutManager is GridLayoutManager) {
                val layoutManager = container.layoutManager as GridLayoutManager
                val firstPos = layoutManager.findFirstVisibleItemPosition()
                val lastPos = layoutManager.findLastVisibleItemPosition() + 1
                for (index in firstPos..lastPos) {
                    (container.findViewHolderForLayoutPosition(index) as? GXViewHolder)?.let {
                        callBack(it.itemView)
                    }
                }
            }
        } else if (container is GXSliderView) {
            container.viewPager?.let { viewPager ->
                val adapter = viewPager.adapter
                if (adapter is GXSliderViewAdapter) {
                    adapter.getItemView(viewPager.currentItem)?.let { itemView ->
                        callBack(itemView)
                    }
                }
            }
        }
    }
}
