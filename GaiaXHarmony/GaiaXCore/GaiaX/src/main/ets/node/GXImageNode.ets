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
    switch (mode) {
      case 'cover':
        this.fitMode = ImageFit.Cover;
        break;
      case 'contain':
        this.fitMode = ImageFit.Contain;
        break;
      case 'scaleToFill':
        this.fitMode = ImageFit.Fill;
        break;
      case 'aspectFit':
        this.fitMode = ImageFit.Contain;
        break;
      case 'aspectFill':
        this.fitMode = ImageFit.Cover;
        break;
      case 'left':
        this.fitMode = ImageFit.START;
        break;
      case 'right':
        this.fitMode = ImageFit.END;
        break;
      case 'center':
        this.fitMode = ImageFit.CENTER;
        break;
      case 'top':
        this.fitMode = ImageFit.TOP;
        break;
      case 'bottom':
        this.fitMode = ImageFit.BOTTOM;
        break;
      case 'top left':
        this.fitMode = ImageFit.TOP_START;
        break;
      case 'top right':
        this.fitMode = ImageFit.TOP_END;
        break;
      case 'bottom left':
        this.fitMode = ImageFit.BOTTOM_START;
        break;
      case 'bottom right':
        this.fitMode = ImageFit.BOTTOM_END;
        break;
      default:
        this.fitMode = ImageFit.Fill;
    }
  }
}