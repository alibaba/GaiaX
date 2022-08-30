import { CommonEventFunction, View } from '@tarojs/components';
import React, { CSSProperties } from 'react';
import { GXNode } from '../../gaiax/GXNode';

export interface GXViewState {

}

export interface GXViewProps {
  onClick?: CommonEventFunction
  propStyle?: string | CSSProperties
  propGXNode: GXNode
}

export default class GXView extends React.Component<GXViewProps, GXViewState> {

  private handleClick(): void {
    this.props.onClick && this.props.onClick(arguments as any)
  }

  render() {
    const { propStyle, propGXNode } = this.props
    return <View
      style={propStyle}
      id={propGXNode.gxIdPath}
      onClick={this.handleClick.bind(this)}
    >
      {this.props.children}
    </View>;
  }
}
