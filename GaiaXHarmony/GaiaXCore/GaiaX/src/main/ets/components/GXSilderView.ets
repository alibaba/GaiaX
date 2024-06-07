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
import GXSliderNode from '../node/GXSiderNode';
import { GXComponent } from './GXComponent';
import { Size } from 'gxstretch';
import hilog from '@ohos.hilog';

@Component
export default struct GXSliderView {
  // @StorageProp('currentBreakpoint') currentBreakpoint: string = 'sm'; //sm, md, lg
  private swiperController: SwiperController = new SwiperController()
  private itemSize: Size<number> = new Size(NaN, NaN)
  // 节点属性
  @Prop node: GXSliderNode;
  private keyGenerator = (item: GXTemplateContext, index: number): string => {
    let ret: string = index + "-" + (item.templateItem.templateId ?? '');
    hilog.debug(0x0000, 'GXC', `GXSliderView keyGenerator ret=${ret}`);
    return ret;
  }

  aboutToAppear(): void {
    const left = this.node.contentInset.left as number ?? 0;
    const right = this.node.contentInset.right as number ?? 0;
    const tmpWidth = this.node.width - (left + right)
    this.itemSize.width = tmpWidth;
  }

  aboutToDisappear(): void {

  }

  onPageShow(): void {

  }

  onPageHide(): void {

  }

  build() {
    Swiper(this.swiperController) {
      ForEach(
        this.node.items,
        (context: GXTemplateContext) => {
          Stack() {
            GXComponent({ templateContext: context, measureSize: this.itemSize })
          }
        },
        (item: GXTemplateContext, index) => this.keyGenerator(item, index))
    }
    .id(this.node.nodeId)
    .position({ x: this.node.x, y: this.node.y })
    .width(this.node.width)
    .height('auto')
    .padding(this.node.contentInset)
    .itemSpace(this.node.itemSpace)
    .autoPlay(this.node.autoScroll)
    .interval(this.node.interval)
    .loop(this.node.infiniteLoop)
    .cachedCount(3)
    .duration(400)
    .curve(Curve.Linear) //swiper的动画曲线，默认为淡入淡出曲线
    .effectMode(EdgeEffect.Spring) // 滑动效果，默认值：EdgeEffect.Spring
    .displayMode(SwiperDisplayMode.AUTO_LINEAR)
    // .displayCount(this.currentBreakpoint === 'sm' ? 1 : (this.currentBreakpoint === 'md' ? 2 : 3))
    // .displayCount(2)
    // .indicator(false)
    .onChange((index: number) => {
      console.info(index.toString())
    })
  }
}