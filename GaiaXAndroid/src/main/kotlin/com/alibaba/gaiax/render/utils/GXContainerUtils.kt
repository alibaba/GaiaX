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

import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.render.view.container.GXViewHolder

object GXContainerUtils {

    fun notifyOnAppear(gxTemplateContext: GXTemplateContext) {
        gxTemplateContext.containers?.forEach {
            it?.findVisibleItems {
                onAppear(it)
            }
        }
    }

    fun notifyOnDisappear(gxTemplateContext: GXTemplateContext) {
        gxTemplateContext.containers?.forEach {
            it?.findVisibleItems {
                onDisappear(it)
            }
        }
    }

    private fun GXContainer.findVisibleItems(callBack: (holder: GXViewHolder) -> Unit) {
        try {
            val rc = this
            if (rc.layoutManager is LinearLayoutManager) {
                val layoutManager = rc.layoutManager as LinearLayoutManager
                val firstPos = layoutManager.findFirstVisibleItemPosition()
                val lastPos = layoutManager.findLastVisibleItemPosition() + 1
                for (index in firstPos..lastPos) {
                    (rc.findViewHolderForLayoutPosition(index) as? GXViewHolder)?.let {
                        callBack(it)
                    }
                }
            } else if (rc.layoutManager is GridLayoutManager) {
                val layoutManager = rc.layoutManager as GridLayoutManager
                val firstPos = layoutManager.findFirstVisibleItemPosition()
                val lastPos = layoutManager.findLastVisibleItemPosition() + 1
                for (index in firstPos..lastPos) {
                    (rc.findViewHolderForLayoutPosition(index) as? GXViewHolder)?.let {
                        callBack(it)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onAppear(holder: GXViewHolder) {
        val itemView = holder.itemView
        if (itemView is ViewGroup && itemView.childCount > 0) {
            GXTemplateEngine.instance.onAppear(itemView.getChildAt(0))
        }
    }

    private fun onDisappear(holder: GXViewHolder) {
        val itemView = holder.itemView
        if (itemView is ViewGroup && itemView.childCount > 0) {
            GXTemplateEngine.instance.onDisappear(itemView.getChildAt(0))
        }
    }

}