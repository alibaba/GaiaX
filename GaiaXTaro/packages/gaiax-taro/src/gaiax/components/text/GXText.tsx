import { Text } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';

export interface GXTextState {

}

export interface GXTextProps {
  propStyle?: string | CSSProperties
  propKey?: string | number
  propDataValue?: string
}

export default class GXText extends React.Component<GXTextProps, GXTextState> {
  render() {
    const { propStyle, propKey, propDataValue } = this.props
    return <Text style={propStyle} key={propKey} >{propDataValue}</Text>;
  }
}
