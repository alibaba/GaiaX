import { View } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';

export interface GXViewState {

}

export interface GXViewProps {
  propStyle?: string | CSSProperties
}

export default class GXView extends React.Component<GXViewProps, GXViewState> {
  render() {
    const { propStyle } = this.props
    return <View style={propStyle} >
      {this.props.children}
    </View>;
  }
}
