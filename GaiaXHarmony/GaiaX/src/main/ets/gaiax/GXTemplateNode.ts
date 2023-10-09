import GXCssConvertStyle from './GXCssConvertStyle';
import GXDataBinding from './GXDatabinding';
import GXExpression from './GXExpression';
import { GXJSONObject } from './GXJson';
import { GXNode } from './GXNode';
import GXTemplateContext from './GXTemplateContext';
import GXTemplateInfo from './GXTemplateInfo';

export class GXGridConfig {
  direction: string = 'vertical';
  itemSpacing: string = null;
  rowSpacing: string = null;
  column: number = 1;
  edgeInsetsLeft: string = null;
  edgeInsetsTop: string = null;
  edgeInsetsRight: string = null;
  edgeInsetsBottom: string = null;

  static createByExtend(src: GXGridConfig, extend: GXJSONObject) {
    // ScrollConfig
    const gxConfig = new GXGridConfig();

    // direction
    gxConfig.direction = src.direction;
    const newDirection = extend['direction']
    if (newDirection != null) {
      gxConfig.direction = newDirection
    }

    // item spacing
    const itemSpacing = src.itemSpacing;
    if (itemSpacing != null) {
      gxConfig.itemSpacing = src.itemSpacing;
    }
    const newItemSpacing = extend['item-spacing'];
    if (newItemSpacing != null) {
      gxConfig.itemSpacing = newItemSpacing;
    }

    // row spacing
    const rowSpacing = src.rowSpacing;
    if (rowSpacing != null) {
      gxConfig.rowSpacing = src.rowSpacing;
    }
    const newRowSpacing = extend['row-spacing'];
    if (newRowSpacing != null) {
      gxConfig.rowSpacing = newRowSpacing;
    }

    return gxConfig;
  }

  static create(gxLayer: GXJSONObject) {
    // ScrollConfig
    const gxConfig = new GXGridConfig();

    // column
    gxConfig.column = Number.parseInt(gxLayer['column']) || 1;

    // direction
    gxConfig.direction = gxLayer['direction'] || 'vertical';

    // item spacing
    const itemSpacing = gxLayer['item-spacing'];
    if (itemSpacing != null) {
      gxConfig.itemSpacing = itemSpacing + "px";
    }

    // row spacing
    const rowSpacing = gxLayer['row-spacing'];
    if (rowSpacing != null) {
      gxConfig.rowSpacing = rowSpacing + "px";
    }

    // top left bottom right
    // "edge-insets": "{2,1,4,3}",
    const edgeInsets: string = gxLayer['edge-insets'];
    if (edgeInsets != null) {
      const edges = edgeInsets.substring(1, edgeInsets.length - 1).split(',');
      gxConfig.edgeInsetsTop = edges[0] + 'px';
      gxConfig.edgeInsetsLeft = edges[1] + 'px';
      gxConfig.edgeInsetsBottom = edges[2] + 'px';
      gxConfig.edgeInsetsRight = edges[3] + 'px';
    }

    return gxConfig;
  }
}

export class GXScrollConfig {
  direction: string = 'horizontal';
  itemSpacing: string = null;
  rowSpacing: string = null;
  edgeInsetsLeft: string = null;
  edgeInsetsTop: string = null;
  edgeInsetsRight: string = null;
  edgeInsetsBottom: string = null;

  static createByExtend(src: GXScrollConfig, extend: GXJSONObject) {
    // ScrollConfig
    const gxConfig = new GXScrollConfig();

    // direction
    gxConfig.direction = src.direction;
    const newDirection = extend['direction']
    if (newDirection != null) {
      gxConfig.direction = newDirection
    }

    // item spacing
    const itemSpacing = src.itemSpacing;
    if (itemSpacing != null) {
      gxConfig.itemSpacing = src.itemSpacing;
    }
    const newItemSpacing = extend['item-spacing'];
    if (newItemSpacing != null) {
      gxConfig.itemSpacing = newItemSpacing;
    }

    // row spacing
    const rowSpacing = src.rowSpacing;
    if (rowSpacing != null) {
      gxConfig.rowSpacing = src.rowSpacing;
    }
    const newRowSpacing = extend['row-spacing'];
    if (newRowSpacing != null) {
      gxConfig.rowSpacing = newRowSpacing;
    }

    return gxConfig;
  }

