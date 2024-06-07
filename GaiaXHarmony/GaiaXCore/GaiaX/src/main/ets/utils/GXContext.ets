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

import display from '@ohos.display';

export default class GXContext {
  // 单例属性
  private static instance: GXContext;
  // 屏幕属性
  screenScale: number = 1.0;
  screenWidth: number = 0.0;
  screenHeight: number = 0.0;

  // 单例方法
  static shared() {
    if (!GXContext.instance) {
      GXContext.instance = new GXContext();
    }
    return GXContext.instance
  }

  // 构造初始化
  private constructor() {
    this.getDisplayInfo();
  }

  /**
   * 获取屏幕信息
   */
  private getDisplayInfo() {
    const screenInfo = display.getDefaultDisplaySync();
    this.screenScale = screenInfo.scaledDensity ?? 1;
    this.screenHeight = screenInfo.height;
    this.screenWidth = screenInfo.width;
  }
}