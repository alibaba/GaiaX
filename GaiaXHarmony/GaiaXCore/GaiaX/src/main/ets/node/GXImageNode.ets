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

import { GXNodeType, GXRecord } from '../common/GXCommon';
import GXImageModel from '../model/GXImageModel';
import GXViewNode from './GXViewNode';

export default class GXImageNode extends GXViewNode {
  // 图片模式
  fitMode: ImageFit = ImageFit.Fill;
  // 数据模型
  imageModel = new GXImageModel()

  // 节点类型
  nodeType(): GXNodeType {
    return GXNodeType.image;
  }

  // 处理数据
  setData(dataInfo: GXRecord): void {
    // 更新绑定数据
    this.imageModel.url = dataInfo['value'] as string;
    this.imageModel.markInfo = dataInfo['mark'] as GXRecord;
    this.imageModel.summaryInfo = dataInfo['summary'] as GXRecord;
    // 处理extend
    this.handleExtend(dataInfo);
  }

  // 更新style
  updateStyle(styleInfo: GXRecord): void {
    super.updateStyle(styleInfo);
    // 更新mode属性
    const mode = styleInfo['mode'] as string
    if (mode != null) {
      this.getImageMode(mode);
    }
  }

  // 初始化 - 获取样式属性
  setStyleInfo(styleInfo: GXRecord): void {
    super.setStyleInfo(styleInfo);
    // mode属性
    const mode = styleInfo['mode'] as string
    if (mode != null) {
      this.getImageMode(mode);
    }
  }

  // 获取mode
  getImageMode(mode: string): void {
    // 图片的模式
    if (mode == 'cover') {
      this.fitMode = ImageFit.Cover;
    } else if (mode == 'contain') {
      this.fitMode = ImageFit.Contain;
    } else {
      this.fitMode = ImageFit.Fill;
    }
  }
}