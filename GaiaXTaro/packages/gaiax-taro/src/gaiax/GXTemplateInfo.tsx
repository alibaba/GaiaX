import { GXEngineInstance } from "..";
import { toJSON } from "./GXCssParser";
import { GXJSONArray, GXJSONObject } from "./GXJson";
import GXTemplate from "./GXTemplate";
import { GXTemplateItem } from "./GXTemplateEngine";
import GXTemplateNode from "./GXTemplateNode";

export default class GXTemplateInfo {

    getChildTemplate(id: string): GXTemplateInfo {
        if (this.children != null) {
            for (const gxTemplateInfo of this.children) {
                if (gxTemplateInfo.layer.id == id) {
                    return gxTemplateInfo;
                }
            }
        }
        return null;
    }

    static createByGXTemplateItem(gxTemplateItem: GXTemplateItem): GXTemplateInfo {
        const gxTemplate = GXEngineInstance.gxData.gxTemplateSource.getTemplate(gxTemplateItem);
        const gxTemplateInfo = this.createByGXTemplate(gxTemplate);
        const layer = gxTemplateInfo.layer;
        this.initChildren(gxTemplateInfo, layer, gxTemplateItem);
        return gxTemplateInfo;
    }

    static createByGXTemplate(gxTemplate: GXTemplate): GXTemplateInfo {
        const gxTemplateInfo = new GXTemplateInfo();
        gxTemplateInfo.layer = JSON.parse(gxTemplate.layer);
        gxTemplateInfo.data = JSON.parse(gxTemplate.data);
        gxTemplateInfo.css = toJSON(gxTemplate.css);
        return gxTemplateInfo;
    }

    static initChildren(gxTemplateInfo: GXTemplateInfo, layer: GXJSONObject, gxTemplateItem: GXTemplateItem) {
        const layers = layer?.layers as GXJSONArray;
        if (layers != null && layers != undefined) {
            for (const target of layers) {
                const childLayer = target as GXJSONObject;
                if (GXTemplateNode.isNestChildTemplateType(childLayer)) {
                    if (gxTemplateInfo.children == null) {
                        gxTemplateInfo.children = new Array<GXTemplateInfo>();
                    }
                    const gxChildTemplateItem = GXTemplateItem.create(gxTemplateItem.templateBiz, childLayer.id);
                    const gxChildTemplateInfo = this.createByGXTemplateItem(gxChildTemplateItem);
                    gxTemplateInfo.children?.push(gxChildTemplateInfo);

                }
                this.initChildren(gxTemplateInfo, childLayer, gxTemplateItem);
            }
        }
    }

    layer: GXJSONObject;

    data: GXJSONObject;

    css: GXJSONObject;

    children?: Array<GXTemplateInfo> = null;
}


