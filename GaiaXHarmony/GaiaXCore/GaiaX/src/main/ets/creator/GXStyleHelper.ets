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

import { GXRecord } from '../common/GXCommon';
import GXUtils from '../utils/GXUtils';
import {
  AlignContent,
  AlignItems,
  AlignSelf,
  Dim,
  DimAuto,
  DimPercent,
  DimPoints,
  Direction,
  Display,
  FlexDirection,
  FlexWrap,
  JustifyContent,
  PositionType,
  Style
} from 'gxstretch';

export default class GXStyleHelper {
  /**
   * 创建style
   * @param styleInfo
   * @returns Style对象
   */
  static creatStyle(styleInfo: GXRecord): Style {
    let style = new Style();
    if (styleInfo) {
      // 更新属性
      GXStyleHelper.updateStyle(style, styleInfo);
    }
    // 返回结果
    return style;
  }

  /**
   * 更新style属性
   * @param style
   * @param styleInfo
   */
  static updateStyle(style: Style, styleInfo: GXRecord): boolean {
    // 用于标记布局是否变化
    let flag = false;

    // margin
    const margin = styleInfo['margin'] as string;
    if (margin) {
      flag = true;
      GXStyleHelper.convertPadding(style, margin, margin, margin, margin);
    } else {
      const marginTop = styleInfo['margin-top'] as string;
      const marginLeft = styleInfo['margin-left'] as string;
      const marginBottom = styleInfo['margin-bottom'] as string;
      const marginRight = styleInfo['margin-right'] as string;
      if (marginTop != null || marginLeft != null || marginBottom != null || marginRight != null) {
        flag = true;
        GXStyleHelper.convertMargin(style, marginTop, marginLeft, marginBottom, marginRight);
      }
    }

    // padding
    const padding = styleInfo['padding'] as string;
    if (padding) {
      flag = true;
      GXStyleHelper.convertPadding(style, padding, padding, padding, padding);
    } else {
      const paddingTop = styleInfo['padding-top'] as string;
      const paddingLeft = styleInfo['padding-left'] as string;
      const paddingBottom = styleInfo['padding-bottom'] as string;
      const paddingRight = styleInfo['padding-right'] as string;
      if (paddingTop != null || paddingLeft != null || paddingBottom != null || paddingRight != null) {
        flag = true;
        GXStyleHelper.convertPadding(style, paddingTop, paddingLeft, paddingBottom, paddingRight);
      }
    }

    // width | height
    const width = styleInfo['width'] as string;
    const height = styleInfo['height'] as string;
    if (height != null || width != null) {
      flag = true;
      GXStyleHelper.convertSize(style, width, height);
    }

    // max-size
    const maxWidth = styleInfo['max-width'] as string;
    const maxHeight = styleInfo['max-height'] as string;
    if (maxWidth != null || maxHeight != null) {
      flag = true;
      GXStyleHelper.convertMaxSize(style, maxWidth, maxHeight);
    }

    // min-size
    const minWidth = styleInfo['min-width'] as string;
    const minHeight = styleInfo['min-height'] as string;
    if (minWidth != null || minHeight != null) {
      flag = true;
      GXStyleHelper.convertMinSize(style, minWidth, minHeight);
    }

    // positionType
    const position = styleInfo['position'] as string;
    if (position != null) {
      flag = true;
      style.positionType = GXStyleHelper.convertPositionType(position);
    }

    // position
    if (style.positionType == PositionType.Absolute) {
      const top = styleInfo['top'] as string;
      const left = styleInfo['left'] as string;
      const bottom = styleInfo['bottom'] as string;
      const right = styleInfo['right'] as string;
      if (top != null || left != null || bottom != null || right != null) {
        flag = true;
        GXStyleHelper.convertPosition(style, top, left, bottom, right);
      }
    }

    // direction
    const direction = styleInfo['direction'] as string;
    if (direction != null) {
      flag = true;
      style.direction = GXStyleHelper.convertDirection(direction);
    }

    // flex-direction
    const flexDirection = styleInfo['flex-direction'] as string;
    if (flexDirection != null) {
      flag = true;
      style.flexDirection = GXStyleHelper.convertFlexDirection(flexDirection);
    }

    // direction
    const display = styleInfo['display'] as string;
    if (display != null) {
      flag = true;
      style.display = GXStyleHelper.convertDisplay(display);
    }

    // align-items
    const alignItems = styleInfo['align-items'] as string;
    if (alignItems != null) {
      flag = true;
      style.alignItems = GXStyleHelper.convertAlignItems(alignItems);
    }

    // align-self
    const alignSelf = styleInfo['align-self'] as string;
    if (alignSelf != null) {
      flag = true;
      style.alignSelf = GXStyleHelper.convertAlignSelf(alignSelf);
    }

    // align-content
    const alignContent = styleInfo['align-content'] as string;
    if (alignContent != null) {
      flag = true;
      style.alignContent = GXStyleHelper.convertAlignContent(alignContent);
    }

    // justify-content
    const justifyContent = styleInfo['justify-content'] as string;
    if (justifyContent != null) {
      flag = true;
      style.justifyContent = GXStyleHelper.convertJustifyContent(justifyContent);
    }

    // flex-wrap
    const flexWrap = styleInfo['flex-wrap'] as string;
    if (flexWrap != null) {
      flag = true;
      style.flexWrap = GXStyleHelper.convertFlexWrap(flexWrap);
    }

    // flex-basis
    const flexBasis = styleInfo['flex-basis'] as string;
    if (flexBasis != null) {
      flag = true;
      style.flexBasis = GXStyleHelper.convertAutoValue(flexBasis);
    }

    // flex-shrink
    const flexShrink = styleInfo['flex-shrink'] as string;
    if (flexShrink != null) {
      const number = GXUtils.convertToNumber(flexShrink);
      if (number) {
        flag = true;
        style.flexShrink = number;
      }
    }

    // flex-grow
    const flexGrow = styleInfo['flex-grow'] as string | number;
    if (flexGrow != null) {
      const number = GXUtils.convertToNumber(flexGrow);
      if (number) {
        flag = true;
        style.flexGrow = number;
      }
    }

    // aspect-ratio
    const aspectRatio = styleInfo['aspect-ratio'] as string | number;
    if (aspectRatio != null) {
      const number = GXUtils.convertToNumber(aspectRatio);
      if (number) {
        flag = true;
        style.aspectRatio = number;
      }
    }

    // fit-content 自适应属性
    const fitContent = styleInfo['fit-content'] as boolean;
    if (fitContent != null) {
      style.fitContent = GXUtils.convertToBool(fitContent);
    }

    return flag;
  }

