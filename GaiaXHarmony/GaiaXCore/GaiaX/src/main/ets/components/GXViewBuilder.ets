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

import GXRichTextNode from '../node/GXRichTextNode'
import GXScrollNode from '../node/GXScrollNode'
import GXSliderNode from '../node/GXSiderNode'
import GXImageNode from '../node/GXImageNode'
import GXGridNode from '../node/GXGridNode'
import GXRootNode from '../node/GXRootNode'
import GXViewNode from '../node/GXViewNode'
import GXTextNode from '../node/GXTextNode'
import GXNode from '../creator/GXNode'
import { GXRootView, buildRootView } from './GXRootView'
import { GXRichText, buildRichText } from './GXRichText'
import { GXImage, buildImage } from './GXImage'
import { GXView, buildView } from './GXView'
import { GXText, buildText } from './GXText'
import GXScrollView from './GXScrollView'
import GXSliderView from './GXSilderView'
import GXGridView from './GXGridView'
import GXTemplateContext from '../context/GXTemplateContext'
import { GXNodeType } from '../common/GXCommon'
import { buildCustomView } from './GXCustomView'
import GXCustomViewNode from '../node/GXCustomViewNode'
import { Display } from 'gxstretch'

@Builder
export function buildGaiaXView(gxContext: GXTemplateContext, node: GXNode) {
  // 创建根视图
  createTemplateViewByNode(gxContext, node)
}

@Builder
export function createTemplateViewByNode(gxContext: GXTemplateContext, node: GXNode) {
  if (node.nodeType() == GXNodeType.scroll) {
    // scroll
    GXScrollView({ gxContext: gxContext, node: (node as GXScrollNode) })

  } else if (node.nodeType() == GXNodeType.grid) {
    // grid
    GXGridView({ node: (node as GXGridNode) })

  } else if (node.nodeType() == GXNodeType.slider) {
    // slider
    GXSliderView({ node: (node as GXSliderNode) })

  } else {
    // default - rootView
    // GXRootView({ gxContext: gxContext, node: (node as GXRootNode) })
    buildRootView((node as GXRootNode), gxContext);
  }
}

@Builder
export function createSubViewByNode(gxContext: GXTemplateContext, node: GXNode) {
  if (node.style?.display != Display.Flex) {
    // 不可见节点，直接终止递归
    Column()
  } else if (node.isTemplateType) {
    // template
    createTemplateViewByNode(gxContext, node)

  } else if (node.nodeType() == GXNodeType.view) {
    // view
    // GXView({ gxContext: gxContext, node: (node as GXViewNode) })
    buildView((node as GXViewNode), gxContext)

  } else if (node.nodeType() == GXNodeType.image) {
    // image
    // GXImage({ node: (node as GXImageNode) })
    buildImage((node as GXImageNode))

  } else if (node.nodeType() == GXNodeType.text) {
    // text
    // GXText({ node: (node as GXTextNode) })
    buildText((node as GXTextNode))

  } else if (node.nodeType() == GXNodeType.richtext) {
    // rich-text
    // GXRichText({ node: (node as GXRichTextNode) })
    buildRichText((node as GXRichTextNode))

  } else if (node.nodeType() == GXNodeType.custom) {
    // 用户自定义view
    buildCustomView((node as GXCustomViewNode))
  }  else {
    // default - view
    // GXView({ gxContext: gxContext, node: (node as GXViewNode) })
    buildView((node as GXViewNode), gxContext)
  }
}