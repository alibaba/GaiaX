import GXTemplateData from './GXTemplateData';
import GXMeasureSize from './GXMeasureSize';
import GXTemplateItem from './GXTemplateItem';
import GXTemplateInfo from './GXTemplateInfo';
import GXTemplateNode from './GXTemplateNode';
import { GXNode } from './GXNode';

export default class GXTemplateContext {
  gxTemplateItem: GXTemplateItem;

  gxTemplateInfo: GXTemplateInfo;

  gxTemplateData: GXTemplateData;

  gxMeasureSize: GXMeasureSize;

  gxVisualTemplateNode?: GXTemplateNode;

  isNestChildTemplate: boolean;

  gxRootNode: GXNode;

  gxRootStyle: any;

  constructor(
    gxTemplateItem: GXTemplateItem,
    gxTemplateData: GXTemplateData,
    gxMeasureSize: GXMeasureSize,
    gxTemplateInfo: GXTemplateInfo,
    gxVisualTemplateNode?: GXTemplateNode
  ) {
    this.gxTemplateItem = gxTemplateItem;
    this.gxTemplateInfo = gxTemplateInfo;
    this.gxTemplateData = gxTemplateData;
    this.gxMeasureSize = gxMeasureSize;
    this.gxVisualTemplateNode = gxVisualTemplateNode;
  }
}
