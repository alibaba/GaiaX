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
    Text('other type node')
  }
}

@Builder
function createText(gxNode: GXNode) {
  Text(gxNode.gxData?.value || 'text value is null')
    .key(gxNode.gxId)
    .width(width(gxNode))
    .height(height(gxNode))
    .flexBasis(flexBasis(gxNode))
    .flexShrink(flexShrink(gxNode))
    .flexGrow(flexGrow(gxNode))
    .alignSelf(alignSelf(gxNode))
    .margin(margin(gxNode))
    .padding(padding(gxNode))
    .aspectRatio(aspectRatio(gxNode))
    .constraintSize(constraintSize(gxNode))
    .backgroundColor(backgroundColor(gxNode))
    .textAlign(textAlign(gxNode))
    .opacity(opacity(gxNode))
    .border({
      width: borderWidth(gxNode),
      color: borderColor(gxNode),
      radius: borderRadius(gxNode),
      style: borderStyle(gxNode)
    })
    .clip(true)
    .visibility(visibility(gxNode))
}

@Builder
function createViewOrTemplate(gxNode: GXNode) {
  // 如果是View或者Template类型，那么创建Flex布局
  Flex({
    wrap: flexWrap(gxNode),
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
  .flexBasis(flexBasis(gxNode))
  .flexShrink(flexShrink(gxNode))
  .flexGrow(flexGrow(gxNode))
  .alignSelf(alignSelf(gxNode))
  .margin(margin(gxNode))
  .padding(padding(gxNode))
  .aspectRatio(aspectRatio(gxNode))
  .constraintSize(constraintSize(gxNode))
  .backgroundColor(backgroundColor(gxNode))
  .opacity(opacity(gxNode))
  .border({
    width: borderWidth(gxNode),
    color: borderColor(gxNode),
    radius: borderRadius(gxNode),
    style: borderStyle(gxNode)
  })
  .clip(true)
  .visibility(visibility(gxNode))
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/visibility
 * @param gxNode
 */
function visibility(gxNode: GXNode): Visibility {
  let visibility = gxNode.gxTemplateNode.finalCss['visibility'];
  if (visibility != undefined && visibility != null) {
    if (visibility == "visible") {
      return Visibility.Visible;
    } else if (visibility == "hidden") {
      return Visibility.Hidden;
    }
  }
  return null;
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/border-style
 * @param gxNode
 */
function borderStyle(gxNode: GXNode): BorderStyle {
  let borderStyle = gxNode.gxTemplateNode.finalCss['border-style'];
  if (borderStyle != undefined && borderStyle != null) {
    if (borderStyle == "none") {
      return null;
    } else if (borderStyle == "dotted") {
      return BorderStyle.Dotted;
    } else if (borderStyle == "dashed") {
      return BorderStyle.Dashed;
    } else if (borderStyle == "solid") {
      return BorderStyle.Solid;
    }
  }
  return null;
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/border-radius
 * @param gxNode
 */
function borderRadius(gxNode: GXNode): string {
  let borderRadius = gxNode.gxTemplateNode.finalCss['border-radius'];
  if (borderRadius != undefined && borderRadius != null) {
    return borderRadius.replace('px', 'vp');
  }
  return null;
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/border-width
 * @param gxNode
 */
function borderColor(gxNode: GXNode): string {
  let borderColor = gxNode.gxTemplateNode.finalCss['border-color'];
  if (borderColor != undefined && borderColor != null) {
    return borderColor;
  }
  return null;
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/border-width
 * @param gxNode
 */
function borderWidth(gxNode: GXNode): string {
  let borderWidth = gxNode.gxTemplateNode.finalCss['border-width'];
  if (borderWidth != undefined && borderWidth != null) {
    return borderWidth.replace('px', 'vp');
  }
  return null;
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/opacity
 * @param gxNode
 */
function opacity(gxNode: GXNode): number {
  let opacity = gxNode.gxTemplateNode.finalCss['opacity'];
  if (opacity != undefined && opacity != null) {
    return Number.parseInt(opacity);
  }
  return 1;
}


/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/flex-basis
 * @param gxNode
 */
function flexBasis(gxNode: GXNode): string {
  let flexBasis = gxNode.gxTemplateNode.finalCss['flex-basis'];
  if (flexBasis != undefined && overflow != null) {
    if (flexBasis == "auto") {
      return "auto";
    } else {
      return flexBasis.replace('px', 'vp');
    }
  }
  return "auto";
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/align-self
 * @param gxNode
 */
function alignSelf(gxNode: GXNode): ItemAlign {
  let alignSelf = gxNode.gxTemplateNode.finalCss['align-self'];
  if (alignSelf != undefined && alignSelf != null) {
    if (alignSelf == "auto") {
      return ItemAlign.Auto;
    } else if (alignSelf == "stretch") {
      return ItemAlign.Stretch;
    } else if (alignSelf == "center") {
      return ItemAlign.Center;
    } else if (alignSelf == "start") {
      return ItemAlign.Start;
    } else if (alignSelf == "end") {
      return ItemAlign.End;
    }
  }
  return null;
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/overflow
 * @param gxNode
 */
function overflow(gxNode: GXNode): string {
  let overflow = gxNode.gxTemplateNode.finalCss['overflow'];
  if (overflow != undefined && overflow != null) {
    return overflow;
  }
  return null;
}

function constraintSize(gxNode: GXNode): ConstraintSizeOptions {
  let minWidth = gxNode.gxTemplateNode.finalCss['min-width'];
  let minHeight = gxNode.gxTemplateNode.finalCss['min-height'];
  let maxWidth = gxNode.gxTemplateNode.finalCss['max-width'];
  let maxHeight = gxNode.gxTemplateNode.finalCss['max-height'];
  return {
    minWidth: minWidth?.replace('px', 'vp'),
    minHeight: minHeight?.replace('px', 'vp'),
    maxWidth: maxWidth?.replace('px', 'vp'),
    maxHeight: maxHeight?.replace('px', 'vp'),
  };
}

/**
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/background-color
 * @param gxNode
 * @returns
 */
function backgroundColor(gxNode: GXNode): ResourceColor {
  let color = gxNode.gxTemplateNode.finalCss['background-color'];
  if (color != undefined && color != null) {
    return color;
  }
  return null;
}

function aspectRatio(gxNode: GXNode): number {
  let aspectRatio = gxNode.gxTemplateNode.finalCss['aspect-ratio'] || null;
  if (aspectRatio != undefined && aspectRatio != null) {
    return Number.parseInt(aspectRatio);
  }
  return null;
}

function margin(gxNode: GXNode): Margin {
  let left = gxNode.gxTemplateNode.finalCss['margin-left'];
  let top = gxNode.gxTemplateNode.finalCss['margin-top'];
  let right = gxNode.gxTemplateNode.finalCss['margin-right'];
  let bottom = gxNode.gxTemplateNode.finalCss['margin-bottom'];
  return {
    left: left?.value?.replace('px', 'vp'),
    top: top?.value?.replace('px', 'vp'),
    right: right?.value?.replace('px', 'vp'),
    bottom: bottom?.value?.replace('px', 'vp')
  }
}

function padding(gxNode: GXNode): Margin {
  let left = gxNode.gxTemplateNode.finalCss['padding-left'];
  let top = gxNode.gxTemplateNode.finalCss['padding-top'];
  let right = gxNode.gxTemplateNode.finalCss['padding-right'];
  let bottom = gxNode.gxTemplateNode.finalCss['padding-bottom'];
  return {
    left: left?.value?.replace('px', 'vp'),
    top: top?.value?.replace('px', 'vp'),
    right: right?.value?.replace('px', 'vp'),
    bottom: bottom?.value?.replace('px', 'vp')
  }
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
 * https://developer.mozilla.org/zh-CN/docs/Web/CSS/flex-wrap
 * @param gxNode
 * @returns
 */
function flexWrap(gxNode: GXNode): FlexWrap {
  let value = gxNode.gxTemplateNode.finalCss['flex-wrap'];
  if (value == "wrap") {
    return FlexWrap.Wrap;
  } else if (value == "wrap-reverse") {
    return FlexWrap.WrapReverse;
  } else if (value == "nowrap") {
    return FlexWrap.NoWrap;
  }
  return FlexWrap.NoWrap;
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
  return 0;
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
