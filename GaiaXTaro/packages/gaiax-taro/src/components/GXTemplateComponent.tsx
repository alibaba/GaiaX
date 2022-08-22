import React from 'react';
import { GXEngineInstance } from '../gaiax/GXTemplateEngineInstance';
import { GXMeasureSize, GXTemplateData } from '../gaiax/GXTemplateEngine';
import GXTemplateItem from "../gaiax/GXTemplateItem";
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
