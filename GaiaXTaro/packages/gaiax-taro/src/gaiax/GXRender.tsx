import { ReactNode } from 'react';
import GXTemplateContext from './GXTemplateContext';
import GXViewTreeCreator from "./GXViewTreeCreator";

export default class GXRender {

    private viewCreator = new GXViewTreeCreator();

    createView(gxTemplateContext: GXTemplateContext): ReactNode {

        gxTemplateContext.rootView = this.viewCreator.build(gxTemplateContext);

        return gxTemplateContext.rootView;
    }
}