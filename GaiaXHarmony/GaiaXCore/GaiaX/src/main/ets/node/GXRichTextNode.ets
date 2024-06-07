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
import GXRichTextModel from '../model/GXRichTextModel';
import GXStyleHelper from '../creator/GXStyleHelper';
import GXTextNode from './GXTextNode';

export default class GXRichTextNode extends GXTextNode {
  // 高亮标签（#,@,$等）
  highlightTag: string | null = null;
  // 高亮颜色
  highlightColor: string | null = null;
  // 高亮字号
  highlightFontSize: number | null = null;
  // 高亮字重
  highlightFontWeight: number | null = null;
  // 高亮字体
  highlightFontFamily: string | null = null;
  // 解析之后的数据
  richTextModels: Array<GXRichTextModel> | null = null;

  // 节点类型
  nodeType(): GXNodeType {
    return GXNodeType.richtext;
  }

  // 绑定数据
  setData(dataInfo: GXRecord) {
    // 更新布局
    this.handleExtend(dataInfo);
    // 根据正则表达式拆封成数字，foreach渲染
    const text = dataInfo['value'] as string;
    this.handleRichText(text);
  }

  // 更新style
  updateStyle(styleInfo: GXRecord): void {
    super.updateStyle(styleInfo);
    // highlight-tag
    const highlightTag = styleInfo['highlight-tag'] as string;
    if (highlightTag != null) {
      this.highlightTag = highlightTag;

      // highlight-color
      const highlightColor = styleInfo['highlight-color'] as string;
      if (highlightColor != null) {
        this.highlightColor = highlightColor;
      }

      // highlight-font-size
      const highlightFontSize = styleInfo['highlight-font-size'] as string;
      if (highlightFontSize != null) {
        this.highlightFontSize = GXStyleHelper.convertSimpleValue(highlightFontSize, 14);
      }

      // highlight-font-weight
      const highlightFontWeight = styleInfo['highlight-font-weight'] as string;
      if (highlightFontWeight != null) {
        this.highlightFontWeight = GXStyleHelper.convertSimpleValue(highlightFontWeight, 400);
      }

      // highlight-font-weight
      const highlightFontFamily = styleInfo['highlight-font-family'] as string;
      if (highlightFontFamily != null) {
        this.highlightFontFamily = highlightFontFamily;
      }
    }
  }

  // 更具tag解析text，生成最终的内容
  private handleRichText(text: string) {
    const highlightTag = this.highlightTag;
    if (highlightTag != null && text != null) {
     this.richTextModels = this.splitTextWithDynamicDelimiters(text, highlightTag);
    } else {
      let models = Array<GXRichTextModel>();
      // 构建model
      let item: GXRichTextModel = new GXRichTextModel()
      item.text = text;
      item.isHighlight = false;
      item.fontSize = this.fontSize;
      item.fontColor = this.textColor;
      item.fontFamily = this.fontFamily
      item.fontWeight = this.fontWeight;
      // 赋值
      this.richTextModels = models;
    }
  }

  // 正则解析
  private splitTextWithDynamicDelimiters(text: string, delimiter: string): GXRichTextModel[] {
    // 对分隔符进行转义，以避免特殊字符影响正则表达式
    const escapedDelimiter = delimiter.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    // 使用 RegExp 构造函数创建正则表达式，捕获分隔符之间的文本
    // const regex = new RegExp(`${escapedDelimiter}(.*?)${escapedDelimiter}`, 'g');
    // 使用 split 方法分割字符串，但分割后的数组不包含匹配的 #...# 本身
    // const parts = text.split(regex).filter(Boolean);
    // const regex: RegExp = /(#.*?#)/g;
    const regex: RegExp = new RegExp(`(${escapedDelimiter}.*?${escapedDelimiter})`, 'g');
    // 使用正则表达式进行分割
    const matches: string[] = text.split(regex).filter(part => part !== "");
    // 转化Model
    let parts = Array<GXRichTextModel>();
    matches.forEach((value: string) => {
      let item: GXRichTextModel = new GXRichTextModel()
      let result = value;
      if (value.length >= 2 && value.startsWith(escapedDelimiter) && value.endsWith(escapedDelimiter)) {
        result = value.slice(1, -1);
        item.text = result;
        item.isHighlight = true;
        item.fontColor = this.highlightColor;
        item.fontSize = this.highlightFontSize;
        item.fontFamily = this.highlightFontFamily
        item.fontWeight = this.highlightFontWeight;
      } else {
        item.text = result;
        item.isHighlight = true;
        item.fontSize = this.fontSize;
        item.fontColor = this.textColor;
        item.fontFamily = this.fontFamily
        item.fontWeight = this.fontWeight;
      }
      parts.push(item);
    });
    return parts;
  }
}


