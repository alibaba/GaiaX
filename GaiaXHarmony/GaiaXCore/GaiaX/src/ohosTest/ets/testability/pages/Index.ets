@Entry
@Component
struct Index {
  @State message: string = 'Hello World'

  build() {
    Row() {
      Column() {
        Text(this.message)
          .fontSize(50)
          .fontWeight(FontWeight.Bold)
        Text("Next")
          .fontSize(50)
          .margin({top:20})
          .fontWeight(FontWeight.Bold)
        Text("after click")
          .fontSize(50)
          .margin({top:20})
          .fontWeight(FontWeight.Bold)
      }
      .width('100%')
    }
    .height('100%')
  }
}