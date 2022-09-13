import { ReactNode } from "react"
import { GXJSONObject } from "./GXJson"
import { GXNode } from "./GXNode"
import GXTemplateContext from "./GXTemplateContext"
import GXTemplateItem from "./GXTemplateItem"
import GXTemplateKey from "./GXTemplateKey"

export default class GXGesture {

    /**
     * Event types：tap, longpress
     */
    gestureType: string = GXTemplateKey.GAIAX_GESTURE_TYPE_TAP

    /**
     * Target view
     */
    view: ReactNode = null

    /**
     * Node id
     */
    nodeId: string = null

    nodeIdPath: string = null

    /**
     * Template information
     */
    templateItem: GXTemplateItem = null

    /**
     * View index
     */
    index: number = null

    /**
     * Event data
     */
    eventParams: GXJSONObject = null

    static create(
        gxTemplateContext: GXTemplateContext,
        gxTemplateData: GXJSONObject,
        gxNode: GXNode
    ): GXGesture {
        const gxGesture = new GXGesture()
        gxGesture.nodeId = gxNode.gxId;
        gxGesture.nodeIdPath = gxNode.gxIdPath;
        gxGesture.templateItem = gxTemplateContext.gxTemplateItem;
        gxGesture.eventParams = gxNode.gxTemplateNode.getEventData(gxTemplateData);
        gxGesture.view = gxNode.gxView;
        return gxGesture;
    }

}