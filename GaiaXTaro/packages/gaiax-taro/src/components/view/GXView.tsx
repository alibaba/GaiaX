import { CommonEventFunction, View } from '@tarojs/components';
import React, { CSSProperties } from 'react';

export interface GXViewState {

}

export interface GXViewProps {
  onClick?: CommonEventFunction
  propStyle?: string | CSSProperties
}

export default class GXView extends React.Component<GXViewProps, GXViewState> {

  private handleClick(): void {
    this.props.onClick && this.props.onClick(arguments as any)
  }

  render() {
    const { propStyle } = this.props
    return <View
      style={propStyle}
      onClick={this.handleClick.bind(this)}
    >
      {this.props.children}
    </View>;
  }
}
