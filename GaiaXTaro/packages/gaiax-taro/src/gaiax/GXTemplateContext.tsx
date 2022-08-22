import { ReactNode } from "react";
import GXTemplateData from "./GXTemplateData";
import GXMeasureSize from "./GXMeasureSize";
import GXTemplateItem from "./GXTemplateItem";
import GXTemplateInfo from "./GXTemplateInfo";
import GXTemplateNode from "./GXTemplateNode";

export default class GXTemplateContext {

    gxTemplateItem: GXTemplateItem;

    gxTemplateInfo: GXTemplateInfo;

    gxTemplateData: GXTemplateData;

    gxMeasureSize: GXMeasureSize;

    gxVisualTemplateNode?: GXTemplateNode;

    isNestChildTemplate: boolean;

    rootView: ReactNode;


    constructor(
        gxTemplateItem: GXTemplateItem,
        gxTemplateData: GXTemplateData,
        gxMeasureSize: GXMeasureSize,
        gxTemplateInfo: GXTemplateInfo,
        gxVisualTemplateNode?: GXTemplateNode
    ) {
        this.gxTemplateItem = gxTemplateItem;
        this.gxTemplateInfo = gxTemplateInfo;
        this.gxTemplateData = gxTemplateData;
        this.gxMeasureSize = gxMeasureSize;
        this.gxVisualTemplateNode = gxVisualTemplateNode;
    }
}
