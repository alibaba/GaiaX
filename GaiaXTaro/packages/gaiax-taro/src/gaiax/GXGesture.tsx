import { ReactNode } from "react"
import { GXJSONObject } from "./GXJson"
import { GXTemplateItem } from "./GXTemplateEngine"
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

}