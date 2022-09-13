import { View } from "@tarojs/components";
import React, { ReactNode } from "react";
import GXTemplateContext from "./GXTemplateContext";
import { GXNode } from "./GXNode";
import GXTemplateNode from "./GXTemplateNode";
import GXCssConvertStyle from "./GXCssConvertStyle";
import GXTemplateInfo from "./GXTemplateInfo";
import { GXJSONArray, GXJSONObject } from "./GXJson";
import GXTemplateItem from "./GXTemplateItem";
import GXView from "../components/view/GXView";
import GXImage from "../components/image/GXImage";
import GXText from "../components/text/GXText";
import GXRichText from "../components/richtext/GXRichText";
import GXIconFontText from "../components/iconfonttext/GXIconFontText";
import GXScroll from "../components/scroll/GXScroll";
import GXGrid from "../components/grid/GXGrid";
import GXGesture from "./GXGesture";
import GXTrack from "./GXTrack";

export default class GXViewTreeCreator {

    build(gxTemplateContext: GXTemplateContext): ReactNode {

        const gxTemplateData = gxTemplateContext.gxTemplateData.templateData as GXJSONObject;

        const gxTemplateInfo = gxTemplateContext.gxTemplateInfo;

        const gxLayer = gxTemplateInfo.layer;

        const gxParentNode = null;

        const gxVisualTemplateNode = gxTemplateContext.gxVisualTemplateNode;

        const gxVisualTemplateNodeData = null

        const gxRootNode = this.createNode(
            gxTemplateContext,
            gxTemplateInfo,
            gxLayer,
            gxTemplateData,
            gxParentNode,
            gxVisualTemplateNode,
            gxVisualTemplateNodeData
        );

        const gxRootStyle = GXCssConvertStyle.createRootStyle(gxTemplateContext.gxMeasureSize, gxRootNode, gxTemplateData);

        gxTemplateContext.gxRootStyle = gxRootStyle;

        let id = null;
        if (gxTemplateContext.gxTemplateItem.templatePrefixId == null) {
            id = `GXRoot-${gxTemplateContext.gxTemplateItem.templateId}`
        } else {
            id = `GXRoot-${gxTemplateContext.gxTemplateItem.templatePrefixId}-${gxTemplateContext.gxTemplateItem.templateId}`
        }

        return <View
            id={id}
            key={id}
            style={gxRootStyle}>{gxRootNode.gxView}</View>;
    }

