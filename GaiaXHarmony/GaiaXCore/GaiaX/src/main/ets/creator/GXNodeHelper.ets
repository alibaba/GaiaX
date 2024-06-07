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

import { GXRecord, GXNodeType } from '../common/GXCommon';
import GXRichTextNode from '../node/GXRichTextNode';
import GXConstants from '../common/GXConstants';
import GXScrollNode from '../node/GXScrollNode';
import GXSliderNode from '../node/GXSiderNode';
import GXImageNode from '../node/GXImageNode';
import GXRootNode from '../node/GXRootNode';
import GXGridNode from '../node/GXGridNode';
import GXTextNode from '../node/GXTextNode';
import GXViewNode from '../node/GXViewNode';
import { Style } from 'gxstretch';
import GXNode from './GXNode';

export default class GXNodeHelper {
  /**
   * 模板类型
   */
  static isTemplateType(layerInfo: GXRecord): boolean {
    return layerInfo['type'] == GXConstants.GXNodeTypeTemplate
  }

  /**
   * 自定义模板
   */
  static isNestTemplateType(layerInfo: GXRecord): boolean {
    return GXNodeHelper.isTemplateType(layerInfo) && layerInfo['sub-type'] == GXConstants.GXNodeTypeCustom;
  }

  /**
   * 容器模板
   */
  static isContainer(layerInfo: GXRecord): boolean {
    return (GXNodeHelper.isScrollType(layerInfo) ||
    GXNodeHelper.isSliderType(layerInfo) ||
    GXNodeHelper.isGridType(layerInfo));
  }

  /**
   * scroll模板类型
   */
  static isScrollType(layerInfo: GXRecord): boolean {
    return GXNodeHelper.isTemplateType(layerInfo) && layerInfo['sub-type'] == GXConstants.GXNodeTypeScroll;
  }

  /**
   * slider模板类型
   */
  static isSliderType(layerInfo: GXRecord): boolean {
    return GXNodeHelper.isTemplateType(layerInfo) && layerInfo['sub-type'] == GXConstants.GXNodeTypeSlider;
  }

  /**
   * grid模板类型
   */
  static isGridType(layerInfo: GXRecord): boolean {
    return GXNodeHelper.isTemplateType(layerInfo) && layerInfo['sub-type'] == GXConstants.GXNodeTypeGrid;
  }

  /**
   * 获取回节点的类型方法
   * @param layerInfo
   * @returns 节点类型
   */
  static getNodeType(layerInfo: GXRecord): GXNodeType {
    let nodeType = GXNodeType.view;
    // 类型查询
    if (layerInfo != null) {
      const type = layerInfo['type'] as string;
      if (GXNodeHelper.isScrollType(layerInfo)) {
        // scroll类型
        nodeType = GXNodeType.scroll;

      } else if (GXNodeHelper.isSliderType(layerInfo)) {
        // slider类型
        nodeType = GXNodeType.slider;

      } else if (GXNodeHelper.isGridType(layerInfo)) {
        // grid类型
        nodeType = GXNodeType.grid;

      } else if (GXNodeHelper.isTemplateType(layerInfo)) {
        // 根视图
        nodeType = GXNodeType.template;

      } else if (type == 'view') {
        // 视图
        nodeType = GXNodeType.view;

      } else if (type == 'image') {
        // 图片
        nodeType = GXNodeType.image;

      } else if (type == 'text') {
        // text
        nodeType = GXNodeType.text;

      } else if (type == 'richtext') {
        // 富文本
        nodeType = GXNodeType.richtext;

      } else if (type == 'iconfont') {
        // iconfont
        nodeType = GXNodeType.iconfont;
      } else if (type == 'custom') {
        // 自定义类型
        nodeType = GXNodeType.custom;

      } else {
        // 默认为view
        nodeType = GXNodeType.image;
      }
    }
    return nodeType;
  }

  /**
   * 创建节点
   * @param type 节点类型
   * @param style 样式
   * @returns 对应节点
   */
  static creatNode(type: GXNodeType, style: Style): GXNode {
    let node: GXNode | null = null;
    switch (type) {
      case GXNodeType.scroll:
        node = new GXScrollNode(style);
        break;
      case GXNodeType.grid:
        node = new GXGridNode(style);
        break;
      case GXNodeType.slider:
        node = new GXSliderNode(style);
        break;
      case GXNodeType.template:
        node = new GXRootNode(style);
        break;
      case GXNodeType.view:
        node = new GXViewNode(style);
        break;
      case GXNodeType.image:
        node = new GXImageNode(style);
        break;
      case GXNodeType.text:
        node = new GXTextNode(style);
        break;
      case GXNodeType.richtext:
        node = new GXRichTextNode(style);
        break;
      default:
        node = new GXViewNode(style);
        break;
    }
    return node;
  }
}