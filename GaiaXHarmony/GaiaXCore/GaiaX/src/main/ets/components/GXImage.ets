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

import { GXInjector, GXImageBuilderParams } from './injector/GXInjector';
import GXImageNode from '../node/GXImageNode';

@Component
export struct GXImage {
  // 节点属性
  @Prop node: GXImageNode;

  aboutToAppear(): void {

  }

  // 析构之前调用，用于free节点和rust指针
  aboutToDisappear(): void {

  }

  build() {
    buildImage(this.node);
    // if (GXInjector.instance.injectedImage() != null) {
    //   Row() {
    //     GXInjector.instance.injectedImage()?.builder(new GXImageBuilderParams(this.node.imageModel.url,
    //       this.node.imageModel.placeHolder,
    //       this.node.fitMode,
    //       this.node.width,
    //       this.node.height, () => {
    //         console.log('load image complete');
    //       }, () => {
    //         console.log('load image fail');
    //       }))
    //   }
    //   .id(this.node.nodeId)
    //   .position({ x: this.node.x, y: this.node.y })
    //   .size({ width: this.node.width, height: this.node.height })
    //   .opacity(this.node.opacity)
    //   .shadow(this.node.boxShadow)
    //   .borderWidth(this.node.borderWidth)
    //   .borderColor(this.node.borderColor)
    //   .borderRadius(this.node.borderRadius)
    //   .backgroundColor(this.node.backgroundColor)
    //   .onClick(() => { // 执行事件
    //     console.log("Column click");
    //     this.node.handleEvent();
    //   })
    //   .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
    //     return this.node.getHitTestMode();
    //   })
    // } else {
    //   Image(this.node.imageModel.url)
    //     .id(this.node.nodeId)
    //     .position({ x: this.node.x, y: this.node.y })
    //     .size({ width: this.node.width, height: this.node.height })
    //     .alt(this.node.imageModel.placeHolder)// 使用alt，在网络图片加载成功前使用占位图
    //     .opacity(this.node.opacity)
    //     .shadow(this.node.boxShadow)
    //     .objectFit(this.node.fitMode)
    //     .borderWidth(this.node.borderWidth)
    //     .borderColor(this.node.borderColor)
    //     .borderRadius(this.node.borderRadius)
    //     .backgroundColor(this.node.backgroundColor)
    //     .onComplete(() => {
    //       console.log('load image complete');
    //     })
    //     .onError(() => {
    //       console.log('load image fail');
    //     })
    //     .onClick(() => { // 执行事件
    //       console.log("Column click");
    //       this.node.handleEvent();
    //     })
    //     .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
    //       return this.node.getHitTestMode();
    //     })
    // }
  }
}

@Builder
export function buildImage(node: GXImageNode) {
  if (GXInjector.instance.injectedImage() != null) {
    Row() {
      GXInjector.instance.injectedImage()?.builder(new GXImageBuilderParams(node.imageModel.url,
        node.imageModel.placeHolder,
        node.fitMode,
        node.width,
        node.height, () => {
          console.log('load image complete');
        }, () => {
          console.log('load image fail');
        }))
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
    .linearGradient({
      direction: node.linearGradientDirection,
      colors: node.linearGradientColors
    })
    .onClick(() => { // 执行事件
      console.log("Column click");
      node.handleEvent(node.clickEvent);
    })
    .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
      return node.getHitTestMode();
    })
  } else {
    Image(node.imageModel.url)
      .id(node.nodeId)
      .position({ x: node.x, y: node.y })
      .size({ width: node.width, height: node.height })
      .alt(node.imageModel.placeHolder)// 使用alt，在网络图片加载成功前使用占位图
      .opacity(node.opacity)
      .shadow(node.boxShadow)
      .objectFit(node.fitMode)
      .borderWidth(node.borderWidth)
      .borderColor(node.borderColor)
      .borderRadius(node.borderRadius)
      .backgroundColor(node.backgroundColor)
      .backgroundImage(node.backgroundImage)
      .linearGradient({
        direction: node.linearGradientDirection,
        colors: node.linearGradientColors
      })
      .onComplete(() => {
      })
      .onError(() => {
      })
      .onClick(() => { // 执行事件
        node.handleEvent(node.clickEvent);
      })
      .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
        return node.getHitTestMode();
      })
  }
}