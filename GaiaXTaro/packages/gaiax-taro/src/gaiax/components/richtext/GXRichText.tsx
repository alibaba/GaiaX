import { Text } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';

export interface GXRichTextState {

}

export interface GXRichTextProps {
  propStyle?: string | CSSProperties
  propKey?: string | number
  propDataValue?: string
}

export default class GXRichText extends React.Component<GXRichTextProps, GXRichTextState> {
  render() {
    const { propStyle, propKey, propDataValue } = this.props
    return <Text style={propStyle} key={propKey} >{propDataValue}</Text>;
  }
}
