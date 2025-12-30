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
import GXGridNode from '../node/GXGridNode';
import { GXComponent } from './GXComponent';
import { Size } from 'gxstretch';
import hilog from '@ohos.hilog';

@Component
export default struct GXGridView {
  // 节点属性
  @Prop node: GXGridNode;
  scroller: Scroller = new Scroller()
  private itemSize: Size<number> = new Size(NaN, NaN)
  private columns: number = 1;
  private gridHeight: number = 0;
  private keyGenerator = (item: GXTemplateContext, index: number): string => {
    let ret: string = index + "-" + (item.templateItem.templateId ?? '');
    hilog.debug(0x0000, 'GXC', `GXGridView keyGenerator ret=${ret}`);
    return ret;
  }

  aboutToAppear(): void {
    this.columns = this.node.columns;
    this.itemSize.width = this.calculateColumn();
    this.gridHeight = this.calculateGridHeight();
  }

  aboutToDisappear(): void {

  }

  // 计算列数
  private calculateColumn(): number {
    const gridWidth = this.node.width;
    const left = this.node.contentInset.left as number ?? 0;
    const right = this.node.contentInset.right as number ?? 0;
    const columns = this.node.columns;
    const columSpace = this.node.columnSpace;
    return (gridWidth - (left + right) - columSpace * (columns - 1)) / 3
  }

  private calculateGridHeight(): number {
    if (this.node.height > 0) {
      return this.node.height;
    } else {
      const items = this.node.items;
      if (items.length > 0) {
        const columns = this.node.columns;
        const rowSpace = this.node.rowSpace;
        const row = Math.floor(items.length / columns) + (items.length % columns == 0 ? 0 : 1)
        // 计算
        const context = items[0];
        context.measureSize = this.itemSize;
        const itemHeight = GXRenderManager.calculateSizeWithContext(context, false)?.[1] ?? 0;
        return itemHeight * row + rowSpace * (row - 1);
      }
      return 0;
    }
  }

  build() {
    Grid(this.scroller) {
      ForEach(
        this.node.items,
        (context: GXTemplateContext) => {
          GridItem() {
            GXComponent({ templateContext: context, measureSize: this.itemSize })
          }
        },
        (item: GXTemplateContext, index) => this.keyGenerator(item, index)
      )
    }
    .id(this.node.nodeId)
    .columnsTemplate('1fr '.repeat(this.columns))
    .columnsGap(this.node.columnSpace)
    .rowsGap(this.node.rowSpace)
    .backgroundColor(this.node.backgroundColor)
    .width(this.node.width)
    .height(this.gridHeight) // .height('auto')
    .padding(this.node.contentInset)
    // .enableScrollInteraction(false) // scrollEnable
    .onScrollIndex((first: number) => {
    })
  }
}


