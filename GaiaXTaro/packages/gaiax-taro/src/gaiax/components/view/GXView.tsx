import { View } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';

export interface GXViewState {

}

export interface GXViewProps {
  propStyle?: string | CSSProperties
  propKey?: string | number
  propChildArray?: ReactNode[]
}

export default class GXView extends React.Component<GXViewProps, GXViewState> {
  render() {
    const { propStyle, propKey, propChildArray } = this.props
    return <View style={propStyle} key={propKey} >
      {propChildArray}
    </View>;
  }
}
