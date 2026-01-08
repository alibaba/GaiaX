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
import { GXCustomViewBuilderParams, GXInjector } from './injector/GXInjector'
import GXCustomViewNode from '../node/GXCustomViewNode'
import { Node } from 'gxstretch';

@Component
export struct GXCustomView {
  // 节点属性
  @Prop node: GXCustomViewNode
  @Prop gxContext: GXTemplateContext;

  // 创建自定义组件的新实例后，执行其build()函数之前调用
  aboutToAppear(): void {

  }

  // 析构之前调用，用于free节点和rust指针
  aboutToDisappear(): void {

  }

  build() {
    // buildCustomView(this.node);
    if (GXInjector.instance.injectedCustomView() != null) {
      Row() {
        GXInjector.instance.injectedCustomView()?.builder(new GXCustomViewBuilderParams(this.node.data, this.node.className))
      }
      .id(this.node.nodeId)
      .position({ x: this.node.x, y: this.node.y })
      .size({ width: this.node.width, height: this.node.height })
      .opacity(this.node.opacity)
      .shadow(this.node.boxShadow)
      .borderWidth(this.node.borderWidth)
      .borderColor(this.node.borderColor)
      .borderRadius(this.node.borderRadius)
      .backgroundColor(this.node.backgroundColor)
      .backgroundImage(this.node.backgroundImage)
      .linearGradient({
        direction: this.node.linearGradientDirection,
        colors: this.node.linearGradientColors
      })
      .onClick(() => { // 执行事件
        this.node.handleEvent(this.node.clickEvent);
      })
      .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
        return this.node.getHitTestMode();
      })
    } else {
      Text(this.node.className)
        .fontSize("12fp")
        .fontColor("red")
        .id(this.node.nodeId)
        .position({ x: this.node.x, y: this.node.y })
        .size({ width: this.node.width, height: this.node.height })
        .opacity(this.node.opacity)
        .shadow(this.node.boxShadow)
        .borderWidth(this.node.borderWidth)
        .borderColor(this.node.borderColor)
        .borderRadius(this.node.borderRadius)
        .backgroundColor(this.node.backgroundColor)
        .backgroundImage(this.node.backgroundImage)
        .linearGradient({
          direction: this.node.linearGradientDirection,
          colors: this.node.linearGradientColors
        })
        .clip(this.node.clip)// 子视图超出父视图进行裁剪
          // .backdropBlur(3) // 毛玻璃效果
          // .clip(new Circle({ width: '280px', height: '280px' }))
          // .mask(new Circle({ width: '280px', height: '280px' }).fill(Color.Gray))
        .onClick(() => { // 执行事件
          this.node.handleEvent(this.node.clickEvent);
        })
        .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
          return this.node.getHitTestMode();
        })
        .onAppear(() => {
        })
    }
  }
}


@Builder
export function buildCustomView(node: GXCustomViewNode) {
  if (GXInjector.instance.injectedCustomView() != null) {
    Row() {
      GXInjector.instance.injectedCustomView()?.builder(new GXCustomViewBuilderParams(node.data, node.className))
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
      node.handleEvent(node.clickEvent);
    })
    .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
      return node.getHitTestMode();
    })
  } else {
    Text(node.className)
      .fontSize("12fp")
      .fontColor("red")
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
      .clip(node.clip)// 子视图超出父视图进行裁剪
        // .backdropBlur(3) // 毛玻璃效果
        // .clip(new Circle({ width: '280px', height: '280px' }))
        // .mask(new Circle({ width: '280px', height: '280px' }).fill(Color.Gray))
      .onClick(() => { // 执行事件
        node.handleEvent(node.clickEvent);
      })
      .onTouchIntercept(() => { // 调用onTouchIntercept修改该组件的HitTestMode属性
        return node.getHitTestMode();
      })
      .onAppear(() => {
      })
  }
}