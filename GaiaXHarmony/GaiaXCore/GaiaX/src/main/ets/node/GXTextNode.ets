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

import MeasureText, { MeasureOptions } from '@ohos.measure';
import { GXNodeType, GXRecord } from '../common/GXCommon';
import GXStyleHelper from '../creator/GXStyleHelper';
import GXContext from '../utils/GXContext';
import GXUtils from '../utils/GXUtils';
import GXViewNode from './GXViewNode';
import { DimPoints, Display } from 'gxstretch';

export default class GXTextNode extends GXViewNode {
  text: string | null = null;
  // 文字属性
  fontSize: number = 14.0;
  fontWeight: number = 400;
  fontFamily: string | null = null;
  // 文字行数
  maxLines: number = 1;
  // 行高
  lineHeight?: number;
  // 文字颜色
  textColor: string = 'black';
  // 文字对齐方式
  textAlign = TextAlign.Start;
  // 文字截断模式
  textOverflow = TextOverflow.Ellipsis;
  // 删除线
  textDecoration = TextDecorationType.None;
  // 文字内边距
  textPadding: Padding | null = null;

  // 节点类型
  nodeType(): GXNodeType {
    return GXNodeType.text;
  }

  // 设置数据
  setData(dataInfo: GXRecord): void {
    this.text = dataInfo['value'] as string;
    // 处理extend
    this.handleExtend(dataInfo);
  }

  // 更新style
  updateStyle(styleInfo: GXRecord): void {
    super.updateStyle(styleInfo);
    // 更新style
    this.configTextStyleInfo(styleInfo);
  }

  // 更新layout
  updateLayout(styleInfo: GXRecord): Boolean {
    let flag = super.updateLayout(styleInfo);
    // 设置textPadding
    this.getTextPadding(styleInfo);
    // fit-content
    const style = this.style;
    if (style != null && style.fitContent && style.display == Display.Flex) {
      const styleInfo = this.extension.styleInfo;
      if (styleInfo != null) {
        flag = true;
        // size
        const width = styleInfo['width'] as string;
        const height = styleInfo['height'] as string;
        if (height != null || width != null) {
          GXStyleHelper.convertSize(style, width, height);
        }
        // minSize
        const minWidth = styleInfo['min-width'] as string;
        const minHeight = styleInfo['min-height'] as string;
        if (minWidth != null || minHeight != null) {
          GXStyleHelper.convertMinSize(style, minWidth, minHeight);
        }
        // maxSize
        const maxWidth = styleInfo['max-width'] as string;
        const maxHeight = styleInfo['max-height'] as string;
        if (maxWidth != null || maxHeight != null) {
          GXStyleHelper.convertMaxSize(style, maxWidth, maxHeight);
        }
        // flexGrow
        const flexGrow = styleInfo['flex-grow'] as string | number;
        if (flexGrow != null) {
          const number = GXUtils.convertToNumber(flexGrow);
          if (number) {
            style.flexGrow = number;
          }
        }
      }
    }
    return flag;
  }

  // 初始化 - 获取样式属性
  setStyleInfo(styleInfo: GXRecord): void {
    super.setStyleInfo(styleInfo);
    // 设置属性
    this.configTextStyleInfo(styleInfo);
  }

  // fit-content & 文字宽度测量逻辑
  updateSpecialLayout() {
    const style = this.style
    if (style != null) {
      // fit-content && 处于展示中
      if (style.fitContent && style.display == Display.Flex) {
        const limitWidth = this.limitWidth;
        const result = this.measureTextSize(limitWidth);
        let size = style.size;
        // 宽
        const width: number = result?.[0];
        size.width = new DimPoints(width);
        // 高
        const height: number = result?.[1];
        size.width = new DimPoints(height);
        style.size = size;

        // 释放rust的指针，重置状态
        style.free();
        // 更新属性，重新生成rust指针
        this.setStyle(style)
        // 标记当前节点
        this.markDirty();
      }
    }
  }

  // 是否需要加到context中的specialNodes
  updateSpecialNodes() {
    const context = this.extension.templateContext;
    if (context != null) {
      context.specialNodes.push(this);
    }
  }

