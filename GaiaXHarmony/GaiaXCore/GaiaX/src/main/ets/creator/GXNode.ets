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

import { GXEventType, GXNodeType, GXRecord } from '../common/GXCommon';
import GXNodeExtension from '../node/GXNodeExtension';
import GXDataParser from '../binding/GXDataParser';
import { Layout, Node } from 'gxstretch';
import GXEvent from '../model/GXEvent';
import GXTrack from '../model/GXTrack';

export default class GXNode extends Node {
  // 节点扩展信息
  extension: GXNodeExtension = new GXNodeExtension();
  // 节点Id
  nodeId: string | null = null;
  // flatNodes
  flatNodes?: Array<GXNode>;
  // 是否为模板类型（根模板 | 嵌套子模板）
  isTemplateType = false;
  // 是否为容器模板
  isContainer = false;
  // 计算之后的布局信息
  x: number = 0;
  y: number = 0;
  width: number = 0;
  height: number = 0;

  // 埋点响应
  trackEvent?: GXTrack;
  // 事件响应
  clickEvent?: GXEvent;
  scrollEvent?: GXEvent;

  // 是否为根节点
  isRootNode(): boolean {
    const parentNode = this.parent;
    if (parentNode) {
      return false;
    }
    return true;
  }

  // 节点类型
  nodeType(): GXNodeType {
    return GXNodeType.view;
  }

  // 是否需要绑定数据
  shouldBinding(): boolean {
    if (this.extension.data
      || this.extension.event
      || this.extension.track
      || this.extension.animation
      || this.extension.virtualData) {
      return true;
    }
    return false;
  }

  // 递归绑定布局
  applyLayout(layout: Layout) {
    // 当前节点布局，暂时不做节点拍平逻辑
    this.x = layout.x;
    this.y = layout.y;
    this.width = layout.width;
    this.height = layout.height;
    // 递归子节点
    for (let index = 0; index < this.children.length; index++) {
      const childNode: GXNode = this.children[index] as GXNode;
      // let childLayout: Layout = layout.children[index];
      let childLayout: Layout = (index < layout.children.length) ? layout.children[index] : null;
      if (childNode && childLayout != null) {
        childNode.applyLayout(childLayout);
      }
    }
  }


  // MARK: - 绑定数据 | 事件 | 埋点

  // 递归绑定数据
  applyData(data: GXRecord) {
    // 绑定数据
    if (this.extension.data != null || this.extension.virtualExtend != null) {
      let result = GXDataParser.parseData(this.extension.data, data);
      result = this.mergeExtendWithResult(result);
      if (result != null) {
        this.setData(result);
      }
    }

    // 绑定事件
    if (this.extension.event) {
      const event = GXDataParser.parseData(this.extension.event, data);
      if (event != null) {
        this.bindEvent(event);
      }
    }

    // 绑定埋点
    if (this.extension.track) {
      const track = GXDataParser.parseData(this.extension.track, data);
      if (track != null) {
        this.bindTrack(track);
      }
    }

    // 绑定动画
    if (this.extension.animation) {
      const animation = GXDataParser.parseData(this.extension.animation, data);
      if (animation != null) {
        this.bindAnimation(animation);
      }
    }
  }

  // 合并extend
  private mergeExtendWithResult(data: GXRecord | null) {
    let virtualExtend = this.extension.virtualExtend;
    if (virtualExtend != null) {
      if (data == null) {
        //为空的话直接赋值
        let result: GXRecord = {};
        result['extend'] = virtualExtend;
        return result;
      } else {
        let tmpExtend = data['extend'] as GXRecord;
        if (tmpExtend != null) {
          //合并extend
          Object.entries(virtualExtend).forEach(item => {
            tmpExtend[item[0]] = item[1];
          });
        } else {
          tmpExtend = virtualExtend;
        }
        data['extend'] = tmpExtend;
        return data;
      }
    } else {
      // 不存在extend直接返回
      return data;
    }
  }

  // 绑定数据
  setData(dataInfo: GXRecord) {
    // 数据信息 & 更新样式
  }

  private bindEvent(eventInfo: GXRecord) {
    // 创建event
    let event = this.clickEvent;
    if (event == null) {
      event = new GXEvent();
    }
    event.nodeId = this.nodeId;
    event.eventParams = eventInfo;
    // 赋值
    this.clickEvent = event;
  }

  private bindTrack(trackInfo: GXRecord) {
    // 创建track
    let track = this.trackEvent;
    if (track == null) {
      track = new GXTrack();
    }
    track.nodeId = this.nodeId;
    track.trackParams = trackInfo;
    // 赋值
    this.trackEvent = track;
  }

  bindAnimation(animationInfo: GXRecord) {
    // 子类实现
  }


  // MARK: - 执行事件
  handleEvent(event?: GXEvent) {
    const templateData = this.extension.templateContext?.templateData;
    if (templateData != null && event != null) {
      if (event.eventType == GXEventType.scroll || event.eventType == GXEventType.scrollEnd) {
        const eventCallback = templateData.scrollEventCallback;
        if (eventCallback) {
          eventCallback(event);
        }
      } else {
        const eventCallback = templateData.clickEventCallback;
        if (eventCallback) {
          eventCallback(event);
        }
      }
    }
  }

  getHitTestMode(): HitTestMode {
    if (this.clickEvent != null) {
      return HitTestMode.Default
    } else {
      return HitTestMode.None
    }
  }

  // MARK - 布局相关

  // 设置样式信息
  setStyleInfo(styleInfo: GXRecord) {
    // 子类实现
  }

  // 设置层级相关的信息
  setLayerInfo(layerInfo: GXRecord) {
    // 子类实现
  }

  // 更新特殊布局（fit-content | grid等）
  updateSpecialLayout() {

  }

  // 是否需要加到context中的specialNodes
  updateSpecialNodes() {

  }
}
