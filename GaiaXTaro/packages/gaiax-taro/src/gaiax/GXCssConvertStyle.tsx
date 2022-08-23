import { GXJSONObject } from "./GXJson";
import { GXNode } from "./GXNode";
import GXTemplateContext from "./GXTemplateContext";
import GXMeasureSize from "./GXMeasureSize";
import GXTemplateNode from "./GXTemplateNode";

export default class GXCssConvertStyle {

    static createRootStyle(gxMeasureSize: GXMeasureSize): React.CSSProperties {
        const rootStyle = {
            display: 'flex',
            position: 'relative',
            direction: 'inherit',
            flexDirection: 'row',
            flexWrap: 'nowrap',
            overflow: 'hidden',
            alignItems: 'stretch',
            alignSelf: 'auto',
            alignContent: 'flex-start',
            justifyContent: 'flex-start',
            flexShrink: '0',
            flexGrow: '0',
            width: '100%',
            height: 'auto',
            minWidth: 'auto',
            minHeight: 'auto',
            maxWidth: 'auto',
            maxHeight: 'auto'
        };

        // 处理外部期望的宽度
        if (gxMeasureSize.templateWidth != undefined && gxMeasureSize.templateWidth != null) {
            rootStyle.width = gxMeasureSize.templateWidth + 'px';
        }

        // 处理外部期望的高度
        if (gxMeasureSize.templateHeight != undefined && gxMeasureSize.templateHeight != null) {
            rootStyle.height = gxMeasureSize.templateHeight + 'px';
        }

        return rootStyle as React.CSSProperties;
    }

    static createViewStyleByCss(
        gxTemplateContext: GXTemplateContext,
        layer: GXJSONObject,
        css: GXJSONObject,
        gxTemplateNode: GXTemplateNode,
        gxParentNode?: GXNode
    ): React.CSSProperties {
        let style = {
            display: 'flex',
            position: "relative",
            direction: 'inherit',
            flexDirection: 'row',
            flexWrap: 'nowrap',
            overflow: 'hidden',
            alignItems: 'stretch',
            alignSelf: 'auto',
            alignContent: 'flex-start',
            justifyContent: 'flex-start',
            flexShrink: '1',
            flexGrow: '0',
            // flexBasis: 'auto',
            // paddingLeft: '0px',
            // paddingTop: '0px',
            // paddingRight: '0px',
            // paddingBottom: '0px',
            // marginLeft: '0px',
            // marginTop: '0px',
            // marginRight: '0px',
            // marginBottom: '0px',
            // left: '0px',
            // top: '0px',
            // right: '0px',
            // bottom: '0px',
            width: 'auto',
            height: 'auto',
            minWidth: 'auto',
            minHeight: 'auto',
            maxWidth: 'auto',
            maxHeight: 'auto',
            // // 无法使用
            // aspectRatio: '',
            // // 
            // backgroundColor: '',
            // fontSize: '',
            // fontFamily: 'unset',
            // color: '',
            // fontWeight: 'unset',
            // backgroundImage: 'unset',
            // maxLines: 'unset',
            // textOverflow: 'unset',
            // textAlign: 'unset',
            // borderColor: 'unset',
            // borderWidth: '0px',
            // borderTopLeftRadius: '0px',
            // borderTopRightRadius: '0px',
            // borderBottomLeftRadius: '0px',
            // borderBottomRightRadius: '0px',
            // lineHeight: '',
            // textDecoration: '',
            // mode: '',
        };
        this.updateViewStyleByCss(gxTemplateContext, style, layer, css, gxTemplateNode, gxParentNode);
        return style as React.CSSProperties;
    }

