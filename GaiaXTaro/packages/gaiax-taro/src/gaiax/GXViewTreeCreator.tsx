import { View, Text, Image } from "@tarojs/components";
import React, { ReactNode } from "react";
import GXTemplateContext from "./GXTemplateContext";
import { GXNode } from "./GXNode";
import GXTemplateNode from "./GXTemplateNode";
import GXCssConvertStyle from "./GXCssConvertStyle";
import GXTemplateInfo from "./GXTemplateInfo";
import { GXJSONArray, GXJSONObject } from "./GXJson";
import { GXTemplateItem } from "./GXTemplateEngine";

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

        if (gxNode.gxTemplateNode.isContainerType()) {
            this.createContainerNode(
                gxNode,
                gxTemplateContext,
                gxTemplateData,
                gxVisualTemplateNode,
                gxLayer,
                gxTemplateInfo);
        }
        // View or Template
        else if (gxNode.gxTemplateNode.isViewType() || gxNode.gxTemplateNode.isGaiaTemplate()) {
            this.createViewOrTemplateNode(
                gxNode,
                gxTemplateContext,
                gxTemplateData,
                gxVisualTemplateNode,
                gxLayer,
                gxTemplateInfo
            );
        }
        // Text
        else if (gxNode.gxTemplateNode.isTextType()) {
            this.createTextNode(
                gxNode,
                gxTemplateContext,
                gxTemplateData
            );
        }
        // RichText
        else if (gxNode.gxTemplateNode.isRichTextType()) {
            this.createRichTextNode(
                gxNode,
                gxTemplateContext,
                gxTemplateData
            );
        }
        // IconFont
        else if (gxNode.gxTemplateNode.isIconFontType()) {
            this.createIconFontNode(
                gxNode,
                gxTemplateContext,
                gxTemplateData
            );
        }
        // Image
        else if (gxNode.gxTemplateNode.isImageType()) {
            this.createImageNode(
                gxNode,
                gxTemplateContext,
                gxTemplateData
            );
        }
        // Other
        else {
            this.createOtherNode(
                gxNode,
                gxTemplateContext,
                gxTemplateData
            );
        }

        return gxNode;
    }

    private createContainerNode(
        gxNode: GXNode,
        gxTemplateContext: GXTemplateContext,
        gxTemplateData: GXJSONObject,
        gxVisualTemplateNode: GXTemplateNode,
        gxLayer: GXJSONObject,
        gxTemplateInfo: GXTemplateInfo
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, gxVisualTemplateNode, gxNode);

        // case 'grid':
        // return <View style={finalNodeStyle} key={gxLayer.id} />;
        // case 'scroll':
        // return <View style={finalNodeStyle} key={gxLayer.id} />;
    }

    private createViewOrTemplateNode(
        gxNode: GXNode,
        gxTemplateContext: GXTemplateContext,
        gxTemplateData: GXJSONObject,
        gxVisualTemplateNode: GXTemplateNode,
        gxLayer: GXJSONObject,
        gxTemplateInfo: GXTemplateInfo
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, gxVisualTemplateNode, gxNode);

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

                    const gxChildTemplateInfo = gxTemplateInfo.getChildTemplate(childLayer.id);

                    const gxChildVisualTemplateNode = GXTemplateNode.create(childLayer, gxTemplateInfo, null);

                    const gxChildLayer = gxChildTemplateInfo.layer;

                    // 容器模板下的子模板
                    if (gxNode.gxTemplateNode.isContainerType()) {

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

        gxNode.gxView = <View style={gxNode.gxTemplateNode.finalStyle} key={gxNode.gxId}>
            {childArray}
        </View>;
    }

    private createOtherNode(
        gxNode: GXNode,
        gxTemplateContext: GXTemplateContext,
        gxTemplateData: GXJSONObject
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxNode);
        const data = gxNode.gxTemplateNode.getData(gxTemplateData);
        gxNode.gxView = <View style={gxNode.gxTemplateNode.finalStyle} key={gxNode.gxId} />;
    }

    private createImageNode(
        gxNode: GXNode,
        gxTemplateContext: GXTemplateContext,
        gxTemplateData: GXJSONObject
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxNode);
        const data = gxNode.gxTemplateNode.getData(gxTemplateData);
        gxNode.gxView = <Image style={gxNode.gxTemplateNode.finalStyle} key={gxNode.gxId} src={data.value} />;
    }

    private createIconFontNode(
        gxNode: GXNode,
        gxTemplateContext: GXTemplateContext,
        gxTemplateData: GXJSONObject
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxNode);
        const data = gxNode.gxTemplateNode.getData(gxTemplateData);
        gxNode.gxView = <Text style={gxNode.gxTemplateNode.finalStyle} key={gxNode.gxId}> {data.value} </Text>;
    }

    private createRichTextNode(
        gxNode: GXNode,
        gxTemplateContext: GXTemplateContext,
        gxTemplateData: GXJSONObject
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxNode);
        const data = gxNode.gxTemplateNode.getData(gxTemplateData);
        gxNode.gxView = <Text style={gxNode.gxTemplateNode.finalStyle} key={gxNode.gxId}> {data.value} </Text>;
    }

    private createTextNode(
        gxNode: GXNode,
        gxTemplateContext: GXTemplateContext,
        gxTemplateData: GXJSONObject
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxNode);
        const data = gxNode.gxTemplateNode.getData(gxTemplateData);
        gxNode.gxView = <Text style={gxNode.gxTemplateNode.finalStyle} key={gxNode.gxId}> {data.value} </Text>;
    }
}