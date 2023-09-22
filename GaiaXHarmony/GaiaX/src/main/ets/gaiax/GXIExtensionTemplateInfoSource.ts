import GXTemplateItem from './GXTemplateItem';
import GXTemplateInfo from './GXTemplateInfo';


export default interface GXIExtensionTemplateInfoSource {
  getTemplateInfo(gxTemplateItem: GXTemplateItem): GXTemplateInfo;
}
