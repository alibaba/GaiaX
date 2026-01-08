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
import { hilog } from '@kit.PerformanceAnalysisKit';
import { Value, getTestExpressionList } from './testMock/testCase'
import { configAnalyzeIfNeeded, analyzeGetValue, JSGXValue } from 'gaiaxanalyze'

@Entry
@Component
struct Index {
  @State message: string = '表达式结果';
  @State expressionList: Array<Value> = getTestExpressionList();

  aboutToAppear(): void {
    configAnalyzeIfNeeded()
  }

  build() {
    Row() {
      Column() {
        Text(this.message)
          .fontColor('#ff0000')
          .padding({
            left: 8,
            right: 8,
            top: 4,
            bottom: 4
          })
          .margin({
            left: 8,
            right: 8,
            top: 4,
            bottom: 4
          })
          .width('90%')
          .borderWidth(1)
          .borderColor('#000000')
          .fontSize(25)
          .fontWeight(FontWeight.Bold)
          .onClick(() => {

          })
        ForEach(this.expressionList, (item: Value) => {
          Text(item.expression)
            .padding({
              left: 8,
              right: 8,
              top: 4,
              bottom: 4
            })
            .margin({
              left: 8,
              right: 8,
              top: 4,
              bottom: 4
            })
            .width('90%')
            .borderWidth(1)
            .borderColor('#000000')
            .height(44)
            .fontSize(14)
            .fontWeight(FontWeight.Bold)
            .onClick(() => {

              let result = analyzeGetValue(item.expression, item.source as object)
              if (Array.isArray(result)) {
                let resultIsArray = result as Array<object>
                this.message = `结果: 获取到了数组,数组长度${resultIsArray.length}`
              } else {
                this.message = `结果: ${result}`
              }
              hilog.info(0x0000, 'testTag', `gxAnalyzeGetValue:${result}`);
            })
        })
      }
      .width('100%')
    }
    .height('100%')
  }
}
