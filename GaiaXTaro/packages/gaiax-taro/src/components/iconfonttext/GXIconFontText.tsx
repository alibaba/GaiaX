import { Text, View } from '@tarojs/components';
import React, { CSSProperties } from 'react';
import classNames from 'classnames'

export interface GXIconFontTextState {

}

export interface GXIconFontTextProps {
  propStyle?: string | CSSProperties
  propDataValue?: string
}

export default class GXIconFontText extends React.Component<GXIconFontTextProps, GXIconFontTextState> {
  render() {
    const { propStyle, propDataValue } = this.props

    const rootStyle = {
      width:'100px',
      height:'50px',
      backgroundColor:'#ff00ff',
      fontSize: '16px',
      color: '#00ff00'
    }


    return <Text
      className={classNames('at-icon', 'at-icon-bell')}
      style={rootStyle}
    ></Text>
  }
}
