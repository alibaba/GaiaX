import { ReactNode } from "react"
import { GXJSONObject } from "./GXJson"
import GXTemplateItem from "./GXTemplateItem"

export default class GXTrack {
    /**
     * Target view
     */
    view: ReactNode = null

    /**
     * Node id
     */
    nodeId: string = null

    /**
     * View index
     */
    index: number = null

    /**
     * Template information
     */
    templateItem: GXTemplateItem = null

    /**
     * Buried data
     */
    trackParams: GXJSONObject = null
}