  // padding
  static convertPadding(style: Style, paddingTop?: string, paddingLeft?: string, paddingBottom?: string,
    paddingRight?: string) {
    let rect = style.padding;
    if (paddingTop != null) {
      rect.top = GXStyleHelper.convertValue(paddingTop);
    }
    if (paddingLeft != null) {
      rect.start = GXStyleHelper.convertValue(paddingLeft);
    }
    if (paddingBottom != null) {
      rect.bottom = GXStyleHelper.convertValue(paddingBottom);
    }
    if (paddingRight != null) {
      rect.end = GXStyleHelper.convertValue(paddingRight);
    }
    style.padding = rect;
  }

  // margin
  static convertMargin(style: Style, marginTop?: string, marginLeft?: string, marginBottom?: string,
    marginRight?: string) {
    let rect = style.margin;
    if (marginTop != null) {
      rect.top = GXStyleHelper.convertValue(marginTop);
    }
    if (marginLeft != null) {
      rect.start = GXStyleHelper.convertValue(marginLeft);
    }
    if (marginBottom != null) {
      rect.bottom = GXStyleHelper.convertValue(marginBottom);
    }
    if (marginRight != null) {
      rect.end = GXStyleHelper.convertValue(marginRight);
    }
    style.margin = rect;
  }

  // position
  static convertPosition(style: Style, top?: string, left?: string, bottom?: string, right?: string) {
    let rect = style.position;
    if (top != null) {
      rect.top = GXStyleHelper.convertValue(top);
    }
    if (left != null) {
      rect.start = GXStyleHelper.convertValue(left);
    }
    if (bottom != null) {
      rect.bottom = GXStyleHelper.convertValue(bottom);
    }
    if (right != null) {
      rect.end = GXStyleHelper.convertValue(right);
    }
    style.position = rect;
  }

