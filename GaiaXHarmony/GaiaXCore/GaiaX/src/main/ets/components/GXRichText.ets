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

import GXRichTextModel from '../model/GXRichTextModel';
import GXRichTextNode from '../node/GXRichTextNode';

@Component
export struct GXRichText {
  // 节点属性
  @Prop node: GXRichTextNode;

  // 处理数据
  aboutToAppear(): void {

  }

  // 析构之前调用，用于free节点和rust指针
  aboutToDisappear(): void {

  }

  build() {
    buildRichText(this.node);
    // Text() {
    //   ForEach(this.node.richTextModels, (textModel: GXRichTextModel) => {
    //     Span(textModel.text)
    //       .fontSize(textModel.fontSize)
    //       .fontWeight(textModel.fontWeight)
    //       .fontColor(textModel.fontColor)
    //       .fontFamily(textModel.fontFamily)
    //   }, (item: GXRichTextModel, index) => index + (item.text ?? ''));
    // }
    // .id(this.node.nodeId)
    // .position({ x: this.node.x, y: this.node.y })
    // .width(this.node.width)
    // .height(this.node.height)
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
export function buildRichText(node: GXRichTextNode){
  Text() {
    ForEach(node.richTextModels, (textModel: GXRichTextModel) => {
      Span(textModel.text)
        .fontSize(textModel.fontSize)
        .fontWeight(textModel.fontWeight)
        .fontColor(textModel.fontColor)
        .fontFamily(textModel.fontFamily)
    }, (item: GXRichTextModel, index) => index + (item.text ?? ''));
  }
  .id(node.nodeId)
  .position({ x: node.x, y: node.y })
  .width(node.width)
  .height(node.height)
  .onClick(() => { // 执行事件
    console.log("Column click");
    node.handleEvent(node.clickEvent);
  })
  .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
    return node.getHitTestMode();
  })
}