import { CommonEventFunction, Image, ImageProps } from '@tarojs/components';
import React, { CSSProperties } from 'react';

export interface GXImageState {

}

export interface GXImageProps {
  onClick?: CommonEventFunction
  propStyle?: string | CSSProperties
  propDataValue?: string
  propMode?: string
}

export default class GXImage extends React.Component<GXImageProps, GXImageState> {

  private handleClick(): void {
    this.props.onClick && this.props.onClick(arguments as any)
  }

  private getMode(srcMode: string): keyof ImageProps.Mode {
    if (srcMode == 'scaleToFill') {
      return 'scaleToFill';
    } else if (srcMode == 'aspectFit') {
      return 'aspectFit';
    } else if (srcMode == 'aspectFill') {
      return 'aspectFill';
    } else if (srcMode == 'top') {
      return 'top';
    } else if (srcMode == 'bottom') {
      return 'bottom';
    }
    else if (srcMode == 'center') {
      return 'center';
    }
    else if (srcMode == 'left') {
      return 'left';
    }
    else if (srcMode == 'right') {
      return 'right';
    }
    else if (srcMode == 'top left') {
      return 'top left';
    }
    else if (srcMode == 'top right') {
      return 'top right';
    }
    else if (srcMode == 'bottom left') {
      return 'bottom left';
    }
    else if (srcMode == 'bottom right') {
      return 'bottom right';
    }
    return 'scaleToFill';
  }

  render() {
    const { propStyle, propDataValue, propMode } = this.props;
    return <Image style={propStyle}
      mode={this.getMode((propMode || 'scaleToFill'))}
      onClick={this.handleClick.bind(this)}
      src={propDataValue} />;


  }
}
