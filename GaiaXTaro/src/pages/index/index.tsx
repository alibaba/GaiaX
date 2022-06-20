import { Component } from "react";
import { View, Text } from "@tarojs/components";
import "./index.less";
import { GXEngineInstance, GXTemplate, IGXDataSource, GXTemplateItem, GXTemplateInfo } from "../../gaiax/GXTemplate";
import { GXFastPreviewInstance, IGXFastPreviewListener } from "../../gaiax/GXFastPreview";


class GXFastPreviewDataSource implements IGXDataSource {

  private templates = new Map<string, any>();

  addData(templateId: string, template: any) {
    this.templates.set(templateId, template)
  }

  getData(templateId: string) {
    return this.templates.get(templateId);
  }

  getTemplateInfo(templateItem: GXTemplateItem): GXTemplateInfo {
    const templateData = this.templates.get(templateItem.templateId)
    let layer = templateData['index.json'];
    let css = templateData['index.css'];
    let data = templateData['index.databinding'];
    return GXTemplateInfo.create(layer, css, data)
  }
}

interface IParams {
  templateId: string;
}

const gxDataSource = new GXFastPreviewDataSource();

export default class Index extends Component<IParams> {

  state = {
    templateId: '',
  };

  componentWillMount() {

    const gxFastPreviewListener: IGXFastPreviewListener = {
      onUpdate: (templateId: string, template: any) => {
        gxDataSource.addData(templateId, template)
          this.setState({
            templateId: templateId
          });
      }
    }

    GXEngineInstance.registerDataSource(gxDataSource);
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
      templateItem.templateBiz = '';
      templateItem.templateId = templateId;

      const template = gxDataSource.getData(templateId);
      const constraintSize = JSON.parse(template["index.json"])?.["package"]?.["constraint-size"]
      templateItem.templateWidth = constraintSize?.['width']
      templateItem.templateHeight = constraintSize?.['height']

      console.log(template);
      if (template != undefined && template['index.mock'] != undefined) {
        templateItem.templateData = template['index.mock'];
      } else {
        templateItem.templateData = {};
      }

      return (
        <View>
          <GXTemplate templateItem={templateItem} />
        </View>
      );
    } else {
      return (
        <View></View>
      );
    }
  }
}