  static create(gxLayer: GXJSONObject) {
    // ScrollConfig
    const gxConfig = new GXScrollConfig();

    // direction
    gxConfig.direction = gxLayer['direction'] || 'horizontal';

    // item spacing
    const itemSpacing = gxLayer['item-spacing'];
    if (itemSpacing != null) {
      gxConfig.itemSpacing = itemSpacing + "px";
    }

    // row spacing
    const rowSpacing = gxLayer['row-spacing'];
    if (rowSpacing != null) {
      gxConfig.rowSpacing = rowSpacing + "px";
    }

    // top left bottom right
    // "edge-insets": "{2,1,4,3}",
    const edgeInsets: string = gxLayer['edge-insets'];
    if (edgeInsets != null) {
      const edges = edgeInsets.substring(1, edgeInsets.length - 1).split(',');
      gxConfig.edgeInsetsTop = edges[0] + 'px';
      gxConfig.edgeInsetsLeft = edges[1] + 'px';
      gxConfig.edgeInsetsBottom = edges[2] + 'px';
      gxConfig.edgeInsetsRight = edges[3] + 'px';
    }


    return gxConfig;
  }
}

/**
 * 节点的原始样式
 */
export default class GXTemplateNode {
  forceWidthChange: boolean;
  imageMode: string;

  getExtend(gxTemplateData?: GXJSONObject): GXJSONObject {
    return GXDataBinding.getExtend(this.data, gxTemplateData);
  }

  getData(gxTemplateData?: GXJSONObject): GXJSONObject {
    return GXDataBinding.getData(this.data, gxTemplateData);
  }

  getDataValue(gxTemplateData?: GXJSONObject): GXJSONObject {
    return GXDataBinding.getData(this.data, gxTemplateData)['value'] as GXJSONObject;
  }

  getEventData(gxTemplateData?: GXJSONObject): GXJSONObject {
    return GXExpression.desireData(this.event, gxTemplateData) as GXJSONObject;
  }

  initFinal(
    gxTemplateContext: GXTemplateContext,
    gxTemplateData?: GXJSONObject,
    gxVisualTemplateData?: GXJSONObject,
    gxParentNode?: GXNode
  ) {

    // 获取样式绑定的结果
    const extendCssData = this.getExtend(gxTemplateData);

    let selfFinalCss = {};
    if (extendCssData != null) {

      selfFinalCss = Object.assign({}, this.css, extendCssData);

      if (this.gxScrollConfig != null) {
        this.finalGXScrollConfig = GXScrollConfig.createByExtend(this.gxScrollConfig, extendCssData);
      }

      if (this.gxGridConfig != null) {
        this.finalGXGridConfig = GXGridConfig.createByExtend(this.gxGridConfig, extendCssData);
      }

    } else {

      selfFinalCss = this.css;

      this.finalGXScrollConfig = this.gxScrollConfig;

      this.finalGXGridConfig = this.gxGridConfig;
    }

    // 初始化虚拟节点样式
    this.gxVisualTemplateNode?.initFinal(gxTemplateContext, gxVisualTemplateData, null, gxParentNode);

    // 组合节点样式
    this.finalCss = Object.assign({}, selfFinalCss, this.gxVisualTemplateNode?.finalCss);

    // 获取转换后的节点样式
    this.finalStyle = GXCssConvertStyle.createViewStyleByCss(gxTemplateContext, this.layer, this.finalCss, this, gxParentNode);
  }

  layer: GXJSONObject;
  css: GXJSONObject;
  data?: GXJSONObject;
  event?: GXJSONObject;
  animation?: GXJSONObject;
  gxVisualTemplateNode?: GXTemplateNode;
  gxScrollConfig: GXScrollConfig = null;
  gxGridConfig: GXGridConfig = null;
  finalGXScrollConfig: GXScrollConfig = null;
  finalGXGridConfig: GXGridConfig = null;
  finalStyle: any;
  finalCss: GXJSONObject;

  textAlign() {
    let value = this.finalCss['text-align'];
    if (value != undefined && value != null) {
      return value;
    }
    return null;
  }

  alignContent() {
    let value = this.finalCss['align-content'];
    if (value != undefined && value != null) {
      return value;
    }
    return null;
  }

  alignItems() {
    let value = this.finalCss['align-items'];
    if (value != undefined && value != null) {
      return value;
    }
    return null;
  }

  justifyContent() {
    let value = this.finalCss['justify-content'];
    if (value != undefined && value != null) {
      return value;
    }
    return null;
  }

  direction(): string {
    let value = this.finalCss['direction'];
    if (value != undefined && value != null) {
      return value;
    }
    return null;
  }

  width(): string {
    let value: string = this.finalCss['width'];
    if (value != undefined && value != null) {
      return value.replace('px', 'vp');
    }
    return "auto";
  }

  height(): string {
    let value: string = this.finalCss['height'];
    if (value != undefined && value != null) {
      return value.replace('px', 'vp');
    }
    return "auto";
  }

  type(): string {
    return this.layer['type'];
  }

  getCustomView(): string {
    return this.layer['view-class-taro']
  }

