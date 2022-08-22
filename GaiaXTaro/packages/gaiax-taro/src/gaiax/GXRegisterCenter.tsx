import { GXEngineInstance } from './GXTemplateEngineInstance';
import GXTemplate from "./GXTemplate";
import GXTemplateItem from "./GXTemplateItem";
import GXTemplateInfo from "./GXTemplateInfo";

export interface GXIExtensionTemplateInfoSource {
    getTemplateInfo(gxTemplateItem: GXTemplateItem): GXTemplateInfo;
}

export interface GXIExtensionTemplateSource {
    getTemplate(gxTemplateItem: GXTemplateItem): GXTemplate;
}

export default class GXRegisterCenter {

    registerExtensionTemplateSource(gxTemplateSource: GXIExtensionTemplateSource) {
        GXEngineInstance.gxData.gxTemplateSource.register(gxTemplateSource);
    }

}