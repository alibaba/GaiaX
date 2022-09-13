
import { View } from "@tarojs/components";
import { Component } from "react";
import {
  GXTemplate,
  GXEngineInstance,
  GXRegisterCenterInstance,
  GXTemplateComponent,
  GXTemplateItem,
  GXMeasureSize,
  GXTemplateData,
  GXIExtensionTemplateSource,
  GXIEventListener,
  GXGesture,
  GXITrackListener,
  GXTrack
} from "@gaiax/taro";
import "./index.scss";
import * as GXFastPreview from "../../gaiax/GXFastPreview";

// Debug Outline
if (process.env.TARO_ENV === 'h5') {
  require('./index_h5_debug.scss')
} else if (process.env.TARO_ENV === 'weapp') {
  require('./index_weapp_debug.scss')
}

class GXFastPreviewTemplateSource implements GXIExtensionTemplateSource {

  getTemplate(gxTemplateItem: GXTemplateItem): GXTemplate {
    const templateData = this.templates.get(gxTemplateItem.templateId)
    let layer = templateData['index.json'];
    let css = templateData['index.css'];
    let data = templateData['index.databinding'];
    return GXTemplate.create(layer, css, data)
  }

  private templates = new Map<string, any>();

  addData(templateId: string, template: any) {
    this.templates.set(templateId, template)
  }

  getData(templateId: string) {
    return this.templates.get(templateId);
  }
}

interface IParams {
  templateId: string;
}

const gxTemplateSource = new GXFastPreviewTemplateSource();

export default class Index extends Component<IParams> {

  state = {
    templateId: '',
    isFollowed: false
  };

  componentWillMount() {

    const gxFastPreviewListener: GXFastPreview.IGXFastPreviewListener = {
      onUpdate: (templateId: string) => {
        console.warn(`onUpdate templateId = ${templateId} `);
        this.setState({
          templateId: templateId
        });
      },

      onAddData: (templateId: string, template: any) => {
        console.warn(`onAddData templateId = ${templateId} `);
        gxTemplateSource.addData(templateId, template);
      }
    }

    GXRegisterCenterInstance.registerExtensionTemplateSource(gxTemplateSource);
    GXFastPreview.GXFastPreviewInstance.startFastPreview();
    GXFastPreview.GXFastPreviewInstance.setListener(gxFastPreviewListener);
  }

  componentDidMount() { }

  componentWillUnmount() {
    GXFastPreview.GXFastPreviewInstance.stopFastPreview();
  }

  componentDidShow() { }

  componentDidHide() { }

  render() {
    const { templateId, isFollowed } = this.state;
    if (templateId != '') {

      let templateItem = new GXTemplateItem();
      templateItem.templateBiz = 'fastpreview';
      templateItem.templateId = templateId;

      let templateData = new GXTemplateData();
      const template = gxTemplateSource.getData(templateId);
      if (template != undefined && template['index.mock'] != undefined) {
        templateData.templateData = template['index.mock'];
      } else {
        templateData.templateData = {};
      }

      const gxEventListener: GXIEventListener = {
        onGestureEvent: (gxGesture: GXGesture) => {
          if (gxGesture.nodeId == "follow") {
            if (isFollowed) {
              templateData.templateData.data.isFollowed = false;
              this.setState({
                isFollowed: false
              });
            } else {
              templateData.templateData.data.isFollowed = true;
              this.setState({
                isFollowed: true
              });
            }
          }
        }
      }

      templateData.eventListener = gxEventListener

      const gxTrackListener: GXITrackListener = {
        onTrackEvent: (gxTrack: GXTrack) => {
          console.log(gxTrack);
        }
      };

      templateData.trackListener = gxTrackListener;

      const constraintSize = JSON.parse(template["index.json"])?.["package"]?.["constraint-size"]
      let measureSize = new GXMeasureSize();
      measureSize.templateWidth = constraintSize?.['width'] + 'px' || (375 + 'px')
      measureSize.templateHeight = constraintSize?.['height'] + 'px'

      return (
        <GXTemplateComponent
          gxTemplateData={templateData}
          gxTemplateItem={templateItem}
          gxMeasureSize={measureSize}
        />
      );
    } else {
      return (
        <View></View>
      );
    }
  }
}