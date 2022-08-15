import { ScrollView, View } from '@tarojs/components';
import React, { ComponentType, CSSProperties, ReactNode } from 'react';
import { GXJSONArray } from '../../gaiax/GXJson';
import { GXNode } from '../../gaiax/GXNode';
import GXTemplateContext from '../../gaiax/GXTemplateContext';
import { GXEngineInstance, GXMeasureSize, GXTemplateComponent, GXTemplateData, GXTemplateInfo, GXTemplateItem } from '../..';

export interface GXGridState {

}

export interface GXGridProps {
    propStyle?: string | CSSProperties
    propDataValue?: GXJSONArray
    propGXTemplateContext: GXTemplateContext
    propGXNode: GXNode
}

export default class GXGrid extends React.Component<GXGridProps, GXGridState> {
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
            width: gxStyle.width
        }

        const isHorizontal = gxScrollConfig.direction == 'horizontal'
        const isVertical = gxScrollConfig.direction == 'vertical'
        const itemSpacing = gxScrollConfig.itemSpacing

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
                marginRight: '0px'
            }

            if (itemIndex != dataSize - 1) {
                itemWrapStyle.marginRight = itemSpacing;
            }

            childArray.push(
                <View style={itemWrapStyle}>
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
