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

enum InjectType {
  image = 0
}

export class GXImageBuilderParams {
  url: ResourceStr | null = null
  viewWidth: number | null = null
  viewHeight: number | null = null
  imageFit: ImageFit = ImageFit.Fill
  placeHolder: ResourceStr | null = null
  // 回调
  onComplete: () => void = () => {
  }
  onError: () => void = () => {
  }

  constructor(url: ResourceStr | null, placeHolder: ResourceStr | null, imageFit: ImageFit, viewWidth: number,
    viewHeight: number, onComplete: () => void, onError: () => void) {
    this.url = url
    this.placeHolder = placeHolder
    this.onComplete = onComplete
    this.onError = onError
    this.imageFit = imageFit
    this.viewWidth = viewWidth
    this.viewHeight = viewHeight
  }
}

export class GXInjector {
  static instance = new GXInjector();
  // 注册图片组件Map
  injectorMap: Record<number, WrappedBuilder<[GXImageBuilderParams]>> = {};

   /**
    * 注册Image
    * @param builder
    */
  registerImage( builder: WrappedBuilder<[GXImageBuilderParams]>) {
    this.injectorMap[InjectType.image] = builder
  }

  /**
   * 获取注册的Image
   * @returns Image
   */
  injectedImage():WrappedBuilder<[GXImageBuilderParams]> | null {
    if (this.injectorMap[InjectType.image]) {
      return this.injectorMap[InjectType.image]
    } else {
      return null
    }
  }
}
