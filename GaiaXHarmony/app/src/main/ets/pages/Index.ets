import { GXComponent } from 'gaiax'
import GXParams from './Modal'

@Entry
@Component
struct Index {
  @State message: string = 'Hello GaiaX Demo'

  build() {
    Row() {
      Column() {
        Text(this.message)
          .fontSize(28)
          .fontWeight(FontWeight.Bold)
        GXComponent({
          gxTemplateItem: GXParams.getGXTemplateItem(),
          gxTemplateData: GXParams.getGXTemplateData(),
          gxMeasureSize: GXParams.getGXMeasureSize()
        })
      }
      .width('100%')

    }
    .height('100%')
  }
}