  // size
  static convertSize(style: Style, width?: string, height?: string) {
    let size = style.size;
    if (width != null) {
      size.width = GXStyleHelper.convertAutoValue(width);
    }
    if (height != null) {
      size.height = GXStyleHelper.convertAutoValue(height);
    }
    style.size = size;
  }

  // max-size
  static convertMaxSize(style: Style, maxWidth?: string, maxHeight?: string) {
    let size = style.maxSize;
    if (maxWidth != null) {
      size.width = GXStyleHelper.convertAutoValue(maxWidth);
    }
    if (maxHeight != null) {
      size.height = GXStyleHelper.convertAutoValue(maxHeight);
    }
    style.maxSize = size;
  }

  // min-size
  static convertMinSize(style: Style, minWidth?: string, minHeight?: string) {
    let size = style.minSize;
    if (minWidth != null) {
      size.width = GXStyleHelper.convertAutoValue(minWidth);
    }
    if (minHeight != null) {
      size.height = GXStyleHelper.convertAutoValue(minHeight);
    }
    style.minSize = size;
  }

  // position
  static convertPositionType(position: string): PositionType {
    let value = PositionType.Relative;
    if (position == 'absolute') {
      value = PositionType.Absolute;
    }
    return value;
  }

  // direction
  static convertDirection(direction: string): Direction {
    let value: Direction;
    switch (direction) {
      case 'ltr':
        value = Direction.LTR
        break;
      case 'rtl':
        value = Direction.RTL
        break;
      default:
        value = Direction.Inherit
        break;
    }
    return value;
  }

  // flex-direction
  static convertFlexDirection(flexDirection: string): FlexDirection {
    let value: FlexDirection;
    switch (flexDirection) {
      case 'row':
        value = FlexDirection.Row;
        break;
      case 'column':
        value = FlexDirection.Column;
        break;
      case 'row-reverse':
        value = FlexDirection.RowReverse;
        break;
      case 'column-reverse':
        value = FlexDirection.ColumnReverse;
        break;
      default:
        value = FlexDirection.Row;
        break;
    }
    return value;
  }

  // flex-wrap
  static convertFlexWrap(flexWrap: string): FlexWrap {
    let value: FlexWrap;
    switch (flexWrap) {
      case 'wrap':
        value = FlexWrap.Wrap;
        break;
      case 'no-wrap':
        value = FlexWrap.NoWrap;
        break;
      case 'wrap-reverse':
        value = FlexWrap.WrapReverse;
        break;
      default:
        value = FlexWrap.NoWrap;
        break;
    }
    return value;
  }

  // display
  static convertDisplay(display: string): Display {
    let value = Display.Flex;
    if (display == 'none') {
      value = Display.None;
    }
    return value;
  }

  // align-self
  static convertAlignSelf(alignSelf: string): AlignSelf {
    let value = AlignSelf.Auto;
    switch (alignSelf) {
      case 'flex-start':
        value = AlignSelf.FlexStart;
        break;
      case 'flex-end':
        value = AlignSelf.FlexEnd;
        break;
      case 'center':
        value = AlignSelf.Center;
        break;
      case 'stretch':
        value = AlignSelf.Stretch;
        break;
      case 'baseline':
        value = AlignSelf.Baseline;
        break;
      default:
        value = AlignSelf.Auto;
    }
    return value;
  }

  // align-items
  static convertAlignItems(alignItems: string): AlignItems {
    let value: AlignItems;
    switch (alignItems) {
      case 'flex-start':
        value = AlignItems.FlexStart;
        break;
      case 'flex-end':
        value = AlignItems.FlexEnd;
        break;
      case 'center':
        value = AlignItems.Center;
        break;
      case 'baseline':
        value = AlignItems.Baseline;
        break;
      case 'stretch':
        value = AlignItems.Stretch;
        break;
      default:
        value = AlignItems.Stretch;
    }
    return value;
  }

  // align-content
  static convertAlignContent(alignContent: string): AlignContent {
    let value: AlignContent;
    switch (alignContent) {
      case 'flex-start':
        value = AlignContent.FlexStart;
        break;
      case 'flex-end':
        value = AlignContent.FlexEnd;
        break;
      case 'center':
        value = AlignContent.Center;
        break;
      case 'stretch':
        value = AlignContent.Stretch;
        break;
      case 'space-between':
        value = AlignContent.SpaceBetween;
        break;
      case 'space-around':
        value = AlignContent.SpaceAround;
        break;
      default:
        value = AlignContent.FlexStart;
    }
    return value;
  }

