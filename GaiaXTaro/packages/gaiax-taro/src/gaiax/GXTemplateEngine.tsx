import { ReactNode } from "react";
import GXRender from "./GXRender";
import GXTemplateContext from "./GXTemplateContext";
import GXData from "./GXData";
import GXTemplateInfo from "./GXTemplateInfo";

export class GXMeasureSize {
    templateWidth: number;
    templateHeight: number;
}

export class GXTemplateData {
    templateData: any;
}

export class GXTemplateItem {
    static create(templateBiz: string, templateId: string) {
        const gxTemplateItem = new GXTemplateItem();
        gxTemplateItem.templateBiz = templateBiz;
        gxTemplateItem.templateId = templateId;
        return gxTemplateItem;
    }
    templateBiz: string;
    templateId: string;
}

export default class GXTemplateEngine {

    gxRender = new GXRender()

    gxData = new GXData()

    createView(gxTemplateItem: GXTemplateItem, gxTemplateData: GXTemplateData, gxMeasureSize: GXMeasureSize): ReactNode {

        // 获取数据
        let gxTemplateInfo: GXTemplateInfo = this.gxData.getTemplateInfo(gxTemplateItem);

        // 构建上下文
        let gxTemplateContext = new GXTemplateContext(gxTemplateItem, gxTemplateData, gxMeasureSize, gxTemplateInfo);

        // 创建视图
        return this.gxRender.createView(gxTemplateContext)
    }
}