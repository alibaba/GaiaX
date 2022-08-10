import { Image } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';

export interface GXImageState {

}

export interface GXImageProps {
  propStyle?: string | CSSProperties
  propDataValue?: string
}

export default class GXImage extends React.Component<GXImageProps, GXImageState> {
  render() {
    const { propStyle, propDataValue } = this.props
    return <Image style={propStyle} src={propDataValue} />;
  }
}
