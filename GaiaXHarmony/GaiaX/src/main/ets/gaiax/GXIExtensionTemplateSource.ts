import GXTemplate from './GXTemplate';
import GXTemplateItem from './GXTemplateItem';


export interface GXIExtensionTemplateSource {
  getTemplate(gxTemplateItem: GXTemplateItem): GXTemplate;
}

export default GXIExtensionTemplateSource