    static updateViewStyleByCss(
        gxTemplateContext: GXTemplateContext,
        targetStyle: any,
        srcLayer: GXJSONObject,
        srcCss: GXJSONObject,
        gxTemplateNode: GXTemplateNode,
        gxParentNode?: GXNode
    ) {

        //
        // Layout
        // 

        let display = srcCss['display'];
        if (display != undefined) {
            targetStyle.display = display;
        }

        let direction = srcCss['direction'];
        if (direction != undefined) {
            targetStyle.direction = direction;
        }

        let flexDirection = srcCss['flex-direction'];
        if (flexDirection != undefined) {
            targetStyle.flexDirection = flexDirection;
        }

        let flexWrap = srcCss['flex-wrap'];
        if (flexWrap != undefined) {
            targetStyle.flexWrap = flexWrap;
        }

        let overflow = srcCss['overflow'];
        if (overflow != undefined) {
            targetStyle.overflow = overflow;
        }

        let alignItems = srcCss['align-items'];
        if (alignItems != undefined) {
            targetStyle.alignItems = alignItems;
        }

        let alignSelf = srcCss['align-self'];
        if (alignSelf != undefined) {
            targetStyle.alignSelf = alignSelf;
        }

        let alignContent = srcCss['align-content'];
        if (alignContent != undefined) {
            targetStyle.alignContent = alignContent;
        }

        let justifyContent = srcCss['justify-content'];
        if (justifyContent != undefined) {
            targetStyle.justifyContent = justifyContent;
        }

        let flexGrow = srcCss['flex-grow'];
        if (flexGrow != undefined) {
            targetStyle.flexGrow = flexGrow;
        }

        let flexShrink = srcCss['flex-shrink'];

        if (flexShrink != undefined) {
            targetStyle.flexShrink = flexShrink;
        }

        let flexBasis = srcCss['flex-basis'];
        if (flexBasis != undefined) {
            targetStyle.flexBasis = flexBasis;
        }

        let position = srcCss['position'];
        if (position != undefined) {
            targetStyle.position = position;
        }

        let left = srcCss['left'];
        if (left != undefined) {
            targetStyle.left = left;
        }

        let right = srcCss['right'];
        if (right != undefined) {
            targetStyle.right = right;
        }

        let top = srcCss['top'];
        if (top != undefined) {
            targetStyle.top = top;
        }

        let bottom = srcCss['bottom'];
        if (bottom != undefined) {
            targetStyle.bottom = bottom;
        }

        let marginLeft = srcCss['margin-left'];
        if (marginLeft != undefined) {
            targetStyle.marginLeft = marginLeft;
        }

        let marginTop = srcCss['margin-top'];
        if (marginTop != undefined) {
            targetStyle.marginTop = marginTop;
        }

        let marginRight = srcCss['margin-right'];
        if (marginRight != undefined) {
            targetStyle.marginRight = marginRight;
        }

        let marginBottom = srcCss['margin-bottom'];
        if (marginBottom != undefined) {
            targetStyle.marginBottom = marginBottom;
        }

        let paddingLeft = srcCss['padding-left'];
        if (paddingLeft != undefined) {
            targetStyle.paddingLeft = paddingLeft;
        }

        let paddingTop = srcCss['padding-top'];
        if (paddingTop != undefined) {
            targetStyle.paddingTop = paddingTop;
        }

        let paddingRight = srcCss['padding-right'];
        if (paddingRight != undefined) {
            targetStyle.paddingRight = paddingRight;
        }

        let paddingBottom = srcCss['padding-bottom'];
        if (paddingBottom != undefined) {
            targetStyle.paddingBottom = paddingBottom;
        }

        let width = srcCss['width'];
        if (width != undefined) {
            targetStyle.width = width;
        }

        let height = srcCss['height'];
        if (height != undefined) {
            targetStyle.height = height;
        }

        let minWidth = srcCss['min-width'];
        if (minWidth != undefined) {
            targetStyle.minWidth = minWidth;
        }

        let minHeight = srcCss['min-height'];
        if (minHeight != undefined) {
            targetStyle.minHeight = minHeight;
        }

        let maxWidth = srcCss['max-width'];
        if (maxWidth != undefined) {
            targetStyle.maxWidth = maxWidth;
        }

        let maxHeight = srcCss['max-height'];
        if (maxHeight != undefined) {
            targetStyle.maxHeight = maxHeight;
        }

        // 特殊处理：在微信小程序上不生效;在H5上生效
        // 小程序不支持
        let aspectRatio = srcCss['aspect-ratio'];
        if (aspectRatio != undefined) {
            targetStyle.aspectRatio = aspectRatio;
        }

        //
        // Style 
        //

        let backgroundColor = srcCss['background-color'];
        if (backgroundColor != undefined) {
            targetStyle.backgroundColor = backgroundColor;
        }

        let backgroundImage = srcCss['background-image'];
        if (backgroundImage != undefined) {
            targetStyle.backgroundImage = backgroundImage;
        }

        let borderColor = srcCss['border-color'];
        if (borderColor != undefined) {
            targetStyle.borderColor = borderColor;
        }

        let borderWidth = srcCss['border-width'];
        if (borderWidth != undefined) {
            targetStyle.borderWidth = borderWidth;
        }

        let borderTopLeftRadius = srcCss['border-top-left-radius'];
        if (borderTopLeftRadius != undefined) {
            targetStyle.borderTopLeftRadius = borderTopLeftRadius;
        }

        let borderTopRightRadius = srcCss['border-top-right-radius'];
        if (borderTopRightRadius != undefined) {
            targetStyle.borderTopRightRadius = borderTopRightRadius;
        }

        let borderBottomLeftRadius = srcCss['border-bottom-left-radius'];
        if (borderBottomLeftRadius != undefined) {
            targetStyle.borderBottomLeftRadius = borderBottomLeftRadius;
        }

        let borderBottomRightRadius = srcCss['border-bottom-right-radius'];
        if (borderBottomRightRadius != undefined) {
            targetStyle.borderBottomRightRadius = borderBottomRightRadius;
        }

        let borderRadius = srcCss['border-radius'];
        if (borderRadius != undefined) {
            targetStyle.borderRadius = borderRadius;
        }

        if (gxTemplateNode.isTextType() || gxTemplateNode.isRichTextType() || gxTemplateNode.isIconFontType()) {
            let fontSize = srcCss['font-size'];
            if (fontSize != undefined) {
                targetStyle.fontSize = fontSize;
            }

            let fontFamily = srcCss['font-family'];
            if (fontFamily != undefined) {
                targetStyle.fontFamily = fontFamily;
            }

            let fontWeight = srcCss['font-weight'];
            if (fontWeight != undefined) {
                targetStyle.fontWeight = fontWeight;
            }

            let color = srcCss['color'];
            if (color != undefined) {
                targetStyle.color = color;
            }

            let textOverflow = srcCss['text-overflow'];
            targetStyle.textOverflow = "ellipsis"
            if (textOverflow != undefined) {
                targetStyle.textOverflow = textOverflow;
            }

            let textAlign = srcCss['text-align'];
            if (textAlign != undefined) {
                targetStyle.textAlign = textAlign;
            }

            let lineHeight = srcCss['line-height'];
            if (lineHeight != undefined) {
                targetStyle.lineHeight = lineHeight;
            }

            let textDecoration = srcCss['text-decoration'];
            if (textDecoration != undefined) {
                targetStyle.textDecoration = textDecoration;
            }
        }
        if (gxTemplateNode.isImageType()) {
            let mode = srcCss['mode'];
            if (mode != undefined) {
                targetStyle.mode = mode;
            }
        }

        //
        // 特殊处理的逻辑
        // 

        // 对图片进行处理
        if (gxTemplateNode.isImageType()) {
            // 特殊处理：图片不允许被压缩
            targetStyle.flexShrink = '0';
        }

        // 对View进行处理
        if (gxTemplateNode.isViewType() || gxTemplateNode.isGaiaTemplate()) {
            // 特殊处理：如果是嵌套节点，并且自身有内边距，并且width100%，那么需要处理一下
            if ((paddingLeft != null || paddingRight != null) && targetStyle.width == '100%' && gxParentNode != null) {
                targetStyle.width = 'auto';
                targetStyle.flexGrow = '1';
            }
        }

        // 对Text进行处理
        if (gxTemplateNode.isTextType()) {
            // 特殊处理：对文字自适应的处理
            let fitContent = srcCss['fit-content'];

            // 对文字自适应进行特殊处理
            if (fitContent != null && fitContent != undefined) {

                // 特殊处理：如果横向，文字是固定宽度，那么不能被压缩
                if (gxParentNode?.gxTemplateNode?.finalStyle?.flexDirection == 'row' && width != undefined) {
                    targetStyle.flexShrink = '0';
                }

                // 特殊处理：如果竖向, 文字是固定高度，那么不能被压缩
                if (gxParentNode?.gxTemplateNode?.finalStyle?.flexDirection == 'column' && height != undefined) {
                    targetStyle.flexShrink = '0';
                }

                // 如果宽度是auto并且设置的自增长，那么fitcontent之后，需要按照实际的宽度设置
                if ((width == 'auto' || width == undefined) && flexGrow == '1' && (fitContent == 'true' || fitContent == true)) {
                    targetStyle.flexGrow = '0';
                }
                // 特殊处理：如果宽度是具体的像素值，并且设置了fitcontent，那么需要宽度auto
                else if (width != undefined && width.endsWith('px') && (fitContent == 'true' || fitContent == true)) {
                    targetStyle.width = 'auto';
                }
            }

            // 对文字渐变色进行处理
            if (backgroundImage != null){
                targetStyle['-webkit-background-clip'] = 'text';
                targetStyle.color = 'transparent';
            }
        }

        if (gxTemplateNode.isTextType() || gxTemplateNode.isRichTextType() || gxTemplateNode.isIconFontType()) {
            
            // 特殊处理：对文字自适应的处理
            let fitContent = srcCss['fit-content'];

            // 特殊处理：文字默认居中
            if (height != undefined && height.endsWith('px')) {
                targetStyle.lineHeight = height;
            }

            // 特殊处理：处理多行文字...逻辑
            let maxLines = srcCss['lines'];
            if (maxLines != undefined && maxLines > 1) {
                targetStyle['-webkit-box-orient'] = 'vertical';
                targetStyle['-webkit-line-clamp'] = maxLines;
                targetStyle.display = '-webkit-box';

                // 如果不是fitcontent=true，并且多行，那么需要手动计算一下高度，并且赋值
                if (targetStyle.height != undefined) {
                    let tmpHeight = targetStyle.height as string;
                    if (tmpHeight.endsWith('px')) {
                        targetStyle.height = parseInt(tmpHeight.substring(0, tmpHeight.indexOf('px'))) * maxLines + 'px';
                    }
                    // 如果不符合后缀逻辑，那么直接设置auto
                    else {
                        targetStyle.height = 'auto';
                    }
                }
                // 自适应，那么高度设置成auto
                else if (fitContent == 'true' || fitContent == true) {
                    targetStyle.height = 'auto';
                }

            }
            // 特殊处理：处理单行文字...逻辑
            else if (targetStyle.textOverflow == "ellipsis") {
                targetStyle.minWidth = '0px';
                targetStyle.whiteSpace = 'nowrap';
                targetStyle.display = '';
            }
        }

    }
}