import { Text } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';
import { GXJSONObject } from '../../gaiax/GXJson';

export interface GXRichTextState {

}

export interface GXRichTextProps {
  onClick?: CommonEventFunction
  propStyle?: string | CSSProperties
  propDataValue?: string
  propExtend?: GXJSONObject
}

export default class GXRichText extends React.Component<GXRichTextProps, GXRichTextState> {

  private handleClick(): void {
    this.props.onClick && this.props.onClick(arguments as any)
  }

  render() {
    const { propStyle, propDataValue, propExtend } = this.props

    let hightLightTag: string = null;
    let hightLightColor: string = null;
    if (propExtend != null) {
      hightLightTag = propExtend['highlight-tag'];
      hightLightColor = propExtend['highlight-color'];
    }
    if (hightLightTag != null &&
      hightLightColor != null &&
      propDataValue.indexOf(hightLightTag) != null
    ) {
      const childArray: ReactNode[] = [];
      let remainContent = propDataValue;
      let tagIndex = remainContent.indexOf(hightLightTag);
      let isTag = false;
      while (tagIndex != -1 && tagIndex >= 0 && tagIndex < remainContent.length) {

        let text = remainContent.substring(0, tagIndex);
        if (!isTag) {
          childArray.push(<Text>{text}</Text>);
          isTag = true;
        } else {
          isTag = false;
          childArray.push(<Text style={{ color: hightLightColor }}>{text}</Text>);
        }

        remainContent = remainContent.substring(tagIndex + 1, remainContent.length);
        tagIndex = remainContent.indexOf(hightLightTag);
      }

      return <Text
        style={propStyle}
        onClick={this.handleClick.bind(this)}  >
        {childArray}
      </Text>;
    }
    return <Text
      style={propStyle}
      onClick={this.handleClick.bind(this)}
    >
      {propDataValue}
    </Text>;
  }
}
