import { View } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';

export interface GXScrollState {

}

export interface GXScrollProps {
    propStyle?: string | CSSProperties
}

export default class GXScroll extends React.Component<GXScrollProps, GXScrollState> {
    render() {
        const { propStyle } = this.props
        return <View style={propStyle} >
        </View>;
    }
}
