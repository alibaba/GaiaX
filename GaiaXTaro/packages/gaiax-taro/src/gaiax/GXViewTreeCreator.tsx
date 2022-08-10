import { View, Text, Image } from "@tarojs/components";
import React, { ReactNode } from "react";
import GXTemplateContext from "./GXTemplateContext";
import { GXNode } from "./GXNode";
import GXTemplateNode from "./GXTemplateNode";
import GXCssConvertStyle from "./GXCssConvertStyle";
import GXTemplateInfo from "./GXTemplateInfo";
import { GXJSONArray, GXJSONObject } from "./GXJson";
import { GXTemplateItem } from "./GXTemplateEngine";
import GXView from "./components/view/GXView";
import GXImage from "./components/image/GXImage";
import GXText from "./components/text/GXText";
import GXRichText from "./components/richtext/GXRichText";
import GXIconFontText from "./components/iconfonttext/GXRichText";

export default class GXViewTreeCreator {

    build(gxTemplateContext: GXTemplateContext): ReactNode {

        const gxRootStyle = GXCssConvertStyle.createRootStyle(gxTemplateContext.gxMeasureSize);

        const gxTemplateData = gxTemplateContext.gxTemplateData.templateData as GXJSONObject;

        const gxTemplateInfo = gxTemplateContext.gxTemplateInfo;

        const gxLayer = gxTemplateInfo.layer;

        const gxParentNode = null;

        const gxVisualTemplateNode = gxTemplateContext.gxVisualTemplateNode;

        const gxRootNode = this.createNode(
            gxTemplateContext,
            gxTemplateData,
            gxTemplateInfo,
            gxLayer,
            gxParentNode,
            gxVisualTemplateNode
        );

        return <View style={gxRootStyle}>{gxRootNode.gxView}</View>;
    }

    private createNode(
        gxTemplateContext: GXTemplateContext,
        gxTemplateData: GXJSONObject,
        gxTemplateInfo: GXTemplateInfo,
        gxLayer: GXJSONObject,
        gxParentNode?: GXNode,
        gxVisualTemplateNode?: GXTemplateNode
    ): GXNode {

        const gxNode = GXNode.create();

        gxNode.setIdPath(gxLayer, gxParentNode);

        gxNode.gxTemplateNode = GXTemplateNode.create(gxLayer, gxTemplateInfo, gxVisualTemplateNode);

        // Container - Grid/Scroll
        if (gxNode.gxTemplateNode.isContainerType()) {
            this.createContainerNode(
                gxTemplateContext,
                gxNode,
                gxTemplateData,
                gxLayer,
                gxTemplateInfo,
                gxParentNode
            );
        }
        // View or Template
        else if (gxNode.gxTemplateNode.isViewType() || gxNode.gxTemplateNode.isGaiaTemplate()) {
            this.createViewOrTemplateNode(
                gxTemplateContext,
                gxNode,
                gxTemplateData,
                gxLayer,
                gxTemplateInfo,
                gxParentNode
            );
        }
        // Text
        else if (gxNode.gxTemplateNode.isTextType()) {
            this.createTextNode(
                gxTemplateContext,
                gxNode,
                gxTemplateData,
                gxParentNode
            );
        }
        // RichText
        else if (gxNode.gxTemplateNode.isRichTextType()) {
            this.createRichTextNode(
                gxTemplateContext,
                gxNode,
                gxTemplateData,
                gxParentNode
            );
        }
        // IconFont
        else if (gxNode.gxTemplateNode.isIconFontType()) {
            this.createIconFontNode(
                gxTemplateContext,
                gxNode,
                gxTemplateData,
                gxParentNode
            );
        }
        // Image
        else if (gxNode.gxTemplateNode.isImageType()) {
            this.createImageNode(
                gxTemplateContext,
                gxNode,
                gxTemplateData,
                gxParentNode
            );
        }
        // Other
        else {
            this.createOtherNode(
                gxTemplateContext,
                gxNode,
                gxTemplateData,
                gxParentNode
            );
        }

        return gxNode;
    }

