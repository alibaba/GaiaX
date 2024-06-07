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
import GXRenderManager from '../binding/GXRenderManager'
import GXTemplateData from '../context/GXTemplateData'
import GXTemplateItem from '../context/GXTemplateItem'
import GXTemplateEngine from '../GXTemplateEngine'
import { buildGaiaXView } from './GXViewBuilder'
import GXNode from '../creator/GXNode'
import { Size } from 'gxstretch'
import hilog from '@ohos.hilog'

@Component
@Reusable
export struct GXComponent {
  // templateContext
  @Prop templateContext: GXTemplateContext;
  // 模板相关基础参数
  @Prop templateItem: GXTemplateItem;
  @Prop templateData: GXTemplateData;
  @Prop measureSize: Size<number>;
  // node
  node: GXNode | null = null;

  aboutToAppear(): void {
    // 防止外部未初始化，此处调用一下
    GXTemplateEngine.instance.init()

    hilog.info(0x0000, 'GXC', 'GXComponent aboutToAppear');

    // 构建节点树
    if (this.templateContext != null) {
      // 更新MeasureSize
      if (this.measureSize != null) {
        this.templateContext.measureSize = this.measureSize;
      }
      // 构建节点树
      const node: GXNode | null = GXRenderManager.renderNodeWithContext(this.templateContext)
      this.node = node;
    } else {
      this.templateContext = new GXTemplateContext(this.templateItem, this.templateData, this.measureSize)
      // 构建节点树
      const node: GXNode | null = GXRenderManager.renderNodeWithContext(this.templateContext)
      this.node = node;
    }
  }

  aboutToDisappear(): void {
    // 递归释放node & style
    this.node?.free();
    hilog.info(0x0000, 'GX', 'GXComponent aboutToDisappear');
  }

  build() {
    if (this.templateContext && this.node) {
      buildGaiaXView(this.templateContext, this.node);
    }
  }
}