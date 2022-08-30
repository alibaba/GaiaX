import { CommonEventFunction, Text, View } from '@tarojs/components';
import React, { CSSProperties } from 'react';
import classNames from 'classnames'
import { GXNode } from '../../gaiax/GXNode';

export interface GXIconFontTextState {

}

export interface GXIconFontTextProps {
  onClick?: CommonEventFunction
  propStyle?: string | CSSProperties
  propDataValue?: string
  propGXNode: GXNode
}

export default class GXIconFontText extends React.Component<GXIconFontTextProps, GXIconFontTextState> {

  private handleClick(): void {
    this.props.onClick && this.props.onClick(arguments as any)
  }

  render() {
    const { propStyle, propDataValue, propGXNode } = this.props

    // 需要转义：https://blog.csdn.net/qq_26834399/article/details/105865440
    // const test = "\ue606"
    return <View
      className={classNames('iconfont')}
      style={propStyle}
      id={propGXNode.gxIdPath}
      onClick={this.handleClick.bind(this)}
    >{propDataValue}</View>
  }
}
