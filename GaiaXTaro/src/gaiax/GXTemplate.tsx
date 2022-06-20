import { View, Text, Image, Button } from "@tarojs/components";
import { Component, ReactNode } from 'react';
import { toJSON as CssToJSON } from './GXCssParser';
import GXExpression from './GXExpression';

export class GXTemplateItem {
  templateWidth: number;
  templateHeight: number;
  templateBiz: string;
  templateId: string;
  templateData: {};
}

export class GXTemplateInfo {

  static create(layer: string, css: string, data: string): GXTemplateInfo {
    let templateInfo = new GXTemplateInfo();
    templateInfo.layer = JSON.parse(layer);
    templateInfo.data = JSON.parse(data);
    templateInfo.css = CssToJSON(css);
    return templateInfo;
  }

  layer: {};
  data: {};
  css: {};
}

export interface IGXDataSource {
  getTemplateInfo(templateItem: GXTemplateItem): GXTemplateInfo;
}

class GXTemplateDataSource implements IGXDataSource {

  private dataSource: IGXDataSource

  registerDataSource(dataSource: IGXDataSource) {
    this.dataSource = dataSource
  }

  getTemplateInfo(templateItem: GXTemplateItem): GXTemplateInfo {
    return this.dataSource.getTemplateInfo(templateItem);
  }
}

class GXTemplateContext {
  templateItem: GXTemplateItem;
  templateInfo: GXTemplateInfo;
  isNestChildTemplate: boolean;

  constructor(templateItem: GXTemplateItem, templateInfo: GXTemplateInfo) {
    this.templateItem = templateItem;
    this.templateInfo = templateInfo;
  }
}

class GXViewTreeCreator {

  private dataSource: IGXDataSource;

  setDataSource(dataSource: IGXDataSource) {
    this.dataSource = dataSource;
  }

  build(context: GXTemplateContext) {
    let root = {
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
      flexShrink: '0',
      flexGrow: '0',
      width: '100%',
      height: 'auto',
      minWidth: 'auto',
      minHeight: 'auto',
      maxWidth: 'auto',
      maxHeight: 'auto',
    };

    // 处理外部期望的宽度
    if (context.templateItem.templateWidth != undefined && context.templateItem.templateWidth != null) {
      root.width = context.templateItem.templateWidth + 'px';
    }
    // 处理外部期望的高度
    if (context.templateItem.templateHeight != undefined && context.templateItem.templateHeight != null) {
      root.height = context.templateItem.templateHeight + 'px';
    }

    return (<View style={root}>
      {this.createViewByLayer(context, context.templateInfo.layer, {
        nodeStyle: {},
        nodeCss: {},
      })}
    </View>);
  }

