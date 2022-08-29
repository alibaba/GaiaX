import { View } from '@tarojs/components';
import _chunk from 'lodash/chunk'
import React, { CSSProperties, ReactNode } from 'react';
import { GXJSONArray } from '../../gaiax/GXJson';
import { GXNode } from '../../gaiax/GXNode';
import GXTemplateContext from '../../gaiax/GXTemplateContext';
import { GXEngineInstance, GXMeasureSize, GXTemplateComponent, GXTemplateData, GXTemplateInfo, GXTemplateItem } from '../..';
import GXTemplateNode, { GXGridConfig } from '../../gaiax/GXTemplateNode';
import GXCssConvertStyle from '../../gaiax/GXCssConvertStyle';

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
        let gxChildTemplateItem: GXTemplateItem = null;
        let gxChildVisualTemplateNode: GXTemplateNode = null;

        propGXNode.gxChildTemplateItems?.forEach((value, key) => {
            if (gxChildTemplateItem == null && gxChildVisualTemplateNode == null) {
                gxChildTemplateItem = key;
                gxChildVisualTemplateNode = value;
            }
        });

        // 获取数据
        let gxTemplateInfo: GXTemplateInfo = GXEngineInstance.gxData.getTemplateInfo(gxChildTemplateItem);

        const childItemWidth = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['width'];
        const childItemHeight = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['height'];

        const isHorizontal = gxGridConfig.direction == 'horizontal'
        const isVertical = gxGridConfig.direction == 'vertical'

        if (Array.isArray(propDataValue) && propDataValue.length === 0) {
            return null;
        }

        const dataSize = propDataValue.length;

        const gridColumn = gxGridConfig.column;

        const appendDataSize = gridColumn % dataSize;

        for (var i = 0; i < appendDataSize; i++) {
            const emptyData = {
                'gaiaxEmptyData': true
            };
            propDataValue.push(emptyData);
        }

        const gridGroup = _chunk(propDataValue, gridColumn);

        const gxGridStyle = GXCssConvertStyle.createGridStyleByConfig(gxStyle, propStyle, gxGridConfig);

        const groupViewsArray: ReactNode[] = [];
        const gridGroupSize = gridGroup.length;
        gridGroup.map((groupItem, groupItemIndex) => {

            const gxGridGroupStyle = {
                display: 'flex',
                marginBottom: '0px'
            }

            if (groupItemIndex != gridGroupSize - 1) {
                if (isVertical) {
                    if (gxGridConfig.rowSpacing != null) {
                        gxGridGroupStyle.marginBottom = gxGridConfig.rowSpacing;
                    }
                }
            }

            const groupItemViewsArray: ReactNode[] = [];
            const groupItemSize = groupItem.length;
            groupItem.map((childItem, childItemIndex) => {

                const gxGridGroupItemStyle = {
                    flex: `1 1 0`,
                    display: 'block',
                    marginRight: '0px'
                }

                if (childItemIndex != groupItemSize - 1) {
                    if (isVertical) {
                        if (gxGridConfig.itemSpacing != null) {
                            gxGridGroupItemStyle.marginRight = gxGridConfig.itemSpacing;
                        }
                    }
                }

                const gxTemplateItem = new GXTemplateItem();
                gxTemplateItem.templateBiz = gxChildTemplateItem.templateBiz;
                gxTemplateItem.templateId = gxChildTemplateItem.templateId;

                const gxTemplateData = new GXTemplateData();
                gxTemplateData.templateData = childItem;

                const gxMeasureSize = new GXMeasureSize();
                gxMeasureSize.templateWidth = childItemWidth;
                gxMeasureSize.templateHeight = childItemHeight;

                let groupItemView = null;
                if (childItem['gaiaxEmptyData'] != true) {
                    groupItemView = <View key={`gaiax-grid-group-item-${childItemIndex}`} style={gxGridGroupItemStyle} >
                        <GXTemplateComponent
                            gxTemplateData={gxTemplateData}
                            gxTemplateItem={gxTemplateItem}
                            gxMeasureSize={gxMeasureSize}
                            gxVisualTemplateNode={gxChildVisualTemplateNode} />
                    </View>;
                } else {
                    groupItemView = <View key={`gaiax-grid-group-item-${childItemIndex}`} style={gxGridGroupItemStyle} >
                    </View>;
                }

                groupItemViewsArray.push(groupItemView);
            });

            // group 
            const groupItemViews: ReactNode = <View style={gxGridGroupStyle} key={`gaiax-grid-group-${groupItemIndex}`}>
                {groupItemViewsArray}
            </View>

            groupViewsArray.push(groupItemViews);
        });

        return (
            <View style={gxGridStyle} key={`gaiax-grid`}>
                {groupViewsArray}
            </View>
        );
    }


}
