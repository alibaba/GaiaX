import GXTemplateItem from './GXTemplateItem';
import GXTemplateInfo from './GXTemplateInfo';
import GXIExtensionTemplateSource from './GXIExtensionTemplateSource';
import GXIExtensionTemplateInfoSource from './GXIExtensionTemplateInfoSource';
import GXTemplate from './GXTemplate';
import hilog from '@ohos.hilog';

class GXTemplateInfoSource implements GXIExtensionTemplateInfoSource {
  private dataCache = new Map<string, Map<string, GXTemplateInfo>>();

  getTemplateInfo(templateItem: GXTemplateItem): GXTemplateInfo {
    // TODO: GXTemplateInfo缓存逻辑
    return GXTemplateInfo.createByGXTemplateItem(templateItem);
  }
}

class GXTemplateSource implements GXIExtensionTemplateSource {
  getTemplate(templateItem: GXTemplateItem): GXTemplate {
    hilog.debug(0x0000, 'gaiaxTag', 'getTemplate %{public}s', JSON.stringify(templateItem) ?? 'null');
    return this.gxTemplateSource?.getTemplate(templateItem)
  }

  private gxTemplateSource?: GXIExtensionTemplateSource;

  register(gxTemplateSource: GXIExtensionTemplateSource) {
    hilog.debug(0x0000, 'gaiaxTag', 'register %{public}s', JSON.stringify(gxTemplateSource) ?? 'null');
    this.gxTemplateSource = gxTemplateSource;
  }
}

export default class GXData {
  gxTemplateInfoSource = new GXTemplateInfoSource();

  gxTemplateSource = new GXTemplateSource();

  getTemplateInfo(templateItem: GXTemplateItem): GXTemplateInfo {
    return this.gxTemplateInfoSource.getTemplateInfo(templateItem);
  }
}
