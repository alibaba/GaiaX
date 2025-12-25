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

import GXTemplateContext from '../context/GXTemplateContext';
import GXNodeTreeCreator from '../creator/GXNodeTreeCreator';
import GXTemplateData from '../context/GXTemplateData';
import GXTemplateItem from '../context/GXTemplateItem';
import GXDataManager from './GXDataManager';
import { Layout, Size } from 'gxstretch';
import GXNode from '../creator/GXNode';
import { JSON } from '@kit.ArkTS';

export default class GXRenderManager {
  /**
   * 创建节点树 & 计算布局 & 绑定数据
   * @param templateItem
   * @param templateData
   * @param measureSize
   * @returns 节点
   */
  static renderNode(templateItem: GXTemplateItem, templateData: GXTemplateData, measureSize: Size<number>): GXNode | null {
    const templateContext = new GXTemplateContext(templateItem, templateData, measureSize)
    return GXRenderManager.renderNodeWithContext(templateContext);
  }

  /**
   * 根据context创建节点树
   * @param templateContext
   * @returns 节点
   */
  static renderNodeWithContext(templateContext: GXTemplateContext): GXNode | null {
    const templateItem = templateContext.templateItem;
    let node: GXNode | null = GXNodeTreeCreator.creatNodeTree(templateItem, templateContext);
    if (node) {
      node.extension.templateContext = templateContext;
      try {
        // 首次计算
        GXRenderManager.computeAndApplyLayout(node);
        // 绑定数据
        const templateData = templateContext.templateData;
        GXDataManager.bindData(templateData, node);
      } catch (e) {
        console.log(JSON.stringify(e));
      }
      // 返回Node
      return node;
    }
    return null;
  }

  /**
   * 创建节点树 & 计算布局
   * @param templateContext
   * @param isNeedData
   * @returns [width, height]
   */
  static calculateSizeWithContext(templateContext: GXTemplateContext, isNeedData: boolean): [width: number, height: number]  {
    const templateItem = templateContext.templateItem;
    let node: GXNode | null = GXNodeTreeCreator.creatNodeTree(templateItem, templateContext);
    if (node) {
      node.extension.templateContext = templateContext;
      // 首次计算
      GXRenderManager.computeAndApplyLayout(node);
      // 绑定数据
      if (isNeedData) {
        const templateData = templateContext.templateData;
        GXDataManager.bindData(templateData, node);
      }
      return [node.width, node.height];
    }
    return [0, 0];
  }


  /**
   * 更新节点数据 & size
   * @param node
   * @param templateData
   * @param measureSize
   */
  static updateNode(node: GXNode, templateData: GXTemplateData, measureSize: Size<number>) {
    const templateContext = node.extension.templateContext;
    if (templateContext != null) {
      templateContext.measureSize = measureSize;
      templateContext.templateData = templateData;
      //更新布局和计算
      GXDataManager.bindData(templateData, node);
    }
  }

  /**
   * 计算和绑定布局
   * @param node
   */
  static computeAndApplyLayout(node: GXNode, isSpecialUpdate: boolean = false) {
    const layout = GXRenderManager.computeLayout(node);
    if (layout != null) {
      node.applyLayout(layout, isSpecialUpdate);
    }
  }

  /**
   * 只计算布局
   * @param node
   * @returns Layout
   */
  static computeLayout(node: GXNode): Layout | null {
    const size = node.extension.templateContext?.measureSize;
    if (size) {
      const layout = node.computeLayout(size)
      return layout;
    } else {
      return null;
    }
  }

  /**
   * 强制刷新布局，并重置标识
   * @param node
   */
  static setNeedLayout(node: GXNode) {
    // 计算并绑定布局
    GXRenderManager.computeAndApplyLayout(node);
    // 重置标记状态
    const context = node.extension.templateContext;
    if (context != null) {
      context.isNeedLayout = false;
    }
  }
}
