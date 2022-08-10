import { Text } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';

export interface GXTextState {

}

export interface GXTextProps {
  propStyle?: string | CSSProperties
  propDataValue?: string
}

export default class GXText extends React.Component<GXTextProps, GXTextState> {
  render() {
    const { propStyle, propDataValue } = this.props
    return <Text style={propStyle} >{propDataValue}</Text>;
  }
}
