import GXGenerate from './GXGenerate';
import GXMeasureSize from './GXMeasureSize';
import GXTemplateContext from './GXTemplateContext';
import GXTemplateData from './GXTemplateData';
import GXTemplateItem from './GXTemplateItem';
import GXTemplateNode from './GXTemplateNode';

@Builder
export default function createViewForEngine(
    gxTemplateItem: GXTemplateItem,
    gxTemplateData: GXTemplateData,
    gxMeasureSize: GXMeasureSize,
    gxVisualTemplateNode?: GXTemplateNode
) {
  createViewForRender(GXGenerate.createContext(gxTemplateItem, gxTemplateData, gxMeasureSize, gxVisualTemplateNode))
}

@Builder
export function createViewForRender(gxTemplateContext?: GXTemplateContext) {
  if (gxTemplateContext == null) {
    Text("GXView is null")
  } else {
    Text("createGXView")
  }

}
