import React from 'react';
import { GXEngineInstance } from '../..';
import { GXMeasureSize, GXTemplateData, GXTemplateItem } from '../../gaiax/GXTemplateEngine';

export interface GXTemplateComponentState {

}

export interface GXTemplateComponentProps {

  templateItem: GXTemplateItem

  measureSize: GXMeasureSize

  templateData: GXTemplateData
}

export default class GXTemplateComponent extends React.Component<GXTemplateComponentProps, GXTemplateComponentState> {
  render() {
    const { templateItem, measureSize, templateData } = this.props
    console.log(templateItem)
    console.log(measureSize)
    console.log(templateData)
    return GXEngineInstance.createView(templateItem, templateData, measureSize);
  }
}