  // 处理文字样式
  private configTextStyleInfo(styleInfo: GXRecord): void {
    // font-size
    const fontSize = styleInfo['font-size'] as string;
    if (fontSize != null) {
      this.fontSize = GXStyleHelper.convertSimpleValue(fontSize, 14.0);
    }

    // font-weight
    const fontWeight = styleInfo['font-weight'] as string;
    if (fontWeight != null) {
      this.fontWeight = GXStyleHelper.convertSimpleValue(fontWeight, 400);
    }

    // font-family
    const fontFamily = styleInfo['font-family'] as string;
    if (fontFamily != null) {
      this.fontFamily = fontFamily;
    }

    // lines
    const lines = styleInfo['lines'] as string;
    if (lines != null) {
      this.maxLines = GXStyleHelper.convertSimpleValue(lines, 1);
    }

    // line-height
    const lineHeight = styleInfo['line-height'] as string;
    if (lineHeight != null) {
      this.lineHeight = GXStyleHelper.convertSimpleValue(lineHeight);
    }

    // color
    const color = styleInfo['color'] as string;
    if (color != null) {
      this.textColor = GXUtils.parseColor(color) ?? 'black';
    }

    // 对齐方式
    const textAlign = styleInfo['text-align'] as string;
    if (textAlign != null) {
      this.textAlign = this.getTextAlign(textAlign);
    }

    // 截断模式
    const textOverflow = styleInfo['text-overflow'] as string;
    if (textOverflow != null) {
      this.textOverflow = this.getTextOverflow(textOverflow);
    }

    // 删除线
    const textDecoration = styleInfo['text-decoration'] as string;
    if (textDecoration != null) {
      this.textDecoration = this.getTextDecoration(textDecoration);
    }

    // padding
    this.getTextPadding(styleInfo);
  }

  /*padding*/
  private getTextPadding(styleInfo: GXRecord) {
    // padding
    const padding = styleInfo['padding'] as string;
    if (padding) {
      this.textPadding = {
        top: padding,
        left: padding,
        bottom: padding,
        right: padding
      }
    } else {
      const paddingTop = styleInfo['padding-top'] as string;
      const paddingLeft = styleInfo['padding-left'] as string;
      const paddingBottom = styleInfo['padding-bottom'] as string;
      const paddingRight = styleInfo['padding-right'] as string;
      if (paddingTop != null || paddingLeft != null || paddingBottom != null || paddingRight != null) {
        this.textPadding = {
          top: paddingTop,
          left: paddingLeft,
          bottom: paddingBottom,
          right: paddingRight
        }
      }
    }
  }

  /*设置TextAlign*/
  private getTextAlign(value: string) {
    if (value == 'right') {
      return TextAlign.End;
    } else if (value == 'center') {
      return TextAlign.Center;
    } else if (value == 'justify') {
      return TextAlign.JUSTIFY;
    } else {
      return TextAlign.Start;
    }
  }

  /*设置TextOverflow*/
  private getTextOverflow(value: string) {
    if (value == 'clip') {
      return TextOverflow.Clip;
    } else {
      return TextOverflow.Ellipsis;
    }
  }

  /*设置Decoration*/
  private getTextDecoration(value: string) {
    if (value == 'line-through') {
      return TextDecorationType.LineThrough;
    } else if (value == 'underline') {
      return TextDecorationType.Underline;
    } else {
      return TextDecorationType.None;
    }
  }

  /**
   * 测量text的size
   * @param limitWidth 限制宽度
   * @returns 元祖类型结果
   */
  private measureTextSize(limitWidth: number): [width: number, height: number] {
    if (this.text == null) {
      return [0, 0];
    }

    // 字体设置
    const options: MeasureOptions = {
      textContent: this.text ?? ''
    };
    options.fontSize = this.fontSize;
    options.fontWeight = this.fontWeight;
    if (this.fontFamily != null) {
      options.fontFamily = this.fontFamily;
    }
    options.maxLines = this.maxLines;
    options.textAlign = this.textAlign;
    options.overflow = this.textOverflow;
    options.lineHeight = this.lineHeight;
    // 行数不为1时候，保持宽度，高度自适应
    if (this.maxLines != 1) {
      options.constraintWidth = limitWidth;
    }

    // size测量
    const textSize: SizeOptions = MeasureText.measureTextSize(options)
    const tmpHeight = GXUtils.convertToNumber(textSize.height) ?? 0.0;
    const tmpWidth = GXUtils.convertToNumber(textSize.width) ?? 0.0;

    // 处理限制条件
    const scale = GXContext.shared().screenScale ?? 1.0;
    const height = Math.ceil(tmpHeight / scale);
    let width = Math.ceil(tmpWidth / scale);
    // 最大宽度限制
    if (this.maxLines == 1 && limitWidth != 0) {
      width = Math.min(width, limitWidth);
    }

    return [width, height];
  }
}