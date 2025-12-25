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

import GXTemplateManager from '../template/GXTemplateManager';
import GXTemplateContext from '../context/GXTemplateContext';
import GXTemplateInfo from '../template/GXTemplateInfo';
import GXTemplateItem from '../context/GXTemplateItem';
import { GXNodeType, GXRecord } from '../common/GXCommon';
import GXStyleHelper from './GXStyleHelper';
import GXNodeHelper from './GXNodeHelper';
import { JSON } from '@kit.ArkTS';
import GXNode from './GXNode';
import GXCustomViewNode from '../node/GXCustomViewNode';

export default class GXNodeTreeCreator {
  /**
   * 外部调用构建节点树
   * @param templateItem
   * @param templateContext
   * @returns 根节点
   */
  static creatNodeTree(templateItem: GXTemplateItem, templateContext: GXTemplateContext): GXNode | null {
    const templateInfo = GXTemplateManager.instance.loadTemplateContent(templateItem);
    return GXNodeTreeCreator.createTemplateNode(templateInfo, templateItem, templateContext);
  }

  /**
   * 创建模板节点（根节点），内部调用
   * @param templateInfo
   * @param templateItem
   * @param templateContext
   * @returns 根节点
   */
  private static createTemplateNode(templateInfo: GXTemplateInfo, templateItem: GXTemplateItem, templateContext: GXTemplateContext): GXNode | null {
    let node: GXNode | null = null;
    if (templateInfo.isValid) {

      // 处理异常模板
      if (!GXNodeHelper.isTemplateType(templateInfo.layerInfo)) {
        throw new Error(`GXNodeTreeCreator createTemplateNode template type is error, ${templateInfo.layerInfo}`)
      }

      let layerInfo = templateInfo.layerInfo;
      let dataInfo = templateInfo.dataInfo;
      let styleInfo = templateInfo.styleInfo;

      GXNodeTreeCreator.mergeRootStyle(templateItem, styleInfo);

      // 构建节点
      let flatNodes: Array<GXNode> = [];
      let layerInfos: GXRecord[] = [layerInfo];
      const nodes = GXNodeTreeCreator.recursionCreateNode(templateContext, templateItem, styleInfo, dataInfo, layerInfos, flatNodes, null);
      if (nodes && nodes.length > 0) {
        node = nodes[0];
        node.flatNodes = flatNodes;
      }
    }
    // 返回根节点
    return node;
  }

  /**
   * 合并当前节点和虚拟节点的style属性
   * @param templateItem
   * @param styleInfo
   */
  private static mergeRootStyle(templateItem: GXTemplateItem, styleInfo: GXRecord) {
    const templateId = templateItem.templateId;
    const virtualStyleInfo = templateItem.virtualStyleInfo;
    if (virtualStyleInfo && templateId) {
      let rootStyleInfo: GXRecord = styleInfo[templateId] as GXRecord;
      if (rootStyleInfo) {
        // 合并虚拟style
        Object.entries(virtualStyleInfo).forEach(item => {
          rootStyleInfo[item[0]] = item[1];
        });
      } else {
        // 将虚拟style直接赋值
        rootStyleInfo = virtualStyleInfo;
      }
      styleInfo[templateId] = rootStyleInfo;
    }
  }

  /**
   * 递归创建子节点 & 嵌套子模板
   * @param templateContext
   * @param templateItem
   * @param styleInfo 样式信息
   * @param dataInfo 数据绑定信息
   * @param layers 层级信息
   * @param flatNodes 扁平化节点数组
   * @returns 节点数组
   */
  private static recursionCreateNode(
    templateContext: GXTemplateContext,
    templateItem: GXTemplateItem,
    styleInfo: GXRecord,
    dataInfo: GXRecord,
    layers: Array<GXRecord>,
    flatNodes: Array<GXNode>,
    parentNode: GXNode | null,
  ): Array<GXNode> | null {
    let nodes: Array<GXNode> | null = null;
    // 递归遍历
    if (layers.length > 0) {
      nodes = Array<GXNode>();
      // 获取数据属性
      const data = dataInfo['data'] as GXRecord;
      const event = dataInfo['event'] as GXRecord;
      const track = dataInfo['track'] as GXRecord;
      const animation = dataInfo['animation'] as GXRecord;
      // 遍历层级
      for (let i = 0; i < layers.length; i++) {

        let node: GXNode | null = null;

        const layer = layers[i];
        const nodeId = layer['id'] as string;

        // 嵌套模板
        if (GXNodeHelper.isNestTemplateType(layer)) {

          // 如果是容器的嵌套模板，节点信息需要和父节点做关联
          // @see GXScrollNode.setData
          if (parentNode && parentNode.isContainer) {
            const subItem = new GXTemplateItem(templateItem.bizId, nodeId);
            subItem.virtualStyleInfo = styleInfo[nodeId] as GXRecord;
            parentNode.extension.addSubItems(subItem)
          }
          // 如果是模板的嵌套模板，那么继续构建节点树
          else {

            const subItem = new GXTemplateItem(templateItem.bizId, nodeId);

            subItem.virtualStyleInfo = styleInfo[nodeId] as GXRecord;

            // 创建嵌套节点
            node = GXNodeTreeCreator.creatNodeTree(subItem, templateContext);

            // 虚拟数据
            if (node) {
              node.extension.virtualData = data[nodeId] as GXRecord;
            } else {
              throw new Error(`GXNodeTreeCreator recursionCreateNode create node error, ${JSON.stringify(templateItem)}`)
            }
          }
        } else {
          // 普通节点
          const nodeStyleInfo = styleInfo[nodeId] as GXRecord;
          let style = GXStyleHelper.creatStyle(nodeStyleInfo);
          let nodeType = GXNodeHelper.getNodeType(layer);
          node = GXNodeHelper.creatNode(nodeType, style);
          node.isContainer = GXNodeHelper.isContainer(layer)
          node.isTemplateType = GXNodeHelper.isTemplateType(layer);
          node.nodeId = nodeId;
          node.setLayerInfo(layer);
          node.setStyleInfo(nodeStyleInfo);
          // 扩展信息
          node.extension.type = layer['type'] as string;
          node.extension.subType = layer['sub-type'] as string;
          // 模板信息
          node.extension.layerInfo = layer;
          node.extension.styleInfo = nodeStyleInfo;
          // 数据
          if (data) {
            node.extension.data = data[nodeId] as GXRecord;
          }
          if (event) {
            node.extension.event = event[nodeId] as GXRecord;
          }
          if (track) {
            node.extension.track = track[nodeId] as GXRecord;
          }
          if (animation) {
            node.extension.animation = animation[nodeId] as GXRecord;
          }
          // 自定义 View 注入className
          // 使用iOS一样的key，防止被gxStudio导出时抹除
          if (nodeType == GXNodeType.custom) {
            (node as GXCustomViewNode).className = layer['view-class-ios'] as string;
          }

          // 其他属性
          node.extension.templateContext = templateContext;

          //
          const subLayers = layer['layers'] as Array<GXRecord>;
          if (subLayers && subLayers.length > 0) {
            const childNodes = GXNodeTreeCreator.recursionCreateNode(templateContext, templateItem, styleInfo, dataInfo, subLayers, flatNodes, node) as Array<GXNode>;
            for (let j = 0; j < childNodes.length; j++) {
              const childNode = childNodes[j];
              node?.addChild(childNode);
            }
          }
        }

        // 添加到数组
        if (node != null) {
          nodes.push(node);
          flatNodes.push(node);
          node.updateSpecialNodes();
        }
      }
    }
    // 返回当前层级的节点数组
    return nodes;
  }
}