import GXCssConvertStyle from "./GXCssConvertStyle";
import GXDataBinding from "./GXDatabinding";
import GXExpression from "./GXExpression";
import { GXJSONObject } from "./GXJson";
import { GXNode } from "./GXNode";
import GXTemplateContext from "./GXTemplateContext";
import GXTemplateInfo from "./GXTemplateInfo";

/**
 * 节点的原始样式
 */
export default class GXTemplateNode {

    getExtend(gxTemplateData?: GXJSONObject): GXJSONObject {
        return GXDataBinding.getExtend(this.data, gxTemplateData);
    }

    getData(gxTemplateData?: GXJSONObject): GXJSONObject {
        return GXDataBinding.getData(this.data, gxTemplateData);
    }

    getDataValue(gxTemplateData?: GXJSONObject): GXJSONObject {
        return GXDataBinding.getData(this.data, gxTemplateData)['value'] as GXJSONObject;
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
        } else {
            selfFinalCss = this.css;
        }

        // 初始化虚拟节点样式
        this.gxVisualTemplateNode?.initFinal(gxTemplateContext, gxVisualTemplateData, null, gxParentNode);

        // 组合节点样式
        this.finalCss = Object.assign({}, selfFinalCss, this.gxVisualTemplateNode?.finalCss);

        // 获取转换后的节点样式
        this.finalStyle = GXCssConvertStyle.createViewStyleByCss(gxTemplateContext, this.layer, this.finalCss, gxParentNode);
    }

    layer: GXJSONObject;

    css: GXJSONObject;

    data?: GXJSONObject;

    event?: GXJSONObject;

    animation?: GXJSONObject;

    gxVisualTemplateNode?: GXTemplateNode;

    finalStyle: React.CSSProperties;

    finalCss: GXJSONObject;

    type(): string {
        return this.layer['type'];
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
            && layer['view-class-android'] == null
            && layer['view-class-ios'] == null
    }

    static isContainerType(layer: GXJSONObject): boolean {
        return layer != null && GXTemplateNode.isScrollType(layer) || GXTemplateNode.isGridType(layer) || GXTemplateNode.isSliderType(layer);
    }

    static isCustomType(layer: GXJSONObject): boolean {
        return false
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

        return gxTemplateNode;
    }
}