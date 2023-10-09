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
  // View or Template
  if (gxNode.gxTemplateNode.isViewType() || gxNode.gxTemplateNode.isGaiaTemplate()) {
    createViewOrTemplate(gxNode)
  } else if (gxNode.gxTemplateNode.isTextType()) {
    createText(gxNode)
  } else {
    Text("other GXNode")
  }
}

@Builder
function createText(gxNode: GXNode) {
  Text(gxNode.gxData?.value || '')
    .key(gxNode.gxId)
    .width(width(gxNode))
    .height(height(gxNode))
    .textAlign(textAlign(gxNode))
}

@Builder
function createViewOrTemplate(gxNode: GXNode) {
  // 如果是View或者Template类型，那么创建Flex布局
  Flex({
    direction: flexDirection(gxNode),
    justifyContent: flexJustifyContent(gxNode),
    alignItems: flexAlignItems(gxNode),
    alignContent: flexAlignContent(gxNode),
  }) {
    ForEach(gxNode.gxChildren, (gxChildNode: GXNode) => {
      // 如果有子节点，那么递归创建子View
      createViewByNodeTree(gxChildNode);
    }, (item: GXNode, index) => index + item.gxId);
  }
  .key(gxNode.gxId)
  .width(width(gxNode))
  .height(height(gxNode))
  .flexShrink(flexShrink(gxNode))
  .flexGrow(flexGrow(gxNode))
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/text-align
 * @param gxNode
 * @returns
 */
function textAlign(gxNode: GXNode): TextAlign {
  let value = gxNode.gxTemplateNode.finalCss['text-align'];
  if (value == "start") {
    return TextAlign.Start;
  } else if (value == "end") {
    return TextAlign.End;
  } else if (value == "center") {
    return TextAlign.Center;
  }
  return TextAlign.Start;
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/align-content
 * @param gxNode
 * @returns
 */
function flexAlignContent(gxNode: GXNode): FlexAlign {
  let value = gxNode.gxTemplateNode.finalCss['align-content'];
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
  return FlexAlign.Start;
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/align-items
 * @param gxNode
 * @returns
 */
function flexAlignItems(gxNode: GXNode): ItemAlign {
  let value = gxNode.gxTemplateNode.finalCss['align-items'];
  if (value == "start") {
    return ItemAlign.Start;
  } else if (value == "end") {
    return ItemAlign.End;
  } else if (value == "center") {
    return ItemAlign.Center;
  } else if (value == "stretch") {
    return ItemAlign.Stretch;
  }
  return ItemAlign.Start;
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/justify-content
 * @param gxNode
 * @returns
 */
function flexJustifyContent(gxNode: GXNode): FlexAlign {
  let value = gxNode.gxTemplateNode.finalCss['justify-content'];
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
  return FlexAlign.Start;
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/flex-direction
 * @param gxNode
 * @returns
 */
function flexDirection(gxNode: GXNode): FlexDirection {
  let value = gxNode.gxTemplateNode.finalCss['flex-direction'];
  if (value == "row") {
    return FlexDirection.Row;
  } else if (value == "column") {
    return FlexDirection.Column
  }
  return FlexDirection.Row;
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/height
 * @param gxNode
 * @returns
 */
function height(gxNode: GXNode): Length {
  let value: string = gxNode.gxTemplateNode.finalCss['height'];
  if (value != undefined && value != null) {
    return value.replace('px', 'vp');
  }
  return "auto";
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/width
 * @param gxNode
 * @returns
 */
function width(gxNode: GXNode): Length {
  let value: string = gxNode.gxTemplateNode.finalCss['width'];
  if (value != undefined && value != null) {
    return value.replace('px', 'vp');
  }
  return "auto";
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/flex-grow
 * @param gxNode
 * @returns
 */
function flexGrow(gxNode: GXNode): number {
  let value: string = gxNode.gxTemplateNode.finalCss['flex-grow'];
  if (value != undefined && value != null) {
    return Number.parseInt(value);
  }
  return 1;
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/flex-shrink
 * @param gxNode
 * @returns
 */
function flexShrink(gxNode: GXNode): number {
  let value: string = gxNode.gxTemplateNode.finalCss['flex-shrink'];
  if (value != undefined && value != null) {
    return Number.parseInt(value);
  }
  return 1;
}
