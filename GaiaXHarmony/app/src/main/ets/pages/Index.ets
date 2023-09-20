import { gaiax } from 'gaiax'

@Entry
@Component
struct Index {
  @State message: string = 'Hello GaiaX Demo '

  build() {
    Row() {
      Column() {
        Text(this.message + gaiax(1, 1))
          .fontSize(28)
          .fontWeight(FontWeight.Bold)
      }
      .width('100%')
    }
    .height('100%')
  }
}