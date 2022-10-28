import { Text } from '@tarojs/components';
import React from 'react';

export interface CustomComponentState {

}

export interface CustomComponentProps {
  propDataValue?: any
}

export default class CustomComponent extends React.Component<CustomComponentProps, CustomComponentState>  {

  render() {
    const { propDataValue } = this.props
    const id = "customComponent";
    const key = "component";
    const text = propDataValue;
    return <Text id={id} key={key}>
      {text}
    </Text>;
  }
}
