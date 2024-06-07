import { MainPage } from 'gaiaxcoreharmony';
import GXTemplateManager from 'gaiaxcoreharmony/src/main/ets/template/GXTemplateManager';
import GXTemplateItem from 'gaiaxcoreharmony/src/main/ets/context/GXTemplateItem';
import { router } from '@kit.ArkUI';


@Entry
@Component
struct Index {
  @State message: string = 'Hello World';
  nodes = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'];

  aboutToAppear(): void {

  }

  build() {
    Column() {
      Text(this.message)
        .fontSize(20)
        .fontWeight(FontWeight.Bold)
        .onAppear(() => {
          console.info('组件出现啦')
        })
        .onClick(() => {
          this.jump()
        })
        .height('10%')
      // MainPage()
      // 创建子List
      List({space: 10}) {
        ForEach(this.nodes, (node: string) => {
          ListItem() {
            MainPage()
          }
        })
      }
      .width('100%')
      .height('90%')
      .listDirection(Axis.Vertical)
    }
    .width('100%')
    .backgroundColor('white')
  }

  jump() {
    router.pushUrl({
      url: "pages/detail",
      params: {
        showId: "1409686"
      }
    }, router.RouterMode.Standard)
  }
}