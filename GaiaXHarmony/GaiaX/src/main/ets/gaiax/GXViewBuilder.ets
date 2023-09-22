import GXGenerate from './GXGenerate';
import GXMeasureSize from './GXMeasureSize';
import { GXNode } from './GXNode';
import GXTemplateContext from './GXTemplateContext';
import GXTemplateData from './GXTemplateData';
import GXTemplateItem from './GXTemplateItem';
import GXTemplateNode from './GXTemplateNode';
import GXViewTreeCreator from './GXViewTreeCreator';

@Builder
export default function createViewForEngine(
    gxTemplateItem: GXTemplateItem,
    gxTemplateData: GXTemplateData,
    gxMeasureSize: GXMeasureSize,
    gxVisualTemplateNode?: GXTemplateNode
) {
  createViewForRender(GXGenerate.createContext(gxTemplateItem, gxTemplateData, gxMeasureSize, gxVisualTemplateNode))
}

@Builder
function createViewForRender(gxTemplateContext?: GXTemplateContext) {
  if (gxTemplateContext == null) {
    Text("GXView is null")
  } else {
    createNode(GXViewTreeCreator.createRootNode(gxTemplateContext))
  }

}

@Builder
function createNode(gxNode?: GXNode) {
  if (gxNode == null) {
    Text("GXNode is null")
  } else {
    Text("createGXView")
  }
}