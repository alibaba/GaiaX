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

import GXTemplateContext from '../context/GXTemplateContext';
import { createSubViewByNode } from './GXViewBuilder';
import GXRootNode from '../node/GXRootNode';
import GXNode from '../creator/GXNode';

@Component
export struct GXRootView {
  @Prop node: GXRootNode;
  @Prop gxContext: GXTemplateContext;

  aboutToAppear(): void {

  }

  // 析构之前调用，用于free节点和rust指针
  aboutToDisappear(): void {

  }

  build() {
    buildRootView(this.node, this.gxContext)
  //   Column() {
  //     ForEach(this.node.children, (child: GXNode) => {
  //       createSubViewByNode(this.gxContext, child);
  //     }, (item: GXNode, index) => index + (item.nodeId ?? ''));
  //   }
  //   .id(this.node.nodeId)
  //   .position({ x: this.node.x, y: this.node.y })
  //   .size({ width: this.node.width, height: this.node.height })
  //   .opacity(this.node.opacity)
  //   .shadow(this.node.boxShadow)
  //   .borderRadius(this.node.borderRadius)
  //   .borderWidth(this.node.borderWidth)
  //   .borderColor(this.node.borderColor)
  //   .backgroundColor(this.node.backgroundColor)
  //   .backgroundImage(this.node.backgroundImage)
  //   .onClick(() => { // 执行事件
  //     console.log("Column click");
  //     this.node.handleEvent();
  //   })
  //   .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
  //     return this.node.getHitTestMode();
  //   })
  }
}

@Builder
export function buildRootView(node: GXRootNode, context: GXTemplateContext) {
  Column() {
    ForEach(node.children, (child: GXNode) => {
      createSubViewByNode(context, child);
    }, (item: GXNode, index) => index + (item.nodeId ?? ''));
  }
  .id(node.nodeId)
  .position({ x: node.x, y: node.y })
  .size({ width: node.width, height: node.height })
  .opacity(node.opacity)
  .shadow(node.boxShadow)
  .borderRadius(node.borderRadius)
  .borderWidth(node.borderWidth)
  .borderColor(node.borderColor)
  .backgroundColor(node.backgroundColor)
  .backgroundImage(node.backgroundImage)
  .onClick(() => { // 执行事件
    console.log("Column click");
    node.handleEvent(node.clickEvent);
  })
  .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
    return node.getHitTestMode();
  })
}
