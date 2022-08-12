import { Text } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';

export interface GXIconFontTextState {

}

export interface GXIconFontTextProps {
  propStyle?: string | CSSProperties
  propDataValue?: string
}

export default class GXIconFontText extends React.Component<GXIconFontTextProps, GXIconFontTextState> {
  render() {
    const { propStyle, propDataValue } = this.props
    return <Text style={propStyle}  >{propDataValue}</Text>;
  }
}
