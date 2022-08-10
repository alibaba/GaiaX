import { View } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';

export interface GXViewState {

}

export interface GXViewProps {
  propStyle?: string | CSSProperties
  propKey?: string | number
}

export default class GXView extends React.Component<GXViewProps, GXViewState> {
  render() {
    const { propStyle, propKey } = this.props
    return <View style={propStyle} key={propKey} >
      {this.props.children}
    </View>;
  }
}
