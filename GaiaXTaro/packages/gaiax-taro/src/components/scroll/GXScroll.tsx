import { ScrollView, View } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';
import { GXJSONArray, GXJSONObject } from '../../gaiax/GXJson';
import { GXNode } from '../../gaiax/GXNode';
import GXTemplateContext from '../../gaiax/GXTemplateContext';
import { GXEngineInstance } from '../../gaiax/GXEngineInstance';
import GXTemplateNode from '../../gaiax/GXTemplateNode';
import isArray from 'lodash/isArray';
import GXTemplateItem from '../../gaiax/GXTemplateItem';
import GXTemplateInfo from '../../gaiax/GXTemplateInfo';
import GXTemplateData from '../../gaiax/GXTemplateData';
import GXIEventListener from '../../gaiax/GXIEventListener';
import GXGesture from '../../gaiax/GXGesture';
import GXTemplateComponent from '../GXTemplateComponent';
import GXMeasureSize from '../../gaiax/GXMeasureSize';
import GXCssConvertStyle from '../../gaiax/GXCssConvertStyle';
import GXITrackListener from '../../gaiax/GXITrackListener';
import GXTrack from '../../gaiax/GXTrack';

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

        if (propDataValue == null || propDataValue == undefined || !isArray(propDataValue)) {
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
                const path = typeData['path'];
                const targetTemplateId: string = typeData['config'][path];
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

            // 获取数据
            let gxTemplateInfo: GXTemplateInfo = GXEngineInstance.gxData.getTemplateInfo(gxChildTemplateItem);

            const childItemWidth = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['width'];
            const childItemHeight = gxTemplateInfo.css[`#${gxTemplateInfo.layer['id']}`]['height'];

            const gxTemplateItem = new GXTemplateItem();
            gxTemplateItem.templateBiz = gxChildTemplateItem.templateBiz;
            gxTemplateItem.templateId = gxChildTemplateItem.templateId;
            gxTemplateItem.templatePrefixId = `${propGXNode.gxIdPath}-item-${itemIndex}`

            const gxTemplateData = new GXTemplateData();
            gxTemplateData.templateData = itemData;

            const gxEventListener = propGXTemplateContext.gxTemplateData.eventListener;
            if (gxEventListener != null) {
                const gxChildItemEventListener: GXIEventListener = {
                    onGestureEvent: (gxGesture: GXGesture) => {
                        gxGesture.index = itemIndex;
                        gxEventListener.onGestureEvent(gxGesture);
                    }
                }
                gxTemplateData.eventListener = gxChildItemEventListener;
            }

            const gxTrackListener = propGXTemplateContext.gxTemplateData.trackListener;
            if (gxTrackListener != null) {
                const gxChildItemTrackListener: GXITrackListener = {
                    onTrackEvent: (gxTrack: GXTrack) => {
                        gxTrack.index = itemIndex;
                        gxTrackListener.onTrackEvent(gxTrack);
                    }
                }
                gxTemplateData.trackListener = gxChildItemTrackListener;
            }

            const gxMeasureSize = new GXMeasureSize();
            gxMeasureSize.templateWidth = childItemWidth;
            gxMeasureSize.templateHeight = childItemHeight;

            let itemWrapStyle = GXCssConvertStyle.createScrollItemWrapStyleByConfig(isHorizontal, itemIndex, dataSize, gxScrollConfig, isVertical);

            childArray.push(
                <View
                    style={itemWrapStyle}
                    id={`${propGXNode.gxIdPath}-item-container-${itemIndex}`}
                    key={`${propGXNode.gxIdPath}-item-container-${itemIndex}`}
                >
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
                key={propGXNode.gxIdPath}
                id={propGXNode.gxIdPath}
                scrollX={isHorizontal}
                scrollY={isVertical}
                style={finalScrollStyle}
            >
                {childArray}
            </ScrollView>
        )
    }
}
