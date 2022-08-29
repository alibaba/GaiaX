import { CommonEventFunction, Text } from '@tarojs/components';
import React, { CSSProperties } from 'react';

export interface GXTextState {

}

export interface GXTextProps {
  onClick?: CommonEventFunction
  propStyle?: string | CSSProperties
  propDataValue?: string
}

export default class GXText extends React.Component<GXTextProps, GXTextState> {

  private handleClick(): void {
    this.props.onClick && this.props.onClick(arguments as any)
  }

  render() {
    const { propStyle, propDataValue } = this.props
    return <Text
      style={propStyle}
      onClick={this.handleClick.bind(this)}
    >
      {propDataValue}
    </Text>;
  }
}
