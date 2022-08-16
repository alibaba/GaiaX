import { View } from '@tarojs/components';
import _chunk from 'lodash/chunk'
import React, { CSSProperties, ReactNode } from 'react';
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

        const gxGridConfig = gxTemplateNode.finalGXGridConfig;

        const gxStyle = gxTemplateNode.finalStyle;

        // 容器的子坑位，目前只支持一个
        const gxChildTemplateItem = propGXNode.gxChildTemplateItems[0];

        // 获取数据
        let gxTemplateInfo: GXTemplateInfo = GXEngineInstance.gxData.getTemplateInfo(gxChildTemplateItem);

        const childItemWidth = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['width'];
        const childItemHeight = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['height'];

        const isHorizontal = gxGridConfig.direction == 'horizontal'
        const isVertical = gxGridConfig.direction == 'vertical'

        if (Array.isArray(propDataValue) && propDataValue.length === 0) {
            return null;
        }

        const gridColumn = gxGridConfig.column;

        const gridGroup = _chunk(propDataValue, gridColumn);

        const gaiaxGridStyle = {
            height: gxStyle.height,
            width: gxStyle.width,
            backgroundColor: '#00ff00',
            overflow: 'hidden'
        }

        const groupViewsArray: ReactNode[] = [];
        const gridGroupSize = gridGroup.length;
        gridGroup.map((groupItem, groupItemIndex) => {

            const gaiaxGridGroupStyle = {
                display: 'flex',
                marginBottom: '0px'
            }

            if (groupItemIndex != gridGroupSize - 1) {
                if (isVertical) {
                    if (gxGridConfig.rowSpacing != null) {
                        gaiaxGridGroupStyle.marginBottom = gxGridConfig.rowSpacing;
                    }
                }
            }

            const groupItemViewsArray: ReactNode[] = [];
            const groupItemSize = groupItem.length;
            groupItem.map((childItem, childItemIndex) => {

                const gaiaxGridGroupItemStyle = {
                    flex: `1 1 0`,
                    display: 'block',
                    marginRight: '0px'
                }
                
                if (childItemIndex != groupItemSize - 1) {
                    if (isVertical) {
                        if (gxGridConfig.itemSpacing != null) {
                            gaiaxGridGroupItemStyle.marginRight = gxGridConfig.itemSpacing;
                        }
                    }
                }

                const templateItem = new GXTemplateItem();
                templateItem.templateBiz = gxChildTemplateItem.templateBiz;
                templateItem.templateId = gxChildTemplateItem.templateId;

                const templateData = new GXTemplateData();
                templateData.templateData = childItem;

                const measureSize = new GXMeasureSize();
                measureSize.templateWidth = childItemWidth;
                measureSize.templateHeight = childItemHeight;

                // item
                const groupItemView = <View key={`gaiax-grid-group-item-${childItemIndex}`} style={gaiaxGridGroupItemStyle} >
                    <GXTemplateComponent templateData={templateData} templateItem={templateItem} measureSize={measureSize} />
                </View>;

                groupItemViewsArray.push(groupItemView);
            });

            // group 
            const groupItemViews: ReactNode = <View style={gaiaxGridGroupStyle} key={`gaiax-grid-group-${groupItemIndex}`}>
                {groupItemViewsArray}
            </View>

            groupViewsArray.push(groupItemViews);
        });

        return (
            <View style={gaiaxGridStyle} key={`gaiax-grid`}>
                {groupViewsArray}
            </View>
        );
    }
}
