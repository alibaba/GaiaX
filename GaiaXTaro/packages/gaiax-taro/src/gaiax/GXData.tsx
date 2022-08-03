import { GXTemplateItem } from "./GXTemplateEngine";
import GXTemplateInfo from "./GXTemplateInfo";
import { GXIExtensionTemplateInfoSource, GXIExtensionTemplateSource } from "./GXRegisterCenter";
import GXTemplate from "./GXTemplate";

class GXTemplateInfoSource implements GXIExtensionTemplateInfoSource {

    // private dataCache = new Map<string, Map<string, GXTemplateInfo>>();

    getTemplateInfo(templateItem: GXTemplateItem): GXTemplateInfo {
        return GXTemplateInfo.createByGXTemplateItem(templateItem);
    }

}

class GXTemplateSource implements GXIExtensionTemplateSource {

    getTemplate(templateItem: GXTemplateItem): GXTemplate {
        return this.gxTemplateSource?.getTemplate(templateItem)
    }

    private gxTemplateSource?: GXIExtensionTemplateSource;

    register(gxTemplateSource: GXIExtensionTemplateSource) {
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
