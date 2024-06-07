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
import GXRenderManager from '../binding/GXRenderManager';
import GXScrollNode from '../node/GXScrollNode';
import { GXComponent } from './GXComponent';
import { Size } from 'gxstretch';
import hilog from '@ohos.hilog';

@Component
export default struct GXScrollView {
  @ObjectLink gxContext: GXTemplateContext;
  // 节点属性
  @ObjectLink node: GXScrollNode;
  scroller: Scroller = new Scroller()
  dataSource: GXScrollDataSource = new GXScrollDataSource();
  private keyGenerator = (item: GXTemplateContext, index: number): string => {
    let ret: string = index + "-" + (item.templateItem.templateId ?? '');
    hilog.debug(0x0000, 'GXC', `GXScrollView keyGenerator ret=${ret}`);
    return ret;
  }
  private scrollHeight: Length = "auto";

  aboutToAppear(): void {
    this.dataSource.list = this.node.items;
    this.scrollHeight = this.calculateScrollHeight();
    hilog.debug(0x0000, 'GXC', `GXScrollView aboutToAppear scrollHeight=${this.scrollHeight}`);
  }

  calculateScrollHeight(): Length {
    if (this.node.height > 0) {
      return this.node.height;
    }

    if (!this.node.extension.subTemplates || this.node.extension.subTemplates.length == 0) {
      hilog.error(0x0000, 'GXC', `GXScrollView calculateScrollHeight subTemplates.length = 0`);
      return 'auto';
    }

    if (this.node.items.length == 0) {
      hilog.error(0x0000, 'GXC', `GXScrollView calculateScrollHeight items.length = 0`);
      return 'auto';
    }

    // TODO 此处可以做性能优化
    // 需要处理的情况：
    // 一个坑位类型，高度一样
    // 一个坑位类型，高度不一样
    // 多个坑位类型，高度一样
    // 多个坑位类型，高度不一样.
    let measureSize = this.getScrollItemMeasureSize();
    let lastHeight = 0;
    for (let i = 0; i < this.node.items.length; i++) {
      let itemContext = this.node.items[i];
      itemContext.measureSize = measureSize;
      let height = GXRenderManager.calculateSizeWithContext(itemContext, true)?.[1] ?? 0;
      lastHeight = Math.max(lastHeight, height);
    }
    return lastHeight;
  }

  getScrollItemMeasureSize(): Size<number> {
    let size = this.gxContext.measureSize
    let padding = this.node.getStyle().padding;
    let width = size.width - padding.start.value - padding.end.value;
    return new Size(width, NaN);
  }

  aboutToDisappear(): void {
    this.node.free();
  }

  onPageShow(): void {

  }

  onPageHide(): void {

  }

  build() {
    List({ space: this.node.itemSpace, initialIndex: this.node.index, scroller: this.scroller }) {
      LazyForEach(
        this.dataSource,
        (context: GXTemplateContext, index) => {
          ListItem() {
            GXComponent({ templateContext: context })
          }
        },
        (context: GXTemplateContext, index) => this.keyGenerator(context, index)
      )
    }
    .id(this.node.nodeId)
    .position({ x: this.node.x, y: this.node.y })
    .width(this.node.width)
    .height(this.scrollHeight)
    // .height(this.node.height)
    .backgroundColor(this.node.backgroundColor)
    .listDirection(this.node.scrollDirection) // 排列方向
    .contentStartOffset(this.node.startOffset)
    .contentEndOffset(this.node.endOffset)
    .edgeEffect(EdgeEffect.Spring) // 滑动到边缘无效果
    .cachedCount(3) //cachedCount表示屏幕外List/Grid预加载item的个数
    .onDidScroll((xOffset: number, yOffset: number) => {
      console.info(xOffset + ' ' + yOffset)
    })
    .alignListItem(this.node.gravity)
    .scrollBar(BarState.Off)
    .onScrollStop(() => {
      console.info('Scroll Stop')
    })
  }
}


class GXScrollDataSource implements IDataSource {
  list: GXTemplateContext[] = [];

  public totalCount(): number {
    return this.list.length
  }

  public getData(index: number): GXTemplateContext {
    let data: GXTemplateContext = this.list[index];
    hilog.debug(0x0000, 'GXC', `GXScrollDataSource getData index=${index} data=${JSON.stringify(data)}`);
    return data
  }

  registerDataChangeListener(listener: DataChangeListener): void {
  }

  unregisterDataChangeListener(listener: DataChangeListener): void {
  }
}
