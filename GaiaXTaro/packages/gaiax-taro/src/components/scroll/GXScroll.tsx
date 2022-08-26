import { ScrollView, View } from '@tarojs/components';
import React, { CSSProperties, ReactNode } from 'react';
import { GXJSONArray } from '../../gaiax/GXJson';
import { GXNode } from '../../gaiax/GXNode';
import GXTemplateContext from '../../gaiax/GXTemplateContext';
import { GXEngineInstance } from '../../gaiax/GXEngineInstance';
import GXTemplateNode from '../../gaiax/GXTemplateNode';
import _isArray from 'lodash/isArray';
import GXTemplateItem from '../../gaiax/GXTemplateItem';
import GXTemplateInfo from '../../gaiax/GXTemplateInfo';
import GXTemplateData from '../../gaiax/GXTemplateData';
import GXIEventListener from '../../gaiax/GXIEventListener';
import GXGesture from '../../gaiax/GXGesture';
import GXTemplateComponent from '../GXTemplateComponent';
import GXMeasureSize from '../../gaiax/GXMeasureSize';
import kebabCase from 'lodash/kebabcase';

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

        if (propDataValue == null || propDataValue == undefined || !_isArray(propDataValue)) {
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
            height: gxStyle.height + '',
            width: gxStyle.width + '',
            flexShrink: '1',
            flexGrow: '1',
            paddingTop: '',
            paddingLeft: '',
            paddingRight: '',
            paddingBottom: '',
            display: '',
            "white-space": ""
        }

        // 和native保持一致
        // edge-insets
        if (gxScrollConfig.edgeInsetsTop != null) {
            scrollStyle.paddingTop = gxScrollConfig.edgeInsetsTop;
        }
        if (gxScrollConfig.edgeInsetsLeft != null) {
            scrollStyle.paddingLeft = gxScrollConfig.edgeInsetsLeft;
        }
        if (gxScrollConfig.edgeInsetsRight != null) {
            scrollStyle.paddingRight = gxScrollConfig.edgeInsetsRight;
        }
        if (gxScrollConfig.edgeInsetsBottom != null) {
            scrollStyle.paddingBottom = gxScrollConfig.edgeInsetsBottom;
        }

        // 处理Scroll的左右内边距的情况
        if ((scrollStyle.paddingLeft != '' || scrollStyle.paddingRight != '') && scrollStyle.width == '100%') {
            scrollStyle.width = 'auto'
        }

        const isHorizontal = gxScrollConfig.direction == 'horizontal'
        const isVertical = gxScrollConfig.direction == 'vertical'

        // 此处可能要根据多平台适配
        if (isHorizontal) {
            // https://taro-docs.jd.com/taro/docs/components/viewContainer/scroll-view
            if (process.env.TARO_ENV === 'h5') {
                scrollStyle.display = 'inline-flex';
            } else if (process.env.TARO_ENV === 'weapp') {
                scrollStyle["white-space"] = "nowrap";
            }
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
                marginBottom: '0px',
                display: ''
            }

            if (isHorizontal) {
                if (process.env.TARO_ENV === 'weapp') {
                    itemWrapStyle.display = 'inline-block';
                }
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

        const finalScrollStyle = Object.keys(scrollStyle).reduce((accumulator, key) => {
            const cssKey = kebabCase(key)
            const cssValue = scrollStyle[key].replace("'", "")
            return `${accumulator}${cssKey}:${cssValue};`
        }, '')

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
