import { CommonEventFunction, Image } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';

export interface GXImageState {

}

export interface GXImageProps {
  onClick?: CommonEventFunction
  propStyle?: string | CSSProperties
  propDataValue?: string
}

export default class GXImage extends React.Component<GXImageProps, GXImageState> {

  private handleClick(): void {
    this.props.onClick && this.props.onClick(arguments as any)
  }

  render() {
    const { propStyle, propDataValue } = this.props
    return <Image style={propStyle}
      onClick={this.handleClick.bind(this)}
      src={propDataValue} />;
  }
}
