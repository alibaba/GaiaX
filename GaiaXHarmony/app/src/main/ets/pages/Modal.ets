import GXIExtensionTemplateSource from 'gaiax/src/main/ets/gaiax/GXIExtensionTemplateSource';
import GXMeasureSize from 'gaiax/src/main/ets/gaiax/GXMeasureSize';
import GXTemplate from 'gaiax/src/main/ets/gaiax/GXTemplate';
import GXTemplateData from 'gaiax/src/main/ets/gaiax/GXTemplateData';
import GXTemplateItem from 'gaiax/src/main/ets/gaiax/GXTemplateItem';
import { gxRegisterCenter } from 'gaiax/src/main/ets/gaiax/GXRegisterCenter';
import hilog from '@ohos.hilog';

class GXFastPreviewTemplateSource implements GXIExtensionTemplateSource {
  getTemplate(gxTemplateItem: GXTemplateItem): GXTemplate {
    const templateData = this.templates.get(gxTemplateItem.templateId)
    if (templateData != null && templateData != undefined) {
      let layer = templateData['index.json'];
      let css = templateData['index.css'];
      let data = templateData['index.databinding'];
      return GXTemplate.create(layer, css, data);
    }
    return null;
  }

  private templates = new Map<string, any>();

  addData(templateId: string, template: any) {
    this.templates.set(templateId, template)
  }

  getData(templateId: string) {
    return this.templates.get(templateId);
  }
}



export const gxFastPreviewTemplateSource = new GXFastPreviewTemplateSource();

class GXParams {
  getGXTemplateItem() {
    let gxTemplateItem = new GXTemplateItem();
    gxTemplateItem.templateBiz = 'harmony';
    gxTemplateItem.templateId = 'harmony-demo';
    return gxTemplateItem;
  }

  getGXTemplateData() {
    let gxTemplateData = new GXTemplateData();
    return gxTemplateData;
  }

  getGXMeasureSize() {
    let gxMeasureSize = new GXMeasureSize();
    gxMeasureSize.templateWidth = "100%";
    return gxMeasureSize;
  }
}

let gxParams = new GXParams();

// gxRegisterCenter.registerExtensionTemplateSource(gxFastPreviewTemplateSource)

// let template = {
//   "index.css": $rawfile('harmony/harmony-demo/index.css'),
//   "index.databinding": $rawfile('harmony/harmony-demo/index.databinding'),
//   "index.json": $rawfile('harmony/harmony-demo/index.json'),
// };
// // gxTemplateSource.addData("harmony-demo", template);
// hilog.error(0x0000, 'gaiaxTag', 'template = %{public}s', JSON.stringify(template) ?? 'null');

export default gxParams as GXParams;

