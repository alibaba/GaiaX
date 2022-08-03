import { GXEngineInstance } from "..";
import GXTemplate from "./GXTemplate";
import { GXTemplateItem } from "./GXTemplateEngine";
import GXTemplateInfo from "./GXTemplateInfo";

export interface GXIExtensionTemplateInfoSource {
    getTemplateInfo(gxTemplateItem: GXTemplateItem): GXTemplateInfo;
}

export interface GXIExtensionTemplateSource {
    getTemplate(gxTemplateItem: GXTemplateItem): GXTemplate;
}

export class GXRegisterCenter {

    registerExtensionTemplateSource(gxTemplateSource: GXIExtensionTemplateSource) {
        GXEngineInstance.gxData.gxTemplateSource.register(gxTemplateSource);
    }

}