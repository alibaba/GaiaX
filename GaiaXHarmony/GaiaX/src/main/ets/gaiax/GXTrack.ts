import { GXJSONObject } from './GXJson'
import { GXNode } from './GXNode'
import GXTemplateContext from './GXTemplateContext'
import GXTemplateItem from './GXTemplateItem'

export default class GXTrack {
  /**
   * Target view
   */
  // view: ReactNode = null

  /**
   * Node id
   */
  nodeId: string = null

  nodeIdPath: string = null

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

  static create(
    gxTemplateContext: GXTemplateContext,
    gxTemplateData: GXJSONObject,
    gxNode: GXNode
  ): GXTrack {
    const gxTrack = new GXTrack()
    gxTrack.nodeId = gxNode.gxId;
    gxTrack.nodeIdPath = gxNode.gxIdPath;
    gxTrack.templateItem = gxTemplateContext.gxTemplateItem;
    gxTrack.trackParams = gxNode.gxTemplateNode.getEventData(gxTemplateData);
    // gxTrack.view = gxNode.gxView;
    return gxTrack;
  }
}
