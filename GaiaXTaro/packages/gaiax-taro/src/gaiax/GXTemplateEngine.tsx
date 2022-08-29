import { ReactNode } from "react";
import GXRender from "./GXRender";
import GXTemplateContext from "./GXTemplateContext";
import GXData from "./GXData";
import GXTemplateInfo from "./GXTemplateInfo";
import GXTemplateNode from "./GXTemplateNode";
import GXTemplateItem from "./GXTemplateItem";
import GXMeasureSize from "./GXMeasureSize";
import GXTemplateData from "./GXTemplateData";


export default class GXTemplateEngine {

    gxRender = new GXRender()

    gxData = new GXData()

    createView(
        gxTemplateItem: GXTemplateItem,
        gxTemplateData: GXTemplateData,
        gxMeasureSize: GXMeasureSize,
        gxVisualTemplateNode?: GXTemplateNode
    ): ReactNode {

        // 获取数据
        let gxTemplateInfo: GXTemplateInfo = this.gxData.getTemplateInfo(gxTemplateItem);

        // 构建上下文
        let gxTemplateContext = new GXTemplateContext(
            gxTemplateItem,
            gxTemplateData,
            gxMeasureSize,
            gxTemplateInfo,
            gxVisualTemplateNode
        );

        // 创建视图
        return this.gxRender.createView(gxTemplateContext)
    }
}