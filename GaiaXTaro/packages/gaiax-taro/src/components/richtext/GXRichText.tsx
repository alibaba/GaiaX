import { Text } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';

export interface GXRichTextState {

}

export interface GXRichTextProps {
  propStyle?: string | CSSProperties
  propDataValue?: string
}

export default class GXRichText extends React.Component<GXRichTextProps, GXRichTextState> {
  render() {
    const { propStyle, propDataValue } = this.props
    return <Text style={propStyle}  >{propDataValue}</Text>;
  }
}
