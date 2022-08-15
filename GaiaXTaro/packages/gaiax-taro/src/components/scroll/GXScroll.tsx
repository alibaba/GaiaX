import { View } from '@tarojs/components';
import React, { ComponentType, CSSProperties, ReactNode } from 'react';
import { GXJSONArray } from '../../gaiax/GXJson';
import VirtualList from '@tarojs/components/virtual-list'
import { GXNode } from '../../gaiax/GXNode';
import GXTemplateContext from '../../gaiax/GXTemplateContext';
import GXUtils from '../../gaiax/GXUtils';
import { GXEngineInstance, GXMeasureSize, GXTemplateComponent, GXTemplateData, GXTemplateInfo, GXTemplateItem } from '../..';

export interface GXScrollState {

}

export interface GXScrollProps {
    propStyle?: string | CSSProperties
    propDataValue?: GXJSONArray
    propGXTemplateContext: GXTemplateContext
    propGXNode: GXNode
}


type GXScrollViewHolderProps = {
    id: string
    style?: CSSProperties
    data: any
    index: number
}

const GXScrollViewHolderFunctionComponent: React.FunctionComponent<GXScrollViewHolderProps> = ({ id, style, data, index }) => {
    const itemData = data[index];

    console.log(itemData)

    const gaiaxItemParams = itemData["gaiaxItemParams"];

    const childItemSpacing = `${(gaiaxItemParams["childItemSpacing"] || 0)}px`;

    const childTemplateItem: GXTemplateItem = gaiaxItemParams["childTemplateItem"];
    let templateItem = new GXTemplateItem();
    templateItem.templateBiz = childTemplateItem.templateBiz;
    templateItem.templateId = childTemplateItem.templateId;

    let templateData = new GXTemplateData();
    templateData.templateData = itemData;

    let measureSize = new GXMeasureSize();
    measureSize.templateWidth = gaiaxItemParams["childItemWidth"];
    measureSize.templateHeight = gaiaxItemParams["childItemHeight"];

    return (
        <GXTemplateComponent templateData={templateData} templateItem={templateItem} measureSize={measureSize} />
    );
};

const MemoGXScrollViewHolderFunctionComponent = React.memo(GXScrollViewHolderFunctionComponent);

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

        let virtualListWidth = GXUtils.convertWidthToNumber(gxStyle.width + '');
        let virtualListHeight = GXUtils.convertHeightToNumber(gxStyle.height + '');

        // 容器的子坑位，目前只支持一个
        const gxChildTemplateItem = propGXNode.gxChildTemplateItems[0];

        // 获取数据
        let gxTemplateInfo: GXTemplateInfo = GXEngineInstance.gxData.getTemplateInfo(gxChildTemplateItem);

        if (gxScrollConfig.direction == 'horizontal') {

            let childItemWidth = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['width'];
            let virtualListItemWidth = GXUtils.convertWidthToNumber(childItemWidth);
            let virtualListItemRealWidth = virtualListItemWidth + Number.parseInt(gxScrollConfig.itemSpacing);

            propDataValue.forEach(itemData => {
                itemData["gaiaxItemParams"] = {
                    "childItemWidth": virtualListItemWidth,
                    "childTemplateItem": gxChildTemplateItem,
                    "childItemSpacing": gxScrollConfig.itemSpacing,
                };
            });

            return <VirtualList
                height={virtualListHeight}
                width={virtualListWidth}
                layout='horizontal'
                itemData={propDataValue}
                itemCount={propDataValue.length}
                itemSize={virtualListItemRealWidth}
            >
                {MemoGXScrollViewHolderFunctionComponent}
            </VirtualList>;
        } else {
            let childItemHeight = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['height'];
            let virtualListItemHeight = GXUtils.convertHeightToNumber(childItemHeight);
            let virtualListItemRealHeight = virtualListItemHeight + Number.parseInt(gxScrollConfig.itemSpacing);

            propDataValue.forEach(itemData => {
                itemData["gaiaxItemParams"] = {
                    "childItemHeight": virtualListItemHeight,
                    "childTemplateItem": gxChildTemplateItem,
                    "childItemSpacing": gxScrollConfig.itemSpacing,
                };
            });

            return <VirtualList
                height={virtualListHeight}
                width={virtualListWidth}
                layout='vertical'
                itemData={propDataValue}
                itemCount={propDataValue.length}
                itemSize={virtualListItemRealHeight}
            >
                {MemoGXScrollViewHolderFunctionComponent}
            </VirtualList>;
        }
    }
}