  isNestChildTemplateType(): boolean {
    return GXTemplateNode.isNestChildTemplateType(this.layer);
  }

  isContainerType(): boolean {
    return GXTemplateNode.isContainerType(this.layer);
  }

  isCustomType(): boolean {
    return GXTemplateNode.isCustomType(this.layer);
  }

  isTextType(): boolean {
    return GXTemplateNode.isTextType(this.layer);
  }

  isRichTextType(): boolean {
    return GXTemplateNode.isRichTextType(this.layer);
  }

  isIconFontType(): boolean {
    return GXTemplateNode.isIconFontType(this.layer);
  }

  isLottieType(): boolean {
    return GXTemplateNode.isLottieType(this.layer);
  }

  isImageType(): boolean {
    return GXTemplateNode.isImageType(this.layer);
  }

  isViewType(): boolean {
    return GXTemplateNode.isViewType(this.layer);
  }

  isGaiaTemplate(): boolean {
    return GXTemplateNode.isGaiaTemplate(this.layer);
  }

  isGridType(): boolean {
    return GXTemplateNode.isGridType(this.layer);
  }

  isScrollType(): boolean {
    return GXTemplateNode.isScrollType(this.layer);
  }

  isSliderType(): boolean {
    return GXTemplateNode.isSliderType(this.layer);
  }

  static isNestChildTemplateType(layer: GXJSONObject): boolean {
    return layer != null &&
      layer['type'] == 'gaia-template'
      && layer['sub-type'] == 'custom'
      && layer['view-class-taro'] == null
  }

  static isContainerType(layer: GXJSONObject): boolean {
    return layer != null && GXTemplateNode.isScrollType(layer) || GXTemplateNode.isGridType(layer) || GXTemplateNode.isSliderType(layer);
  }

  static isCustomType(layer: GXJSONObject): boolean {
    return layer != null && layer['type'] == 'custom' && (layer['view-class-taro'] != null)
  }

  static isTextType(layer: GXJSONObject): boolean {
    return layer != null && layer['type'] == 'text'
  }

  static isRichTextType(layer: GXJSONObject): boolean {
    return layer != null && layer['type'] == 'richtext'
  }

  static isIconFontType(layer: GXJSONObject): boolean {
    return layer != null && layer['type'] == 'iconfont'
  }

  static isLottieType(layer: GXJSONObject): boolean {
    return layer != null && layer['type'] == 'lottie'
  }

  static isImageType(layer: GXJSONObject): boolean {
    return layer != null && layer['type'] == 'image'
  }

  static isViewType(layer: GXJSONObject): boolean {
    return layer != null && layer['type'] == 'view' || layer['type'] == 'gaia-template' && layer['sub-type'] != null
  }

  static isGaiaTemplate(layer: GXJSONObject): boolean {
    return layer != null && layer['type'] == 'gaia-template'
  }

  static isGridType(layer: GXJSONObject): boolean {
    return layer != null && layer['type'] == 'gaia-template' && layer['sub-type'] == 'grid'
  }

  static isScrollType(layer: GXJSONObject): boolean {
    return layer != null && layer['type'] == 'gaia-template' && layer['sub-type'] == 'scroll'
  }

  static isSliderType(layer: GXJSONObject): boolean {
    return layer != null && layer['type'] == 'gaia-template' && layer['sub-type'] == 'slider'
  }

  static create(gxLayer: GXJSONObject, gxTemplateInfo: GXTemplateInfo, gxVisualTemplateNode?: GXTemplateNode): GXTemplateNode {
    const gxTemplateNode = new GXTemplateNode()

    const layerId = gxLayer['id'];

    // 获取原始节点的层级
    gxTemplateNode.layer = gxLayer;

    // 获取原始节点的样式
    gxTemplateNode.css = gxTemplateInfo.css['#' + layerId] || gxTemplateInfo.css['.' + layerId];

    // 获取原始节点的数据
    gxTemplateNode.data = gxTemplateInfo.data['data']?.[layerId];

    // 获取原始节点的事件
    gxTemplateNode.event = gxTemplateInfo.data['event']?.[layerId];

    // 获取原始节点的动画
    gxTemplateNode.animation = gxTemplateInfo.data['animation']?.[layerId];

    // 设置虚拟节点
    gxTemplateNode.gxVisualTemplateNode = gxVisualTemplateNode;

    // 设置Scroll配置
    if (GXTemplateNode.isScrollType(gxLayer)) {
      gxTemplateNode.gxScrollConfig = GXScrollConfig.create(gxLayer);
    }

    // 设置Grid配置
    if (GXTemplateNode.isGridType(gxLayer)) {
      gxTemplateNode.gxGridConfig = GXGridConfig.create(gxLayer);
    }

    return gxTemplateNode;
  }
}