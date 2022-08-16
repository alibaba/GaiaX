import { View } from '@tarojs/components';
import _chunk from 'lodash/chunk'
import React, { CSSProperties } from 'react';
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

        // if (Array.isArray(propDataValue) && propDataValue.length === 0) {
            return null;
        // }

        // const gridColumn = gxGridConfig.column;

        // const gridGroup = _chunk(propDataValue, gridColumn);

        // const gaiaxGridStyle = {
        //     height: gxStyle.height,
        //     width: gxStyle.width,
        //     backgroundColor: '#00ff00'
        // }

        // const gaiaxGridGroupStyle = {
        //     display: 'flex'
        // }

        // const gaiaxGridGroupItemStyle = {
        //     flex: `0 0 ${100 / gridColumn}%`,
        //     display: 'block',
        //     backgroundColor: '#ff00ff'
        // }

        // // const isHorizontal = gxScrollConfig.direction == 'horizontal'
        // // const isVertical = gxScrollConfig.direction == 'vertical'
        // // const itemSpacing = gxScrollConfig.itemSpacing

        // // const childItemWidth = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['width'];
        // // const childItemHeight = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['height'];

        // // const childArray: ReactNode[] = [];

        // // const dataSize = propDataValue.length
        // // propDataValue.forEach((itemData, itemIndex) => {

        // //     const templateItem = new GXTemplateItem();
        // //     templateItem.templateBiz = gxChildTemplateItem.templateBiz;
        // //     templateItem.templateId = gxChildTemplateItem.templateId;

        // //     const templateData = new GXTemplateData();
        // //     templateData.templateData = itemData;

        // //     const measureSize = new GXMeasureSize();
        // //     measureSize.templateWidth = childItemWidth;
        // //     measureSize.templateHeight = childItemHeight;

        // //     let itemWrapStyle = {
        // //         marginRight: '0px'
        // //     }

        // //     if (itemIndex != dataSize - 1) {
        // //         itemWrapStyle.marginRight = itemSpacing;
        // //     }

        // //     childArray.push(
        // //         <View style={itemWrapStyle}>
        // //             <GXTemplateComponent templateData={templateData} templateItem={templateItem} measureSize={measureSize} />
        // //         </View>
        // //     );
        // // });

        // return (
        //     <View style={gaiaxGridStyle} key={`gaiax-grid`}>
        //         {gridGroup.map((item, i) => (
        //             <View style={gaiaxGridGroupStyle} key={`gaiax-grid-group-${i}`}>
        //                 {item.map((childItem, index) => (
        //                     <View
        //                         key={`gaiax-grid-group-item-${index}`}
        //                         style={gaiaxGridGroupItemStyle}
        //                     >

        //                     </View>
        //                 ))}
        //             </View>
        //         ))}
        //     </View>
        // )
    }
}
