import { ScrollView, View } from '@tarojs/components';
import React, { ComponentType, CSSProperties, ReactNode } from 'react';
import { GXJSONArray } from '../../gaiax/GXJson';
import { GXNode } from '../../gaiax/GXNode';
import GXTemplateContext from '../../gaiax/GXTemplateContext';
import { GXEngineInstance, GXMeasureSize, GXTemplateComponent, GXTemplateData, GXTemplateInfo, GXTemplateItem } from '../..';

export interface GXScrollState {

}

export interface GXScrollProps {
    propStyle?: string | CSSProperties
    propDataValue?: GXJSONArray
    propGXTemplateContext: GXTemplateContext
    propGXNode: GXNode
}

export default class GXScroll extends React.Component<GXScrollProps, GXScrollState> {
    render() {
        const {
            propStyle,
            propGXTemplateContext,
            propGXNode,
            propDataValue
        } = this.props

        const gxTemplateNode = propGXNode.gxTemplateNode;

        const gxScrollConfig = gxTemplateNode.finalGXScrollConfig;

        const gxStyle = gxTemplateNode.finalStyle;

        // 容器的子坑位，目前只支持一个
        const gxChildTemplateItem = propGXNode.gxChildTemplateItems[0];

        // 获取数据
        let gxTemplateInfo: GXTemplateInfo = GXEngineInstance.gxData.getTemplateInfo(gxChildTemplateItem);

        const scrollStyle = {
            height: gxStyle.height,
            width: gxStyle.width,
            marginTop: '',
            marginLeft: '',
            marginRight: '',
            marginBottom: '',
            display: ''
        }

        // 和native保持一致
        // edge-insets
        if (gxScrollConfig.edgeInsetsTop != null) {
            scrollStyle.marginTop = gxScrollConfig.edgeInsetsTop;
        }
        if (gxScrollConfig.edgeInsetsLeft != null) {
            scrollStyle.marginLeft = gxScrollConfig.edgeInsetsLeft;
        }
        if (gxScrollConfig.edgeInsetsRight != null) {
            scrollStyle.marginRight = gxScrollConfig.edgeInsetsRight;
        }
        if (gxScrollConfig.edgeInsetsBottom != null) {
            scrollStyle.marginBottom = gxScrollConfig.edgeInsetsBottom;
        }

        const isHorizontal = gxScrollConfig.direction == 'horizontal'
        const isVertical = gxScrollConfig.direction == 'vertical'

        // 此处可能要根据多平台适配
        if (isHorizontal) {
            // scrollStyle.display = '-webkit-inline-box';
            scrollStyle.display = 'inline-flex';
        } else {
            scrollStyle.display = '';
        }

        const childItemWidth = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['width'];
        const childItemHeight = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['height'];

        const childArray: ReactNode[] = [];

        const dataSize = propDataValue.length
        propDataValue.forEach((itemData, itemIndex) => {

            const templateItem = new GXTemplateItem();
            templateItem.templateBiz = gxChildTemplateItem.templateBiz;
            templateItem.templateId = gxChildTemplateItem.templateId;

            const templateData = new GXTemplateData();
            templateData.templateData = itemData;

            const measureSize = new GXMeasureSize();
            measureSize.templateWidth = childItemWidth;
            measureSize.templateHeight = childItemHeight;

            let itemWrapStyle = {
                marginRight: '0px',
                marginBottom: '0px'
            }

            if (itemIndex != dataSize - 1) {
                if (isHorizontal) {
                    if (gxScrollConfig.itemSpacing != null) {
                        itemWrapStyle.marginRight = gxScrollConfig.itemSpacing;
                    }

                } else if (isVertical) {
                    if (gxScrollConfig.itemSpacing != null) {
                        itemWrapStyle.marginBottom = gxScrollConfig.itemSpacing;
                    }
                }
            }

            childArray.push(
                <View style={itemWrapStyle} id={`${templateItem.templateId}-${itemIndex}`}>
                    <GXTemplateComponent templateData={templateData} templateItem={templateItem} measureSize={measureSize} />
                </View>
            );
        });

        return (
            <ScrollView
                scrollX={isHorizontal}
                scrollY={isVertical}
                style={scrollStyle}
            >
                {childArray}
            </ScrollView>
        )
    }
}
