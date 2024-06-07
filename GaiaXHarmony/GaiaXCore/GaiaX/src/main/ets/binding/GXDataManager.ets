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

import GXTemplateData from '../context/GXTemplateData';
import GXRenderManager from './GXRenderManager';
import { GXRecord } from '../common/GXCommon';
import GXDataParser from './GXDataParser';
import GXNode from '../creator/GXNode';

export default class GXDataManager {
  /**
   * 绑定数据
   * @param data 数据源
   * @param node 根节点
   */
  static bindData(data: GXTemplateData, node: GXNode) {
    if (data.jsonData == null || node == null) {
      return;
    }
    // 真实绑定数据
    GXDataManager.gxBindData(data.jsonData, node);
  }

  /**
   * 绑定数据至节点上
   * @param data 数据源（json）
   * @param node 节点
   */
  private static gxBindData(data: GXRecord, node: GXNode) {
    // 获取拍平的节点数据
    const flatNodes = node.flatNodes;
    if (flatNodes && flatNodes.length > 0) {
      flatNodes.forEach(tmpNode => {
        if (tmpNode.shouldBinding()) {
          //判断是否为嵌套模板
          if (tmpNode != node && tmpNode.isTemplateType) {
            //处理嵌套模板的外部数据源
            const virtualData = tmpNode.extension.virtualData;
            let resultVirtualData: GXRecord | null = null;
            if (virtualData != null) {
              resultVirtualData = GXDataParser.parseData(virtualData, data);
            }
            // 绑定数据
            tmpNode.extension.virtualExtend = null;
            let resultData: GXRecord | null = null;
            if (resultVirtualData != null) {
              // ①获取外部extend
              tmpNode.extension.virtualExtend = resultVirtualData['extend'] as GXRecord;
              // ②获取外部数据源，进行绑定操作
              resultData = resultVirtualData['value'] as GXRecord;
            }
            // ③绑定数据到子模板
            if (resultData != null) {
              GXDataManager.gxBindData(resultData, tmpNode);
            }
          } else {
            // 绑定数据到节点上
            tmpNode.applyData(data)
          }
        }
      })
    }

    // 绑定数据之后，重新计算布局
    if (node.isRootNode()) {
      const context = node.extension?.templateContext;
      if (context && context.isNeedLayout) {
        //判断是否有文本
        const specialNodes = context.specialNodes;
        if (specialNodes && specialNodes.length > 0) {
          GXRenderManager.computeAndApplyLayout(node);
          // 遍历text，更新文字布局
          specialNodes.forEach(node => {
            node.updateSpecialLayout();
          })
        }
        // 重新结算
        GXRenderManager.setNeedLayout(node);
      }
    }
  }
}