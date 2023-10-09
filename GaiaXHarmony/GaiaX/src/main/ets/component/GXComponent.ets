import GXMeasureSize from '../gaiax/GXMeasureSize';
import { gxRawFileTemplateSource } from '../gaiax/GXRegisterCenter';
import GXTemplateData from '../gaiax/GXTemplateData';
import GXTemplateItem from '../gaiax/GXTemplateItem';
import createView from '../gaiax/GXViewBuilder';
import { loadTemplate } from '../utils/RawFileUtils';

@Component
export struct GXComponent {
  gxTemplateItem: GXTemplateItem;
  gxTemplateData: GXTemplateData;
  gxMeasureSize: GXMeasureSize;
  @State reload: boolean = false

  aboutToAppear() {
    try {
      loadTemplate(getContext(), this.gxTemplateItem, (error, template) => {
        if (template != null) {
          gxRawFileTemplateSource.addData(this.gxTemplateItem.templateId, template);
          this.reload = true;
        } else {
          console.log(`load template error ${error}`);
        }
      });
    } catch (error) {
      console.error(`callback getRawFileContent failed, error code: ${error.code}, message: ${error.message}.`)
    }
  }

  build() {
    if (this.reload) {
      createView(this.gxTemplateItem, this.gxTemplateData, this.gxMeasureSize)
    } else {
      Text("GXTemplate loading")
    }
  }
}

