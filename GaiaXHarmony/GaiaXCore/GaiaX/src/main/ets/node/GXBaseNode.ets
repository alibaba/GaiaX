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

import GXStyleHelper from '../creator/GXStyleHelper';
import { GXRecord } from '../common/GXCommon';
import GXNode from '../creator/GXNode';

export default class GXBaseNode extends GXNode {
  // 透明度
  opacity = 1.0;
  // 越界显示，默认为hidden
  clip = true;
  // 边框，默认透明，宽度0
  borderWidth = 0.0;
  borderColor = 'Transparent';
  // 背景色，默认透明
  backgroundColor = 'Transparent';
  // 圆角，默认无圆角
  borderRadius: string | null = null;
  // 毛玻璃效果
  backdropFilter: string | null = null;
  // 渐变
  backgroundImage: string | null = null;
  // 阴影
  boxShadow: ShadowOptions | null = null;


  // 更新布局 & 样式信息
  handleExtend(dataInfo: GXRecord) {
    const extend = dataInfo['extend'] as GXRecord;
    if (extend != null) {
      // 处理动态样式
      this.updateStyle(extend);
      // 处理动态布局
      const isMark =  this.updateLayout(extend);
      if (isMark) {
        const style = this.style
        if (style != null) {
          // 释放rust的指针，重置状态
          style.free();
          // 更新属性，重新生成rust指针
          this.setStyle(style)
          // 标记当前节点
          this.markDirty();
          // 标记重新布局计算
          if (this.extension.templateContext) {
            this.extension.templateContext.isNeedLayout = true;
          }
        }
      }
    }
  }

  // 更新样式属性
  updateStyle(styleInfo: GXRecord) {
    this.configStyleInfo(styleInfo);
  }

  // 更新布局属性，影响布局
  updateLayout(styleInfo: GXRecord): Boolean {
    let flag = false;
    if (this.style && styleInfo) {
      flag = GXStyleHelper.updateStyle(this.style, styleInfo);
    }
    return flag;
  }

  // 初始化 - 获取样式属性
  setStyleInfo(styleInfo: GXRecord): void {
    // 设置样式属性
    this.configStyleInfo(styleInfo);
  }

  //  处理style样式
  private configStyleInfo(styleInfo: GXRecord): void {
    // opacity
    const opacity = styleInfo['opacity'] as string;
    if (opacity != null) {
      this.opacity = GXStyleHelper.convertSimpleValue(opacity);
    }

    // overflow
    const overflow = styleInfo['overflow'] as string;
    if (overflow != null && overflow != 'hidden') {
      this.clip = false;
    } else {
      this.clip = true;
    }

    // border-width
    const borderWidth = styleInfo['border-width'] as string;
    if (borderWidth != null) {
      this.borderWidth = GXStyleHelper.convertSimpleValue(borderWidth);
    }

    // border-color
    const borderColor = styleInfo['border-color'] as string;
    if (borderColor) {
      this.borderColor = borderColor;
    }

    // background-color
    const backgroundColor = styleInfo['background-color'] as string;
    if (backgroundColor != null) {
      this.backgroundColor = backgroundColor;
    }

    // background-image
    const backgroundImage = styleInfo['background-image'] as string;
    if (backgroundImage != null) {
      this.backgroundImage = backgroundImage;
    }

    // background-filter
    const backdropFilter = styleInfo['backdrop-filter'] as string;
    if (backdropFilter != null) {
      this.backdropFilter = backdropFilter;
    }

    // box-shadow
    this.getShadow(styleInfo)

    // border-radius
    this.getBorderRadius(styleInfo);
  }

  // 圆角处理
  private getBorderRadius(styleInfo: GXRecord) {
    let isFullBorderRadius = true;
    // 非全圆角 + 高优
    const topLeft = styleInfo['border-top-left-radius'] as string;
    const topRight = styleInfo['border-top-right-radius'] as string;
    const bottomRight = styleInfo['border-bottom-right-radius'] as string;
    const bottomLeft = styleInfo['border-bottom-left-radius'] as string;
    if (topLeft != null || topRight != null || bottomRight != null || bottomLeft != null) {
      isFullBorderRadius = false;
      const tf = GXStyleHelper.convertSimpleValue(topLeft, 0);
      const tr = GXStyleHelper.convertSimpleValue(topRight, 0);
      const br = GXStyleHelper.convertSimpleValue(bottomRight, 0);
      const bl = GXStyleHelper.convertSimpleValue(bottomLeft, 0);
      this.borderRadius = `${tf} ${tr} ${br} ${bl}`
    }
    // border-radius
    if (isFullBorderRadius) {
      const borderRadius = styleInfo['border-radius'] as string;
      if (borderRadius != null) {
        let value = GXStyleHelper.convertSimpleValue(borderRadius, 0);
        this.borderRadius = `${value}}`;
      }
    }
  }

  // 阴影处理
  private getShadow(styleInfo: GXRecord) {
    const boxShadow = styleInfo['box-shadow'] as string;
    if (boxShadow != null) {
      const array = boxShadow.split(' ');
      if (array && array.length == 5) {
        let shadowInfo: ShadowOptions = {radius: 0};
        shadowInfo['offsetX'] = GXStyleHelper.convertSimpleValue(array[0]);
        shadowInfo['offsetY'] = GXStyleHelper.convertSimpleValue(array[1]);
        shadowInfo['radius'] = GXStyleHelper.convertSimpleValue(array[2]);
        shadowInfo['color'] = array[4];
        this.boxShadow = shadowInfo;
      }
    }
  }
}
