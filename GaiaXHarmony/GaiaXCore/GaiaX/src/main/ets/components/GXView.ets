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

import GXTemplateContext from '../context/GXTemplateContext'
import { createSubViewByNode } from './GXViewBuilder'
import GXViewNode from '../node/GXViewNode'
import GXNode from '../creator/GXNode'

@Component
export struct GXView {
  // 节点属性
  @Prop node: GXViewNode
  @Prop gxContext: GXTemplateContext;

  // 创建自定义组件的新实例后，执行其build()函数之前调用
  aboutToAppear(): void {

  }

  // 析构之前调用，用于free节点和rust指针
  aboutToDisappear(): void {

  }

  build() {
    buildView(this.node, this.gxContext);
    // Column() {
    //   if (this.node.children.length > 0) {
    //     // 创建view，并且需要创建子视图
    //     ForEach(this.node.children, (child: GXNode) => {
    //       createSubViewByNode(this.gxContext, child);
    //     }, (item: GXNode, index) => index + (item.nodeId ?? ''));
    //   }
    // }
    // .id(this.node.nodeId)
    // .position({ x: this.node.x, y: this.node.y })
    // .size({ width: this.node.width, height: this.node.height })
    // .opacity(this.node.opacity)
    // .shadow(this.node.boxShadow)
    // .borderWidth(this.node.borderWidth)
    // .borderColor(this.node.borderColor)
    // .borderRadius(this.node.borderRadius)
    // .backgroundColor(this.node.backgroundColor)
    // .backgroundImage(this.node.backgroundImage)
    // .clip(this.node.clip) // 子视图超出父视图进行裁剪
    // // .backdropBlur(3) // 毛玻璃效果
    // // .clip(new Circle({ width: '280px', height: '280px' }))
    // // .mask(new Circle({ width: '280px', height: '280px' }).fill(Color.Gray))
    // .onClick(() => { // 执行事件
    //   console.log("Column click");
    //   this.node.handleEvent();
    // })
    // .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
    //   return this.node.getHitTestMode();
    // })
  }
}


@Builder
export function buildView(node: GXViewNode, context: GXTemplateContext) {
  Column() {
    if (node.children.length > 0) {
      // 创建view，并且需要创建子视图
      ForEach(node.children, (child: GXNode) => {
        createSubViewByNode(context, child);
      }, (item: GXNode, index) => index + (item.nodeId ?? ''));
    }
  }
  .id(node.nodeId)
  .position({ x: node.x, y: node.y })
  .size({ width: node.width, height: node.height })
  .opacity(node.opacity)
  .shadow(node.boxShadow)
  .borderWidth(node.borderWidth)
  .borderColor(node.borderColor)
  .borderRadius(node.borderRadius)
  .backgroundColor(node.backgroundColor)
  .backgroundImage(node.backgroundImage)
  .clip(node.clip) // 子视图超出父视图进行裁剪
  // .backdropBlur(3) // 毛玻璃效果
  // .clip(new Circle({ width: '280px', height: '280px' }))
  // .mask(new Circle({ width: '280px', height: '280px' }).fill(Color.Gray))
  .onClick(() => { // 执行事件
    console.log("Column click");
    node.handleEvent(node.clickEvent);
  })
  .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
    return node.getHitTestMode();
  })
}