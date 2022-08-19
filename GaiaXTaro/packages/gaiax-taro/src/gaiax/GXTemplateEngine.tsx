import { ReactNode } from "react";
import GXRender from "./GXRender";
import GXTemplateContext from "./GXTemplateContext";
import GXData from "./GXData";
import GXTemplateInfo from "./GXTemplateInfo";
import GXTemplateNode from "./GXTemplateNode";
import GXGesture from "./GXGesture";
import GXTrack from "./GXTrack";


export interface GXIEventListener {

    /**
     * Gesture event
     */
    onGestureEvent(gxGesture: GXGesture)

}

export interface GXITrackListener {

    /**
     * Track event
     */
    onTrackEvent(gxTrack: GXTrack)
}

export class GXMeasureSize {
    templateWidth: number;
    templateHeight: number;
}

export class GXTemplateData {
    // JSON Data
    templateData: any;

    /**
         * @suppress
         */
    scrollIndex: number = -1

    /**
     * Event listener
     */
    eventListener: GXIEventListener = null

    /**
     * Track listener
     */
    trackListener: GXITrackListener = null
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