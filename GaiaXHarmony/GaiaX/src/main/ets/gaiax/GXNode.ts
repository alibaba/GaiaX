import { GXJSONObject } from './GXJson';
import GXTemplateContext from './GXTemplateContext';
import GXTemplateItem from './GXTemplateItem';
import GXTemplateNode from './GXTemplateNode';

export class GXNode {
  gxTemplateNode: GXTemplateNode;

  gxId: string = '';

  gxIdPath: string = '';

  gxChildren: Array<GXNode> = null;

  gxChildTemplateItems?: Map<GXTemplateItem, GXTemplateNode>;

  gxData?: GXJSONObject;

  setIdPath(
    gxTemplateContext: GXTemplateContext,
    gxLayer: GXJSONObject,
    gxParentNode?: GXNode
  ) {
    this.gxId = gxLayer['id'];
    if (gxParentNode != null) {
      if (this.gxIdPath.length != 0) {
        this.gxIdPath = `${gxParentNode.gxIdPath}@${this.gxIdPath}@${this.gxId}`
      } else {
        this.gxIdPath = `${gxParentNode.gxIdPath}@${this.gxId}`
      }
    } else {
      if (this.gxIdPath.length != 0) {
        this.gxIdPath = `${this.gxIdPath}@${this.gxId}`
      } else {
        if (gxTemplateContext.gxTemplateItem.templatePrefixId != null) {
          this.gxIdPath = `${gxTemplateContext.gxTemplateItem.templatePrefixId}-${this.gxId}`
        } else {
          this.gxIdPath = this.gxId
        }

      }
    }
  }

  static create(): GXNode {
    return new GXNode();
  }
}