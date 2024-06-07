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

import GXTemplateItem from '../../context/GXTemplateItem';
import GXTemplateData from '../../context/GXTemplateData';
import GXTemplateEngine from '../../GXTemplateEngine';
import GXContext from '../../utils/GXContext';
import { GXComponent } from '../GXComponent';
import { Size } from 'gxstretch';
import { getJson } from './Utils';

@Preview
@Component
export struct MainPage {
  @State message: string = 'Hello World';
  // templateItem = new GXTemplateItem('template', 'gx-grid', '0')
  // templateItem = new GXTemplateItem('template', 'gx-grid', '0')
  // templateItem = new GXTemplateItem('template', 'gx-slider', '0')
  // templateItem = new GXTemplateItem('template', 'gx-scroll', '0')
  // templateItem = new GXTemplateItem('template', 'yk-nest-item', '0',)
  templateItem = new GXTemplateItem('template', 'yk-topic-item', '0')
  // templateItem = new GXTemplateItem('template', 'richTextItem', '0')
  templateData = new GXTemplateData();
  measureSize: Size<number> = new Size(NaN, NaN);
  cWidth = 0;

  async aboutToAppear() {
    GXTemplateEngine.instance.init()

    let sWidth = GXContext.shared().screenWidth;
    let sScale = GXContext.shared().screenScale;
    this.message = 'info.width = ' + sWidth;
    const scale = sScale ?? 1;

    this.templateItem = new GXTemplateItem('template', 'template_scroll_multi_type_two_item_show_one_item', '0')
    this.templateData.jsonData = await getJson(getContext().resourceManager, "data/template_scroll_multi_type_two_item_show_one_item.json")

    // this.templateItem = new GXTemplateItem('template', 'template_scroll_load_more', '0')
    // this.templateData.jsonData = await getJson(getContext().resourceManager, "data/template_scroll_load_more.json")

    // this.templateItem = new GXTemplateItem('template', 'gx-scroll', '0')
    // this.templateData.jsonData = await getJson(getContext().resourceManager, "data/scroll.json")
    // this.templateData.jsonData = await getJson(getContext().resourceManager, "data/item.json")

    // this.templateData.jsonData = {"title": "这是一个@高亮@内容，你看对么", "desc": "这是一个副标题"}

    // size
    this.measureSize = new Size(sWidth / scale, NaN);
    this.cWidth = sWidth / scale;
  }

  build() {
    Column() {

      Text(this.message)
        .backgroundColor('red')
        .margin(12)
        .textAlign(TextAlign.Start)

      GXComponent({
        templateItem: this.templateItem,
        templateData: this.templateData,
        measureSize: this.measureSize
      })
        .backgroundColor('transparent')
    }
    .alignItems(HorizontalAlign.Start)
    .width('100%')
    .backgroundColor('green')
  }
}
