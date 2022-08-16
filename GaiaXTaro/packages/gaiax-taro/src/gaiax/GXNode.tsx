import { ReactNode } from "react";
import { GXJSONObject } from "./GXJson";
import { GXTemplateItem } from "./GXTemplateEngine";
import GXTemplateNode from "./GXTemplateNode";

export class GXNode {

    gxTemplateNode: GXTemplateNode;

    gxId: string = '';

    gxIdPath: string = '';

    gxChildren: Array<GXNode> = null;

    gxView?: ReactNode = null;

    gxChildTemplateItems?: Map<GXTemplateItem, GXTemplateNode>;

    setIdPath(gxLayer: GXJSONObject, gxParentNode?: GXNode) {
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
                this.gxIdPath = this.gxId
            }
        }
    }
    static create(): GXNode {
        return new GXNode();
    }
}