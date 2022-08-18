import { Text, View } from '@tarojs/components';
import React, { CSSProperties } from 'react';
import classNames from 'classnames'

export interface GXIconFontTextState {

}

export interface GXIconFontTextProps {
  propStyle?: string | CSSProperties
  propDataValue?: string
}

export default class GXIconFontText extends React.Component<GXIconFontTextProps, GXIconFontTextState> {
  render() {
    const { propStyle, propDataValue } = this.props

    // 需要转义：https://blog.csdn.net/qq_26834399/article/details/105865440
    // const test = "\ue606"
    return <View
      className={classNames('iconfont')}
      style={propStyle}
    >{propDataValue}</View>
  }
}
