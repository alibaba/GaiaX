import GXTemplate from "./GXTemplate";
import GXTemplateItem from "./GXTemplateItem";


export default interface GXIExtensionTemplateSource {
    getTemplate(gxTemplateItem: GXTemplateItem): GXTemplate;
}