    private createContainerNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject,
        gxLayer: GXJSONObject,
        gxTemplateInfo: GXTemplateInfo,
        gxParentNode: GXNode
    ) {
        // gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, gxVisualTemplateNode, gxNode);

        // case 'grid':
        // return <View style={finalNodeStyle} key={gxLayer.id} />;
        // case 'scroll':
        // return <View style={finalNodeStyle} key={gxLayer.id} />;
    }

    private createViewOrTemplateNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject,
        gxLayer: GXJSONObject,
        gxTemplateInfo: GXTemplateInfo,
        gxParentNode: GXNode
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxParentNode);

        const childArray: ReactNode[] = [];
        const layers = gxLayer['layers'] as GXJSONArray;

        if (layers != null) {

            if (gxNode != null && gxNode.gxChildren == null) {
                gxNode.gxChildren = new Array<GXNode>();
            }

            for (const target of layers) {

                const childLayer = target as GXJSONObject;

                // 嵌套子模板类型，是个虚拟节点
                if (GXTemplateNode.isNestChildTemplateType(childLayer)) {

                    // 获取子模板信息
                    const gxChildTemplateInfo = gxTemplateInfo.getChildTemplate(childLayer.id);

                    // 创建一个虚拟节点
                    const gxChildVisualTemplateNode = GXTemplateNode.create(childLayer, gxTemplateInfo, null);

                    // 获取子模板的根节点
                    const gxChildLayer = gxChildTemplateInfo.layer;

                    // 容器模板下的子模板
                    if (gxNode.gxTemplateNode.isContainerType()) {

                        // 初始化
                        if (gxNode != null && gxNode.gxChildTemplateItems == null) {
                            gxNode.gxChildTemplateItems = new Array<GXTemplateItem>();
                        }

                        // 容器下的子模板
                        const childTemplateItems = GXTemplateItem.create(
                            gxTemplateContext.gxTemplateItem.templateBiz,
                            gxChildLayer.id
                        );

                        gxNode?.gxChildTemplateItems?.push(childTemplateItems);
                    }
                    // 普通模板嵌套的子模板根节点，可能是普通模板也可能是容器模板
                    else {

                        // 嵌套子模板的数据
                        const gxChildTemplateData = gxChildVisualTemplateNode.getDataValue(gxTemplateData);

                        const childNode = this.createNode(
                            gxTemplateContext,
                            gxChildTemplateData,
                            gxChildTemplateInfo,
                            gxChildLayer,
                            gxNode,
                            gxChildVisualTemplateNode
                        );

                        gxNode?.gxChildren?.push(childNode);

                        childArray.push(childNode.gxView);
                    }
                }
                // 普通子节点
                else {
                    const childNode = this.createNode(
                        gxTemplateContext,
                        gxTemplateData,
                        gxTemplateInfo,
                        childLayer,
                        gxNode,
                        null
                    );

                    gxNode?.gxChildren?.push(childNode);

                    childArray.push(childNode.gxView);
                }
            }
        }

        gxNode.gxView = <GXView propStyle={gxNode.gxTemplateNode.finalStyle} propKey={gxNode.gxId} propChildArray={childArray} />;
    }

    private createOtherNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject,
        gxParentNode?: GXNode
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxParentNode);
        const data = gxNode.gxTemplateNode.getData(gxTemplateData);
        gxNode.gxView = <GXView propStyle={gxNode.gxTemplateNode.finalStyle} propKey={gxNode.gxId} />;
    }

    private createImageNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject,
        gxParentNode?: GXNode
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxParentNode);
        const data = gxNode.gxTemplateNode.getData(gxTemplateData);
        gxNode.gxView = <GXImage propStyle={gxNode.gxTemplateNode.finalStyle} propKey={gxNode.gxId} propDataValue={data.value} />;
    }

    private createIconFontNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject,
        gxParentNode?: GXNode
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxParentNode);
        const data = gxNode.gxTemplateNode.getData(gxTemplateData);
        gxNode.gxView = <GXIconFontText propStyle={gxNode.gxTemplateNode.finalStyle} propKey={gxNode.gxId} propDataValue={data.value} />;
    }

    private createRichTextNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject,
        gxParentNode?: GXNode
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxParentNode);
        const data = gxNode.gxTemplateNode.getData(gxTemplateData);
        gxNode.gxView = <GXRichText propStyle={gxNode.gxTemplateNode.finalStyle} propKey={gxNode.gxId} propDataValue={data.value} />;
    }

    private createTextNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject,
        gxParentNode?: GXNode
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxParentNode);
        const data = gxNode.gxTemplateNode.getData(gxTemplateData);
        gxNode.gxView = <GXText propStyle={gxNode.gxTemplateNode.finalStyle} propKey={gxNode.gxId} propDataValue={data.value} />;
    }
}