  // justify-content
  static convertJustifyContent(justifyContent: string): JustifyContent {
    let value: JustifyContent;
    switch (justifyContent) {
      case 'flex-start':
        value = JustifyContent.FlexStart;
        break;
      case 'flex-end':
        value = JustifyContent.FlexEnd;
        break;
      case 'center':
        value = JustifyContent.Center;
        break;
      case 'space-between':
        value = JustifyContent.SpaceBetween;
        break;
      case 'space-around':
        value = JustifyContent.SpaceAround;
        break;
      case 'space-evenly':
        value = JustifyContent.SpaceEvenly;
        break;
      default:
        value = JustifyContent.FlexStart;
    }
    return value;
  }

  // 获取基础布局属性，默认值为auto
  static convertSimpleValue(value: number | string | null, defaultValue = 0): number {
    if (typeof value === 'number') {
      return value;
    } else if (typeof value === 'string') {
      if (value.endsWith('px')) {
        //计算px
        const tmpValue = value.substring(0, value.length - 2);
        const number = GXUtils.convertToNumber(tmpValue) ?? 0;
        return number;
      } else if (value.endsWith('pt')) {
        // 计算 pt = px * ratio
        const ratio = 1.0;
        const tmpValue = value.substring(0, value.length - 2);
        const number = GXUtils.convertToNumber(tmpValue) ?? 0;
        return number * ratio;
      } else {
        const number = GXUtils.convertToNumber(value) ?? 0;
        return number;
      }
    } else {
      return defaultValue;
    }
  }

  // 获取基础布局属性，默认值为undefined
  static convertValue(value: number | string): Dim {
    return GXStyleHelper.convertToDim(value, false);
  }

  // 获取基础布局属性，默认值为auto
  static convertAutoValue(value: number | string): Dim {
    return GXStyleHelper.convertToDim(value, true);
  }

  // 具体实现
  private static convertToDim(value: number | string, isAuto: boolean): Dim {
    if (typeof value === 'number') {
      return new DimPoints(value)

    } else if (typeof value === 'string' && value != 'null') {
      if (value.endsWith('px')) {
        //计算px
        const tmpValue = value.substring(0, value.length - 2);
        const number = GXUtils.convertToNumber(tmpValue) ?? 0;
        return new DimPoints(number)

      } else if (value.endsWith('pt')) {
        // 计算 pt = px * ratio
        const ratio = 1.0;
        const tmpValue = value.substring(0, value.length - 2);
        const number = GXUtils.convertToNumber(tmpValue) ?? 0;
        return new DimPoints(number * ratio)

      } else if (value.endsWith('%')) {
        // 计算 %
        const tmpValue = value.substring(0, value.length - 1);
        const number = GXUtils.convertToNumber(tmpValue) ?? 0;
        const percent = number / 100.0;
        return new DimPercent(percent)

      } else if (value.endsWith('auto')) {
        // 计算 auto
        return new DimAuto();

      } else {
        // 兼容
        const number = GXUtils.convertToNumber(value) ?? 0;
        if (number == 0) {
          return isAuto ? new DimAuto() : new DimPoints(0);
        } else {
          return new DimPoints(number)
        }
      }

    } else {
      // 默认逻辑处理
      return isAuto ? new DimAuto() : new DimPoints(0);
    }
  }

  /**
   * 将系统 graphics 使用的weight枚举，替换为ts层weight
   * 0:100
   * 1:200
   * 2:300
   * ...
   * @param value
   * @returns
   */
  static convertSystemWeight(value: number | string): number {
    if (value === 0 || value === "0") {
      return 100;
    }
    if (value === 1 || value === "1") {
      return 200;
    }
    if (value === 2 || value === "2") {
      return 300;
    }
    if (value === 3 || value === "3") {
      return 400;
    }
    if (value === 4 || value === "4") {
      return 500;
    }
    if (value === 5 || value === "5") {
      return 600;
    }
    if (value === 6 || value === "6") {
      return 700;
    }
    if (value === 7 || value === "7") {
      return 800;
    }
    if (value === 8 || value === "8") {
      return 900;
    }
    if (typeof value === "number") {
      return value;
    } else {
      return Number(value).valueOf();
    }
  }
}