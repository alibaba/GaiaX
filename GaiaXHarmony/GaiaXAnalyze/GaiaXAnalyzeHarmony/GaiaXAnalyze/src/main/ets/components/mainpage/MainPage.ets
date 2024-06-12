import { hilog } from '@kit.PerformanceAnalysisKit';
import testNapi from 'libgaiaxanalyze.so';

@Component
export struct MainPage {
  @State message: string = 'Hello World';

  build() {
    Row() {
      Column() {
        Text(this.message)
          .fontSize(50)
          .fontWeight(FontWeight.Bold)
          .onClick(() => {
            hilog.info(0x0000, 'testTag', 'Test NAPI 2 + 3 = %{public}d');
          })
      }
      .width('100%')
    }
    .height('100%')
  }
}