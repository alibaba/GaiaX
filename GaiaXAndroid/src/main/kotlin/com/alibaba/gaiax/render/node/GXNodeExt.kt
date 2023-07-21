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

package com.alibaba.gaiax.render.node

import android.view.View


/**
 * @suppress
 */
fun GXNode.getGXViewById(id: String): View? {
    return findViewById(this, id)
}

/**
 * @suppress
 */
private fun GXNode.findViewById(viewData: GXNode, id: String): View? {
    if (viewData.templateNode.layer.id == id) {
        return viewData.view
    }
    viewData.children?.forEach {
        val view = findViewById(it, id)
        if (view != null) {
            return view
        }
    }
    return null
}


/**
 * @suppress
 */
fun GXNode.getGXNodeById(id: String): GXNode? {
    return findNodeById(this, id)
}

/**
 * @suppress
 */
private fun GXNode.findNodeById(gxNode: GXNode, id: String): GXNode? {
    if (gxNode.templateNode.layer.id == id) {
        return gxNode
    }
    gxNode.children?.forEach {
        val view = findNodeById(it, id)
        if (view != null) {
            return view
        }
    }
    return null
}


fun GXNode.getGXNodeByView(view: View): GXNode? {
    return findNodeByView(this, view)
}

private fun GXNode.findNodeByView(gxNode: GXNode, view: View): GXNode? {
    if (gxNode.view == view) {
        return gxNode
    }
    gxNode.children?.forEach {
        val result = findNodeByView(it, view)
        if (result != null) {
            return result
        }
    }
    return null
}