
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
  GXGesture
} from "@gaiax/taro";
import "./index.scss";
import { GXFastPreviewInstance, IGXFastPreviewListener as GXIFastPreviewListener } from "../../gaiax/GXFastPreview";

// Debug Outline
if (process.env.TARO_ENV === 'h5') {
  require('./index_debug.scss')
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
  };

  componentWillMount() {

    const gxFastPreviewListener: GXIFastPreviewListener = {
      onUpdate: (templateId: string) => {
        console.log(`onUpdate templateId = ${templateId} `);
        this.setState({
          templateId: templateId
        });
      },

      onAddData: function (templateId: string, template: any) {
        console.log(`onAddData templateId = ${templateId} `);
        console.log(template);
        gxTemplateSource.addData(templateId, template);
      }
    }

    GXRegisterCenterInstance.registerExtensionTemplateSource(gxTemplateSource);
    GXFastPreviewInstance.startFastPreview();
    GXFastPreviewInstance.setListener(gxFastPreviewListener);
  }

  componentDidMount() { }

  componentWillUnmount() {
    GXFastPreviewInstance.stopFastPreview();
  }

  componentDidShow() { }

  componentDidHide() { }

  render() {
    const { templateId } = this.state;
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
        onGestureEvent: function (gxGesture: GXGesture) {
          console.log(gxGesture);
        }
      }

      templateData.eventListener = gxEventListener

      const constraintSize = JSON.parse(template["index.json"])?.["package"]?.["constraint-size"]
      let measureSize = new GXMeasureSize();
      measureSize.templateWidth = constraintSize?.['width'] || 375
      measureSize.templateHeight = constraintSize?.['height']

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