  private createViewStyleByCss(context: GXTemplateContext, layer: any, nodeCss: any, parentNodeInfo: any): any {
    let nodeStyle = {
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
    this.updateViewStyleByCss(context, nodeStyle, layer, nodeCss, parentNodeInfo);
    return nodeStyle;
  }

  private createViewByLayer(context: GXTemplateContext, layer: any, parentNodeInfo: any, visualNodeInfo: any = {}): ReactNode {

    // 获取原始节点样式
    const nodeRawCss = context.templateInfo.css["#" + layer.id] || context.templateInfo.css["." + layer.id]

    // 获取数据绑定
    const nodeData = context.templateInfo.data["data"]?.[layer.id];

    // 获取事件绑定
    const nodeEvent = context.templateInfo.data["event"];

    let nodeExtendRawCss = {};
    let dataResult = '';
    if (typeof nodeData == 'object') {

      // 获取数据绑定结果
      const nodeValueData = nodeData.value
      if (nodeValueData != undefined) {
        const nodeValueResult = GXExpression.desireData(nodeValueData, context.templateItem.templateData)
        if (nodeValueResult != null) {
          dataResult = nodeValueResult;
        }
      }

      // 获取样式绑定的结果
      const nodeExtendData = nodeData.extend
      if (nodeExtendData != undefined) {
        const nodeExtendResult = GXExpression.desireData(nodeExtendData, context.templateItem.templateData)
        if (nodeExtendResult != null) {
          nodeExtendRawCss = nodeExtendResult;
        }
      }
    } else if (typeof nodeData == 'string') {
      dataResult = nodeData;
    }

    // 合并节点样式
    let parentRawCss = {};
    if (visualNodeInfo.nodeCss != null && visualNodeInfo.nodeCss != undefined) {
      parentRawCss = visualNodeInfo.nodeCss;
    }
    const nodeCss = Object.assign({}, nodeRawCss, nodeExtendRawCss, parentRawCss);

    // 获取转换后的节点样式
    const nodeStyle = this.createViewStyleByCss(context, layer, nodeCss, parentNodeInfo)

    console.log({
      id: layer.id,
      nodeData: nodeData,
      nodeStyle: nodeStyle,
      nodeCss: nodeCss,
      dataResult: dataResult
    });

    switch (layer.type) {
      case 'gaia-template':
        if (layer['sub-type'] == 'custom') {
          const nestTemplateItem = new GXTemplateItem();
          nestTemplateItem.templateBiz = context.templateItem.templateBiz;
          nestTemplateItem.templateId = layer.id;
          const nestTemplateInfo = this.dataSource.getTemplateInfo(nestTemplateItem);
          if (nestTemplateInfo != null && nestTemplateInfo != undefined) {
            let templateContext = new GXTemplateContext(nestTemplateItem, nestTemplateInfo);
            templateContext.isNestChildTemplate = true;
            return this.createViewByLayer(templateContext, nestTemplateInfo.layer, {
              nodeStyle: parentNodeInfo.nodeStyle,
              nodeCss: parentNodeInfo.nodeCss,
            }, {
              nodeStyle: nodeStyle,
              nodeCss: nodeCss,
            });
          } else {
            return <View style={nodeStyle} key={layer.id} />;
          }
        } else {
          // 普通层级
          if (layer.layers != null && layer.layers != undefined) {
            const childArray: ReactNode[] = [];
            for (var i = 0; i < layer.layers.length; i++) {
              const childLayer = layer.layers[i];
              childArray.push(this.createViewByLayer(context, childLayer, {
                nodeStyle: nodeStyle,
              }))
            }
            return <View style={nodeStyle} key={layer.id} >
              {childArray}
            </View>;
          } else {
            return <View style={nodeStyle} key={layer.id} />;
          }
        }
      case 'view':
        if (layer.layers != null && layer.layers != undefined) {
          const childArray: ReactNode[] = [];
          for (var i = 0; i < layer.layers.length; i++) {
            const childLayer = layer.layers[i];
            childArray.push(this.createViewByLayer(context, childLayer, {
              nodeStyle: nodeStyle
            }))
          }
          return <View style={nodeStyle} key={layer.id} >
            {childArray}
          </View>;
        }
        return <View style={nodeStyle} key={layer.id} />;
      case 'text':
        return <Text style={nodeStyle} key={layer.id} >{dataResult}</Text>;
      case 'richtext':
        return <Text style={nodeStyle} key={layer.id} >{dataResult}</Text>;
      case 'iconfont':
        return <Text style={nodeStyle} key={layer.id} >{dataResult}</Text>;
      case 'grid':
        return <View style={nodeStyle} key={layer.id} />;
      case 'scroll':
        return <View style={nodeStyle} key={layer.id} />;
      case 'image':
        return <Image style={nodeStyle} key={layer.id} src={dataResult} />;
      default:
        // 不会走到
        return <View style={nodeStyle} key={layer.id} />;
    }
  }

  private updateViewStyleByCss(context: GXTemplateContext, nodeStyle: any, layer: any, nodeCss: any, parentNodeInfo: any) {

    // Layout

    let display = nodeCss['display'];
    if (display != undefined) {
      nodeStyle.display = display;
    }

    let direction = nodeCss['direction'];
    if (direction != undefined) {
      nodeStyle.direction = direction;
    }

    let flexDirection = nodeCss['flex-direction'];
    if (flexDirection != undefined) {
      nodeStyle.flexDirection = flexDirection;
    }

    let flexWrap = nodeCss['flex-wrap'];
    if (flexWrap != undefined) {
      nodeStyle.flexWrap = flexWrap;
    }

    let overflow = nodeCss['overflow'];
    if (overflow != undefined) {
      nodeStyle.overflow = overflow;
    }

    let alignItems = nodeCss['align-items'];
    if (alignItems != undefined) {
      nodeStyle.alignItems = alignItems;
    }

    let alignSelf = nodeCss['align-self'];
    if (alignSelf != undefined) {
      nodeStyle.alignSelf = alignSelf;
    }

    let alignContent = nodeCss['align-content'];
    if (alignContent != undefined) {
      nodeStyle.alignContent = alignContent;
    }

    let justifyContent = nodeCss['justify-content'];
    if (justifyContent != undefined) {
      nodeStyle.justifyContent = justifyContent;
    }

    let flexGrow = nodeCss['flex-grow'];
    if (flexGrow != undefined) {
      nodeStyle.flexGrow = flexGrow;
    }

    let flexShrink = nodeCss['flex-shrink'];

    if (flexShrink != undefined) {
      nodeStyle.flexShrink = flexShrink;
    }

    let flexBasis = nodeCss['flex-basis'];
    if (flexBasis != undefined) {
      nodeStyle.flexBasis = flexBasis;
    }

    let position = nodeCss['position'];
    if (position != undefined) {
      nodeStyle.position = position;
    }

    let left = nodeCss['left'];
    if (left != undefined) {
      nodeStyle.left = left;
    }

    let right = nodeCss['right'];
    if (right != undefined) {
      nodeStyle.right = right;
    }

    let top = nodeCss['top'];
    if (top != undefined) {
      nodeStyle.top = top;
    }

    let bottom = nodeCss['bottom'];
    if (bottom != undefined) {
      nodeStyle.bottom = bottom;
    }

    let marginLeft = nodeCss['margin-left'];
    if (marginLeft != undefined) {
      nodeStyle.marginLeft = marginLeft;
    }

    let marginTop = nodeCss['margin-top'];
    if (marginTop != undefined) {
      nodeStyle.marginTop = marginTop;
    }

    let marginRight = nodeCss['margin-right'];
    if (marginRight != undefined) {
      nodeStyle.marginRight = marginRight;
    }

    let marginBottom = nodeCss['margin-bottom'];
    if (marginBottom != undefined) {
      nodeStyle.marginBottom = marginBottom;
    }

    let paddingLeft = nodeCss['padding-left'];
    if (paddingLeft != undefined) {
      nodeStyle.paddingLeft = paddingLeft;
    }

    let paddingTop = nodeCss['padding-top'];
    if (paddingTop != undefined) {
      nodeStyle.paddingTop = paddingTop;
    }

    let paddingRight = nodeCss['padding-right'];
    if (paddingRight != undefined) {
      nodeStyle.paddingRight = paddingRight;
    }

    let paddingBottom = nodeCss['padding-bottom'];
    if (paddingBottom != undefined) {
      nodeStyle.paddingBottom = paddingBottom;
    }

    let width = nodeCss['width'];
    if (width != undefined) {
      nodeStyle.width = width;
    }

    let height = nodeCss['height'];
    if (height != undefined) {
      nodeStyle.height = height;
    }

    let minWidth = nodeCss['min-width'];
    if (minWidth != undefined) {
      nodeStyle.minWidth = minWidth;
    }

    let minHeight = nodeCss['min-height'];
    if (minHeight != undefined) {
      nodeStyle.minHeight = minHeight;
    }

    let maxWidth = nodeCss['max-width'];
    if (maxWidth != undefined) {
      nodeStyle.maxWidth = maxWidth;
    }

    let maxHeight = nodeCss['max-height'];
    if (maxHeight != undefined) {
      nodeStyle.maxHeight = maxHeight;
    }

    // 特殊处理：图片不允许被压缩
    if (layer.type == 'image') {
      nodeStyle.flexShrink = '0';
    }

    // 特殊处理：在微信小程序上不生效;在H5上生效
    // 小程序不支持
    let aspectRatio = nodeCss['aspect-ratio'];
    if (aspectRatio != undefined) {
      nodeStyle.aspectRatio = aspectRatio;
    }

    // 特殊处理：如果横向，文字是固定宽度，那么不能被压缩
    if (parentNodeInfo.nodeStyle.flexDirection == 'row' && width != undefined) {
      nodeStyle.flexShrink = '0';
    }

    // 特殊处理：如果竖向, 文字是固定高度，那么不能被压缩
    if (parentNodeInfo.nodeStyle.flexDirection == 'column' && height != undefined) {
      nodeStyle.flexShrink = '0';
    }

    // 特殊处理：对文字自适应的处理
    let fitContent = nodeCss['fit-content'];
    // 如果宽度是auto并且设置的自增长，那么fitcontent之后，需要按照实际的宽度设置
    if ((width == 'auto' || width == undefined) && flexGrow == '1' && (fitContent == 'true' || fitContent == true)) {
      nodeStyle.flexGrow = '0';
    }
    // 特殊处理：如果宽度是具体的像素值，并且设置了fitcontent，那么需要宽度auto
    else if (width != undefined && width.endsWith('px') && (fitContent == 'true' || fitContent == true)) {
      nodeStyle.width = 'auto';
    }

    // Style 

    switch (layer.type) {
      case 'gaia-template':
        // 如果是嵌套模板的根节点，并且宽度100%，有左右padding信息，那么需要可以被压缩
        if (context.isNestChildTemplate && layer['sub-type'] == undefined) {
          if (width == '100%' && (paddingLeft != undefined || paddingRight != undefined)) {
            nodeStyle.flexShrink = '1';
            // 若还是垂直布局，那么宽度需要auto
            if (parentNodeInfo.nodeStyle.flexDirection == 'column') {
              nodeStyle.width = 'auto';
            }
          }
        }
        break;
      case 'view': break;
      case 'text':
      case 'richtext':
      case 'iconfont':
        let fontSize = nodeCss['font-size'];
        if (fontSize != undefined) {
          nodeStyle.fontSize = fontSize;
        }

        let fontFamily = nodeCss['font-family'];
        if (fontFamily != undefined) {
          nodeStyle.fontFamily = fontFamily;
        }

        let fontWeight = nodeCss['font-weight'];
        if (fontWeight != undefined) {
          nodeStyle.fontWeight = fontWeight;
        }

        let color = nodeCss['color'];
        if (color != undefined) {
          nodeStyle.color = color;
        }

        let textOverflow = nodeCss['text-overflow'];
        nodeStyle.textOverflow = "ellipsis"
        if (textOverflow != undefined) {
          nodeStyle.textOverflow = textOverflow;
        }

        let textAlign = nodeCss['text-align'];
        if (textAlign != undefined) {
          nodeStyle.textAlign = textAlign;
        }

        let lineHeight = nodeCss['line-height'];
        if (lineHeight != undefined) {
          nodeStyle.lineHeight = lineHeight;
        }

        // 特殊处理：文字默认居中
        if (height != undefined && height.endsWith('px')) {
          nodeStyle.lineHeight = height;
        }

        let textDecoration = nodeCss['text-decoration'];
        if (textDecoration != undefined) {
          nodeStyle.textDecoration = textDecoration;
        }

        // 特殊处理：处理多行文字...逻辑
        let maxLines = nodeCss['lines'];
        if (maxLines != undefined && maxLines > 1) {
          nodeStyle['-webkit-box-orient'] = 'vertical';
          nodeStyle['-webkit-line-clamp'] = maxLines;
          nodeStyle.display = '-webkit-box';

          // 如果不是fitcontent=true，并且多行，那么需要手动计算一下高度，并且赋值
          if (nodeStyle.height != undefined) {
            let tmpHeight = nodeStyle.height;
            if (tmpHeight.endsWith('px')) {
              nodeStyle.height = parseInt(tmpHeight.substring(0, tmpHeight.indexOf('px'))) * maxLines + 'px';
            }
            // 如果不符合后缀逻辑，那么直接设置auto
            else {
              nodeStyle.height = 'auto';
            }
          }
          // 自适应，那么高度设置成auto
          else if (fitContent == 'true' || fitContent == true) {
            nodeStyle.height = 'auto';
          }

        }
        // 特殊处理：处理单行文字...逻辑
        else if (nodeStyle.textOverflow == "ellipsis") {
          nodeStyle.minWidth = '0px';
          nodeStyle.whiteSpace = 'nowrap';
          nodeStyle.display = '';
        }

        break;
      case 'grid': break;
      case 'scroll': break;
      case 'image':
        let mode = nodeCss['mode'];
        if (mode != undefined) {
          nodeStyle.mode = mode;
        }
        break;
    }

    let backgroundColor = nodeCss['background-color'];
    if (backgroundColor != undefined) {
      nodeStyle.backgroundColor = backgroundColor;
    }

    let backgroundImage = nodeCss['background-image'];
    if (backgroundImage != undefined) {
      nodeStyle.backgroundImage = backgroundImage;
    }

    let borderColor = nodeCss['border-color'];
    if (borderColor != undefined) {
      nodeStyle.borderColor = borderColor;
    }

    let borderWidth = nodeCss['border-width'];
    if (borderWidth != undefined) {
      nodeStyle.borderWidth = borderWidth;
    }

    let borderTopLeftRadius = nodeCss['border-top-left-radius'];
    if (borderTopLeftRadius != undefined) {
      nodeStyle.borderTopLeftRadius = borderTopLeftRadius;
    }

    let borderTopRightRadius = nodeCss['border-top-right-radius'];
    if (borderTopRightRadius != undefined) {
      nodeStyle.borderTopRightRadius = borderTopRightRadius;
    }

    let borderBottomLeftRadius = nodeCss['border-bottom-left-radius'];
    if (borderBottomLeftRadius != undefined) {
      nodeStyle.borderBottomLeftRadius = borderBottomLeftRadius;
    }

    let borderBottomRightRadius = nodeCss['border-bottom-right-radius'];
    if (borderBottomRightRadius != undefined) {
      nodeStyle.borderBottomRightRadius = borderBottomRightRadius;
    }

    let borderRadius = nodeCss['border-radius'];
    if (borderRadius != undefined) {
      nodeStyle.borderRadius = borderRadius;
    }
  }

}

class GXTemplateEngine {

  private viewTreeCreator = new GXViewTreeCreator()

  private dataSource = new GXTemplateDataSource()

  createView(templateItem: GXTemplateItem) {

    this.viewTreeCreator.setDataSource(this.dataSource)

    // 获取数据
    let templateInfo = this.getTemplateInfo(templateItem)
    // 构建上下文
    let templateContext = new GXTemplateContext(templateItem, templateInfo);
    // 创建视图
    return this.viewTreeCreator.build(templateContext)
  }

  private getTemplateInfo(templateItem: GXTemplateItem): GXTemplateInfo {
    return this.dataSource.getTemplateInfo(templateItem);
  }

  registerDataSource(dataSource: IGXDataSource) {
    this.dataSource.registerDataSource(dataSource)
  }
}

export const GXEngineInstance = new GXTemplateEngine()

export interface GXTemplateProps {
  templateItem: GXTemplateItem
}

export class GXTemplate extends Component<GXTemplateProps, GXTemplateProps> {
  render() {
    const { templateItem } = this.props
    return GXEngineInstance.createView(templateItem);
  }
}
