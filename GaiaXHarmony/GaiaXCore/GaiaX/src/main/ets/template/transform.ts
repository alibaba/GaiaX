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

export function parseCSS(cssString: string): Object {
  const cssJSON = new Object();
  // 首先移除所有的注释
  let noComments = cssString.replace(/\/\*[\s\S]*?\*\//g, '');
  // 干净地分割选择器和样式
  let blocks = noComments.split('}').map(block => block.trim()).filter(block => block.length);

  for (let block of blocks) {
    let styleBlock = block.split('{').map(part => part.trim());
    let selector = ''
    let stylesString = ''
    if (styleBlock[0].length > 0) {
      selector = styleBlock[0]
    }
    if (styleBlock[1].length > 0) {
      stylesString = styleBlock[1]
    }

    // 现在处理样式字符串
    let stylesArray = stylesString.split(';').map(style => style.trim()).filter(style => style.length);
    let stylesMap = new Object();

    for (let style of stylesArray) {
      let propertyName = ''
      let propertyValue = ''
      let propertyBlock = style.split(':').map(part => part.trim());
      if (propertyBlock[0].length > 0) {
        propertyName = propertyBlock[0]
      }
      if (propertyBlock[1].length > 0) {
        propertyValue = propertyBlock[1]
      }
      stylesMap[propertyName] = propertyValue
    }
    cssJSON[selector.replace(/#/g, "")] = stylesMap
  }

  return cssJSON;
}