import GXIExtensionTemplateSource from './GXIExtensionTemplateSource';
import GXIExtensionCustomComponent from './GXIExtensionCustomComponent';
import GXTemplateEngine from './GXTemplateEngine';
import hilog from '@ohos.hilog';
import GXTemplate from './GXTemplate';
import GXTemplateItem from './GXTemplateItem';

class GXRegisterCenter {
  gxExtensionCustomComponent: GXIExtensionCustomComponent

  registerExtensionTemplateSource(gxTemplateSource: GXIExtensionTemplateSource) {
    GXTemplateEngine.gxData.gxTemplateSource.register(gxTemplateSource);
  }

  registerExtensionCustomComponent(gxExtensionComponent: GXIExtensionCustomComponent) {
    this.gxExtensionCustomComponent = gxExtensionComponent
  }
}

export default GXRegisterCenter;

class GXRawFileTemplateSource implements GXIExtensionTemplateSource {
  getTemplate(gxTemplateItem: GXTemplateItem): GXTemplate {
    const templateData = this.templates.get(gxTemplateItem.templateId);
    hilog.info(0x0000, 'gaiaxTag', 'GXRawFileTemplateSource = %{public}s', JSON.stringify(templateData) ?? 'null');
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
    hilog.info(0x0000, 'gaiaxTag', 'addData %{public}s %{public}s', templateId, JSON.stringify(template) ?? 'null');
    this.templates.set(templateId, template)
  }

  getData(templateId: string) {
    return this.templates.get(templateId);
  }
}

export const gxRawFileTemplateSource = new GXRawFileTemplateSource();

export const gxRegisterCenter = new GXRegisterCenter();

gxRegisterCenter.registerExtensionTemplateSource(gxRawFileTemplateSource)