import React from 'react';
import { GXEngineInstance } from '..';
import { GXMeasureSize, GXTemplateData, GXTemplateItem } from '../gaiax/GXTemplateEngine';
import GXTemplateNode from '../gaiax/GXTemplateNode';

export interface GXTemplateComponentState {

}

export interface GXTemplateComponentProps {

  gxTemplateItem: GXTemplateItem

  gxMeasureSize: GXMeasureSize

  gxTemplateData: GXTemplateData

  gxVisualTemplateNode?: GXTemplateNode
}

export default class GXTemplateComponent extends React.Component<GXTemplateComponentProps, GXTemplateComponentState> {
  render() {
    const { gxTemplateItem, gxMeasureSize, gxTemplateData, gxVisualTemplateNode } = this.props
    return GXEngineInstance.createView(
      gxTemplateItem,
      gxTemplateData,
      gxMeasureSize,
      gxVisualTemplateNode
    );
  }
}
