import { View } from '@tarojs/components';
import React, { ComponentType, CSSProperties, ReactNode } from 'react';
import { GXJSONArray } from '../../gaiax/GXJson';
import VirtualList from '@tarojs/components/virtual-list'
import { GXNode } from '../../gaiax/GXNode';
import GXTemplateContext from '../../gaiax/GXTemplateContext';
import GXUtils from '../../gaiax/GXUtils';
import { GXEngineInstance, GXTemplateInfo } from '../..';
import { logDOM } from '@testing-library/react';

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
    console.log(`id=${id} style=${style} data=${data} index=${index}`)
    return (
        <View style="width:100px;height:100px;background-color:black;" />
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

            return <VirtualList
                height={virtualListHeight}
                width={virtualListWidth}
                layout='horizontal'
                itemData={propDataValue}
                itemCount={propDataValue.length}
                itemSize={virtualListItemWidth}
            >
                {MemoGXScrollViewHolderFunctionComponent}
            </VirtualList>;
        } else {
            let childItemHeight = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['height'];
            let virtualListItemHeight = GXUtils.convertHeightToNumber(childItemHeight);

            return <VirtualList
                height={virtualListHeight}
                width={virtualListWidth}
                layout='vertical'
                itemData={propDataValue}
                itemCount={propDataValue.length}
                itemSize={virtualListItemHeight}
            >
                {MemoGXScrollViewHolderFunctionComponent}
            </VirtualList>;
        }
    }
}
