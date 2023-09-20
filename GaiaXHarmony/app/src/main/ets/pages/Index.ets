import { GaiaXComponent } from 'gaiax'

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
        GaiaXComponent()
      }
      .width('100%')

    }
    .height('100%')
  }
}