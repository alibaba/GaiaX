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
    Text(gxNode.gxData?.value || '')
      .createTextAttr(gxNode)
      .textAlign(textAlign(gxNode))
  } else {
    Text("other GXNode")
  }
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/text-align
 * @param gxNode
 * @returns
 */
function textAlign(gxNode: GXNode): TextAlign {
  let value = gxNode.gxTemplateNode.textAlign();
  if (value != null) {
    if (value == "start") {
      return TextAlign.Start;
    } else if (value == "end") {
      return TextAlign.End;
    } else if (value == "center") {
      return TextAlign.Center;
    }
  }
  return TextAlign.Start;
}

/**
 * 创建Flex参数
 * @param gxNode
 * @returns
 */
function createFlexOptions(gxNode: GXNode): FlexOptions {
  return {
    direction: direction(gxNode),
    justifyContent: justifyContent(gxNode),
    alignItems: alignItems(gxNode),
    alignContent: alignContent(gxNode),
  };
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/align-content
 * @param gxNode
 * @returns
 */
function alignContent(gxNode: GXNode): FlexAlign {
  let value = gxNode.gxTemplateNode.alignContent();
  if (value != null) {
    if (value == "start" || value == "flex-start") {
      return FlexAlign.Start;
    } else if (value == "end" || value == "flex-end") {
      return FlexAlign.End;
    } else if (value == "center") {
      return FlexAlign.Center;
    } else if (value == "space-between") {
      return FlexAlign.SpaceBetween;
    } else if (value == "space-around") {
      return FlexAlign.SpaceAround;
    } else if (value == "space-evenly") {
      return FlexAlign.SpaceEvenly;
    }
  }
  return FlexAlign.Start;
}


/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/align-items
 * @param gxNode
 * @returns
 */
function alignItems(gxNode: GXNode): ItemAlign {
  let value = gxNode.gxTemplateNode.alignItems();
  if (value != null) {
    if (value == "start") {
      return ItemAlign.Start;
    } else if (value == "end") {
      return ItemAlign.End;
    } else if (value == "center") {
      return ItemAlign.Center;
    } else if (value == "stretch") {
      return ItemAlign.Stretch;
    }
  }
  return ItemAlign.Start;
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/justify-content
 * @param gxNode
 * @returns
 */
function justifyContent(gxNode: GXNode): FlexAlign {
  let value = gxNode.gxTemplateNode.justifyContent();
  if (value != null) {
    if (value == "start" || value == "flex-start") {
      return FlexAlign.Start;
    } else if (value == "end" || value == "flex-end") {
      return FlexAlign.End;
    } else if (value == "center") {
      return FlexAlign.Center;
    } else if (value == "space-between") {
      return FlexAlign.SpaceBetween;
    } else if (value == "space-around") {
      return FlexAlign.SpaceAround;
    } else if (value == "space-evenly") {
      return FlexAlign.SpaceEvenly;
    }
  }
  return FlexAlign.Start;
}

function direction(gxNode: GXNode): FlexDirection {
  let value = gxNode.gxTemplateNode.direction();
  if (value != null) {
    if (value == "row") {
      return FlexDirection.Row;
    } else if (value == "column") {
      return FlexDirection.Column
    }
  }

  return FlexDirection.Column;
}

@Extend(Flex)
function createFlexAttr(gxNode: GXNode) {
  .width(gxNode.gxTemplateNode.width())
  .height(gxNode.gxTemplateNode.height())
}

@Extend(Text)
function createTextAttr(gxNode: GXNode) {
  .width(gxNode.gxTemplateNode.width())
  .height(gxNode.gxTemplateNode.height())
}

