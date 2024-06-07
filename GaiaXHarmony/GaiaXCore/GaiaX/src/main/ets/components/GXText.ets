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

import GXTextNode from '../node/GXTextNode'

@Component
export struct GXText {
  // 节点属性
  @Prop node: GXTextNode

  aboutToAppear(): void {

  }

  // 析构之前调用，用于free节点和rust指针
  aboutToDisappear(): void {

  }

  build() {
    buildText(this.node)
    // Text(this.node.text)
    //   .id(this.node.nodeId)
    //   .position({ x: this.node.x, y: this.node.y })
    //   .size({ width: this.node.width, height: this.node.height })
    //   .opacity(this.node.opacity)
    //   .padding(this.node.textPadding)
    //   .fontSize(this.node.fontSize)
    //   .fontWeight(this.node.fontWeight)
    //   .fontFamily(this.node.fontFamily)
    //   .fontColor(this.node.textColor)
    //   .maxLines(this.node.maxLines)// .lineHeight(this.node.lineHeight)
    //   .textAlign(this.node.textAlign)
    //   .borderWidth(this.node.borderWidth)
    //   .borderColor(this.node.borderColor)
    //   .borderRadius(this.node.borderRadius)
    //   .backgroundColor(this.node.backgroundColor)
    //   .backgroundImage(this.node.backgroundImage)
    //   .textOverflow({ overflow: this.node.textOverflow })
    //   .decoration({
    //     type: this.node.textDecoration,
    //     color: this.node.textColor
    //   })
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
export function buildText(node: GXTextNode) {
  Text(node.text)
    .id(node.nodeId)
    .position({ x: node.x, y: node.y })
    .size({ width: node.width, height: node.height })
    .opacity(node.opacity)
    .padding(node.textPadding)
    .fontSize(node.fontSize)
    .fontWeight(node.fontWeight)
    .fontFamily(node.fontFamily)
    .fontColor(node.textColor)
    .maxLines(node.maxLines)// .lineHeight(this.node.lineHeight)
    .textAlign(node.textAlign)
    .borderWidth(node.borderWidth)
    .borderColor(node.borderColor)
    .borderRadius(node.borderRadius)
    .backgroundColor(node.backgroundColor)
    .backgroundImage(node.backgroundImage)
    .textOverflow({ overflow: node.textOverflow })
    .decoration({
      type: node.textDecoration,
      color: node.textColor
    })
    .onClick(() => { // 执行事件
      console.log("Column click");
      node.handleEvent(node.clickEvent);
    })
    .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
      return node.getHitTestMode();
    })
}