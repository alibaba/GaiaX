import { CommonEventFunction, Text } from '@tarojs/components';
import React, { CSSProperties } from 'react';
import { GXNode } from '../../gaiax/GXNode';

export interface GXTextState {

}

export interface GXTextProps {
  onClick?: CommonEventFunction
  propStyle?: string | CSSProperties
  propDataValue?: string
  propGXNode: GXNode
}

export default class GXText extends React.Component<GXTextProps, GXTextState> {

  private handleClick(): void {
    this.props.onClick && this.props.onClick(arguments as any)
  }

  render() {
    const { propGXNode, propStyle, propDataValue } = this.props
    return <Text
      id={propGXNode.gxIdPath}
      style={propStyle}
      onClick={this.handleClick.bind(this)}
    >
      {propDataValue}
    </Text>;
  }
}
