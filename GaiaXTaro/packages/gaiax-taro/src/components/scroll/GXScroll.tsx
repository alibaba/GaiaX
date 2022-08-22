import { ScrollView, View } from '@tarojs/components';
import React, { ComponentType, CSSProperties, ReactNode } from 'react';
import { GXJSONArray } from '../../gaiax/GXJson';
import { GXNode } from '../../gaiax/GXNode';
import GXTemplateContext from '../../gaiax/GXTemplateContext';
import { GXEngineInstance, GXGesture, GXIEventListener, GXMeasureSize, GXTemplateComponent, GXTemplateData, GXTemplateInfo, GXTemplateItem } from '../..';
import GXTemplateNode from '../../gaiax/GXTemplateNode';

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

        if (propDataValue == null || propDataValue == undefined) {
            console.error("GXScroll propDataValue is null")
            return null
        }

        const gxTemplateNode = propGXNode.gxTemplateNode;

        const gxScrollConfig = gxTemplateNode.finalGXScrollConfig;

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
                style={scrollStyle}
            >
                {childArray}
            </ScrollView>
        )
    }
}
