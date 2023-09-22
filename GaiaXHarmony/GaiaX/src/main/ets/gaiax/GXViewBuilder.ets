import GXGenerate from './GXGenerate';
import GXMeasureSize from './GXMeasureSize';
import { GXNode } from './GXNode';
import GXTemplateContext from './GXTemplateContext';
import GXTemplateData from './GXTemplateData';
import GXTemplateItem from './GXTemplateItem';
import GXTemplateNode from './GXTemplateNode';
import GXViewTreeCreator from './GXViewTreeCreator';

@Builder
export default function createView(
    gxTemplateItem: GXTemplateItem,
    gxTemplateData: GXTemplateData,
    gxMeasureSize: GXMeasureSize,
    gxVisualTemplateNode?: GXTemplateNode
) {
  createViewForContext(GXGenerate.createContext(gxTemplateItem, gxTemplateData, gxMeasureSize, gxVisualTemplateNode))
}

@Builder
function createViewForContext(gxTemplateContext?: GXTemplateContext) {
  if (gxTemplateContext == null) {
    Text("GXView is null")
  } else {
    createViewForNode(GXViewTreeCreator.createRootNode(gxTemplateContext))
  }
}

@Builder
function createViewForNode(gxNode?: GXNode) {
  if (gxNode == null) {
    Text("GXNode is null")
  } else {
    createViewByNodeTree(gxNode)
  }
}

@Builder
function createViewByNodeTree(gxNode: GXNode) {
  if (gxNode.gxTemplateNode.isViewType() || gxNode.gxTemplateNode.isGaiaTemplate()) {
    Flex(createFlexOptions(gxNode)) {
      ForEach(gxNode.gxChildren, (gxChildNode: GXNode) => {
        createViewByNodeTree(gxChildNode)
      }, (item, index) => index + JSON.stringify(item));
    }.createFlexAttr(gxNode)
  } else if (gxNode.gxTemplateNode.isTextType()) {
    Text("text GXNode")
  } else {
    Text("other GXNode")
  }
}

function createFlexOptions(gxNode: GXNode): FlexOptions {
  return {};
}

@Extend(Flex)
function createFlexAttr(gxNode: GXNode) {
  .width("100%")
  .height("auto")
}