    private createNode(
        gxTemplateContext: GXTemplateContext,
        gxTemplateInfo: GXTemplateInfo,
        gxLayer: GXJSONObject,
        gxTemplateData: GXJSONObject,
        gxParentNode?: GXNode,
        gxVisualTemplateNode?: GXTemplateNode,
        gxVisualTemplateNodeData?: GXJSONObject,

    ): GXNode {

        const gxNode = GXNode.create();

        gxNode.setIdPath(gxTemplateContext, gxLayer, gxParentNode);

        gxNode.gxTemplateNode = GXTemplateNode.create(gxLayer, gxTemplateInfo, gxVisualTemplateNode);

        // Container - Grid/Scroll
        if (gxNode.gxTemplateNode.isContainerType()) {
            this.createContainerNode(
                gxTemplateContext,
                gxNode,
                gxTemplateData,
                gxLayer,
                gxTemplateInfo,
                gxParentNode,
                gxVisualTemplateNodeData
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
                gxParentNode,
                gxVisualTemplateNodeData
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
        gxParentNode: GXNode,
        gxVisualTemplateNodeData?: GXJSONObject,
    ) {

        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, gxVisualTemplateNodeData, gxParentNode);

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
                            gxNode.gxChildTemplateItems = new Map<GXTemplateItem, GXTemplateNode>();
                        }

                        // 容器下的子模板
                        const childTemplateItem = GXTemplateItem.create(
                            gxTemplateContext.gxTemplateItem.templateBiz,
                            gxChildLayer.id
                        );

                        gxNode?.gxChildTemplateItems?.set(childTemplateItem, gxChildVisualTemplateNode);
                    }
                }
            }
        }

        if (gxNode.gxTemplateNode.isScrollType()) {
            this.createScrollNode(
                gxTemplateContext,
                gxNode,
                gxTemplateData
            );
        }
        else if (gxNode.gxTemplateNode.isGridType()) {
            this.createGridNode(
                gxTemplateContext,
                gxNode,
                gxTemplateData
            );
        }
    }

    private createGridNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject
    ) {
        const data = gxNode.gxTemplateNode.getData(gxTemplateData);
        gxNode.gxView = <GXGrid
            key={gxNode.gxId}
            propGXTemplateContext={gxTemplateContext}
            propGXNode={gxNode}
            propDataValue={data.value}
        />;
    }

    private createScrollNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject
    ) {
        const data = gxNode.gxTemplateNode.getData(gxTemplateData);
        gxNode.gxView = <GXScroll
            key={gxNode.gxId}
            propGXTemplateContext={gxTemplateContext}
            propGXNode={gxNode}
            propStyle={gxNode.gxTemplateNode.finalStyle}
            propDataValue={data.value}
        />;
    }

    private createViewOrTemplateNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject,
        gxLayer: GXJSONObject,
        gxTemplateInfo: GXTemplateInfo,
        gxParentNode: GXNode,
        gxVisualTemplateNodeData?: GXJSONObject,
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, gxVisualTemplateNodeData, gxParentNode);

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
                            gxNode.gxChildTemplateItems = new Map<GXTemplateItem, GXTemplateNode>();
                        }

                        // 容器下的子模板
                        const childTemplateItem = GXTemplateItem.create(
                            gxTemplateContext.gxTemplateItem.templateBiz,
                            gxChildLayer.id
                        );

                        gxNode?.gxChildTemplateItems?.set(childTemplateItem, gxChildVisualTemplateNode);
                    }
                    // 普通模板嵌套的子模板根节点，可能是普通模板也可能是容器模板
                    else {

                        // 获取嵌套子模板的数据，会传给下一级模板当做数据源
                        const gxChildTemplateData = gxChildVisualTemplateNode.getDataValue(gxTemplateData);

                        // 使用原有数据源作为虚拟节点的数据源
                        const gxChildVisualTemplateNodeData = gxTemplateData;

                        const childNode = this.createNode(
                            gxTemplateContext,
                            gxChildTemplateInfo,
                            gxChildLayer,
                            gxChildTemplateData,
                            gxNode,
                            gxChildVisualTemplateNode,
                            gxChildVisualTemplateNodeData
                        );

                        gxNode?.gxChildren?.push(childNode);

                        childArray.push(childNode.gxView);
                    }
                }
                // 普通子节点
                else {
                    const childNode = this.createNode(
                        gxTemplateContext,
                        gxTemplateInfo,
                        childLayer,
                        gxTemplateData,
                        gxNode,
                        null
                    );

                    gxNode?.gxChildren?.push(childNode);

                    childArray.push(childNode.gxView);
                }
            }
        }

        gxNode.gxView = <GXView
            onClick={this.createEvent(gxTemplateContext, gxNode, gxTemplateData)?.bind(this)}
            propStyle={gxNode.gxTemplateNode.finalStyle}
            propGXNode={gxNode}
            key={gxNode.gxId}>
            {childArray}
        </GXView>;

        this.sendTrack(gxTemplateContext, gxNode, gxTemplateData);
    }

    private sendTrack(gxTemplateContext: GXTemplateContext, gxNode: GXNode, gxTemplateData: GXJSONObject) {
        const gxTrackListener = gxTemplateContext.gxTemplateData?.trackListener;
        const gxEvent = gxNode.gxTemplateNode?.event;
        if (gxTrackListener != null && gxEvent != null) {
            const gxTrack = GXTrack.create(
                gxTemplateContext,
                gxTemplateData,
                gxNode
            );
            gxTrackListener.onTrackEvent(gxTrack);
        }
    }

    private createEvent(gxTemplateContext: GXTemplateContext, gxNode: GXNode, gxTemplateData: GXJSONObject) {
        const gxEventListener = gxTemplateContext.gxTemplateData?.eventListener;
        const gxEvent = gxNode.gxTemplateNode?.event;
        let gxEventClick = null;
        if (gxEventListener != null && gxEvent != null) {
            gxEventClick = (args) => {
                const gxGesture = GXGesture.create(
                    gxTemplateContext,
                    gxTemplateData,
                    gxNode
                );
                gxEventListener.onGestureEvent(gxGesture);
            };
        }
        return gxEventClick;
    }

    private createOtherNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject,
        gxParentNode?: GXNode
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxParentNode);

        const data = gxNode.gxTemplateNode.getData(gxTemplateData);

        gxNode.gxView = <GXView
            onClick={this.createEvent(gxTemplateContext, gxNode, gxTemplateData)?.bind(this)}
            propStyle={gxNode.gxTemplateNode.finalStyle}
            propGXNode={gxNode}
            key={gxNode.gxId}
        />;

        this.sendTrack(gxTemplateContext, gxNode, gxTemplateData);
    }

    private createImageNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject,
        gxParentNode?: GXNode
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxParentNode);

        const data = gxNode.gxTemplateNode.getData(gxTemplateData);

        gxNode.gxView = <GXImage
            propMode={gxNode.gxTemplateNode.imageMode}
            onClick={this.createEvent(gxTemplateContext, gxNode, gxTemplateData)?.bind(this)}
            propStyle={gxNode.gxTemplateNode.finalStyle}
            key={gxNode.gxId}
            propGXNode={gxNode}
            propDataValue={data.value}
        />;

        this.sendTrack(gxTemplateContext, gxNode, gxTemplateData);
    }

    private createIconFontNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject,
        gxParentNode?: GXNode
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxParentNode);

        const data = gxNode.gxTemplateNode.getData(gxTemplateData);

        gxNode.gxView = <GXIconFontText
            onClick={this.createEvent(gxTemplateContext, gxNode, gxTemplateData)?.bind(this)}
            propStyle={gxNode.gxTemplateNode.finalStyle}
            key={gxNode.gxId}
            propGXNode={gxNode}
            propDataValue={data.value}
        />;

        this.sendTrack(gxTemplateContext, gxNode, gxTemplateData);
    }

    private createRichTextNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject,
        gxParentNode?: GXNode
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxParentNode);

        const data = gxNode.gxTemplateNode.getData(gxTemplateData);

        const extend = gxNode.gxTemplateNode.getExtend(gxTemplateData);

        gxNode.gxView = <GXRichText
            onClick={this.createEvent(gxTemplateContext, gxNode, gxTemplateData)?.bind(this)}
            propStyle={gxNode.gxTemplateNode.finalStyle}
            key={gxNode.gxId}
            propGXNode={gxNode}
            propDataValue={data.value}
            propExtend={extend}
        />;

        this.sendTrack(gxTemplateContext, gxNode, gxTemplateData);
    }

    private createTextNode(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: GXJSONObject,
        gxParentNode?: GXNode
    ) {
        gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxParentNode);

        const data = gxNode.gxTemplateNode.getData(gxTemplateData);

        gxNode.gxView = <GXText
            onClick={this.createEvent(gxTemplateContext, gxNode, gxTemplateData)?.bind(this)}
            propStyle={gxNode.gxTemplateNode.finalStyle}
            propGXNode={gxNode}
            key={gxNode.gxId}
            propDataValue={data.value}
        />;

        this.sendTrack(gxTemplateContext, gxNode, gxTemplateData);
    }
}