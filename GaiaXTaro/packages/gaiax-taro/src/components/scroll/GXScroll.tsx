import { ScrollView, View } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';
import { GXJSONArray, GXJSONObject } from '../../gaiax/GXJson';
import { GXNode } from '../../gaiax/GXNode';
import GXTemplateContext from '../../gaiax/GXTemplateContext';
import { GXEngineInstance } from '../../gaiax/GXEngineInstance';
import GXTemplateNode, { GXScrollConfig } from '../../gaiax/GXTemplateNode';
import _isArray from 'lodash/isArray';
import GXTemplateItem from '../../gaiax/GXTemplateItem';
import GXTemplateInfo from '../../gaiax/GXTemplateInfo';
import GXTemplateData from '../../gaiax/GXTemplateData';
import GXIEventListener from '../../gaiax/GXIEventListener';
import GXGesture from '../../gaiax/GXGesture';
import GXTemplateComponent from '../GXTemplateComponent';
import GXMeasureSize from '../../gaiax/GXMeasureSize';

import GXCssConvertStyle from '../../gaiax/GXCssConvertStyle';

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
            propDataValue,
        } = this.props

        if (propDataValue == null || propDataValue == undefined || !_isArray(propDataValue)) {
            console.error("GXScroll propDataValue is null")
            return null
        }

        const gxTemplateNode = propGXNode.gxTemplateNode;

        const gxScrollConfig = gxTemplateNode.finalGXScrollConfig;

        const gxStyle = gxTemplateNode.finalStyle;

        const isHorizontal = gxScrollConfig.direction == 'horizontal';
        const isVertical = gxScrollConfig.direction == 'vertical';

        const finalScrollStyle = GXCssConvertStyle.createScrollStyleByConfig(gxStyle, gxScrollConfig);

        const childArray: ReactNode[] = [];

        const dataSize = propDataValue.length
        propDataValue.forEach((itemData, itemIndex) => {

            let gxChildTemplateItem: GXTemplateItem = null;
            let gxChildVisualTemplateNode: GXTemplateNode = null;

            if (propGXNode.gxChildTemplateItems?.size > 1) {
                const typeData = gxTemplateNode.getExtend(itemData as GXJSONObject)['item-type'];
                const targetTemplateId: string = typeData['config'][typeData['path']];
                console.log(typeData)
                propGXNode.gxChildTemplateItems?.forEach((value, key) => {
                    if (key.templateId == targetTemplateId) {
                        gxChildTemplateItem = key;
                        gxChildVisualTemplateNode = value;
                    }

                });
            }

            if (gxChildTemplateItem == null && gxChildVisualTemplateNode == null) {
                propGXNode.gxChildTemplateItems?.forEach((value, key) => {
                    if (gxChildTemplateItem == null && gxChildVisualTemplateNode == null) {
                        gxChildTemplateItem = key;
                        gxChildVisualTemplateNode = value;
                    }
                });
            }

            console.log(gxChildTemplateItem)
            // 获取数据
            let gxTemplateInfo: GXTemplateInfo = GXEngineInstance.gxData.getTemplateInfo(gxChildTemplateItem);

            const childItemWidth = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['width'];
            const childItemHeight = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['height'];

            const gxTemplateItem = new GXTemplateItem();
            gxTemplateItem.templateBiz = gxChildTemplateItem.templateBiz;
            gxTemplateItem.templateId = gxChildTemplateItem.templateId;

            const gxTemplateData = new GXTemplateData();
            gxTemplateData.templateData = itemData;

            const gxEventListener = propGXTemplateContext.gxTemplateData.eventListener
            if (gxEventListener != null) {
                const gxChildItemEventListener: GXIEventListener = {
                    onGestureEvent: function (gxGesture: GXGesture) {
                        gxGesture.index = itemIndex;
                        gxEventListener.onGestureEvent(gxGesture);
                    }
                }
                gxTemplateData.eventListener = gxChildItemEventListener;
            }

            const gxMeasureSize = new GXMeasureSize();
            gxMeasureSize.templateWidth = childItemWidth;
            gxMeasureSize.templateHeight = childItemHeight;

            let itemWrapStyle = GXCssConvertStyle.createScrollItemWrapStyleByConfig(isHorizontal, itemIndex, dataSize, gxScrollConfig, isVertical);

            childArray.push(
                <View style={itemWrapStyle} id={`${gxTemplateItem.templateId}-${itemIndex}`}>
                    <GXTemplateComponent
                        gxTemplateData={gxTemplateData}
                        gxTemplateItem={gxTemplateItem}
                        gxMeasureSize={gxMeasureSize}
                        gxVisualTemplateNode={gxChildVisualTemplateNode} />
                </View>
            );
        });

        return (
            <ScrollView
                scrollX={isHorizontal}
                scrollY={isVertical}
                style={finalScrollStyle}
            >
                {childArray}
            </ScrollView>
        )
    }
}
